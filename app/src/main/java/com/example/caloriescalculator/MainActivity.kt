package com.example.caloriescalculator

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.api.RetrofitInstance
import com.example.caloriescalculator.utils.CLICKED_MEAL_POSITION
import com.example.caloriescalculator.utils.DEFAULT_MEALS
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    companion object{
        private const val REQUEST_CODE = 1909
        private const val TAG = "MainActivity"
    }

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userId: String

    private lateinit var tvProfileName: TextView
    private lateinit var tvTodayDate: TextView
    private lateinit var cvYesterday: CardView
    private lateinit var cvToday: CardView
    private lateinit var cvTomorrow: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var rvMeals : RecyclerView
    private lateinit var rvAdapter : DisplayMealsAdapter
    private lateinit var mainProgessBar: ProgressBar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userId = intent.getStringExtra("user_id").toString()
        val emailId = intent.getStringExtra("email_id")
        /*To logout -> FirebaseAuth.getInstance().signOut()
        startActivity()
        finish()
        */
        tvTodayDate = findViewById(R.id.tvTodayDate)
        mainProgessBar = findViewById(R.id.pbMain)
        mainProgessBar.isVisible = false
        var today = setupDate()
        cvYesterday = findViewById(R.id.cvYesterday)
        cvToday = findViewById(R.id.cvToday)
        cvTomorrow = findViewById(R.id.cvTomorrow)
        cvYesterday.setOnClickListener {
            tvTodayDate.text = "${today.minusDays(1).dayOfMonth} ${today.minusDays(1).month.toString().lowercase().capitalize()}"
            val formatter = DateTimeFormatter.ISO_DATE
            today = today.minusDays(1)
            Log.d(TAG, "Date to Api call: ${today.format(formatter)}")
            createMealsMacroList(today.format(formatter), userId)
            //download from db current day
            //callToApiWithDate(today)
            //Toast.makeText(this@MainActivity, "You calling for the day $today", Toast.LENGTH_LONG).show()
        }
        cvTomorrow.setOnClickListener {
            tvTodayDate.text = "${today.plusDays(1).dayOfMonth} ${today.plusDays(1).month.toString().lowercase().capitalize()}"
            today = today.plusDays(1)
            val formatter = DateTimeFormatter.ISO_DATE
            Log.d(TAG, "Date to Api call: ${today.format(formatter)}")
            createMealsMacroList(today.format(formatter), userId)
            //download from db current day
            //callToApiWithDate(today)
            //Toast.makeText(this@MainActivity, "You calling for the day $today", Toast.LENGTH_LONG).show()
        }
        rvMeals = findViewById(R.id.rvMeals)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        val drawerHeaderView = navView.getHeaderView(0)
        tvProfileName = drawerHeaderView.findViewById(R.id.tvProfileName)
        tvProfileName.text = userId

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        //createMealsMacroList("2022-02-21", userId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miItem1 -> {
                    Toast.makeText(
                        applicationContext,
                        "Clicked on drawer item: ${it.itemId}",
                        Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.miItem2 -> {
                    Toast.makeText(
                        applicationContext,
                        "Clicked on drawer item: ${it.itemId}",
                        Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.miItem3 -> {
                    Toast.makeText(
                        applicationContext,
                        "Clicked on drawer item: ${it.itemId}",
                        Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.miItem4 -> {
                    Toast.makeText(
                        applicationContext,
                        "Clicked on profile settings.",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupDate(): LocalDateTime{
        val formatter = DateTimeFormatter.ISO_DATE
        val current = LocalDateTime.now()
        tvTodayDate.text = "${current.dayOfMonth} ${current.month.toString().lowercase().capitalize()}"
        //createMealsMacroList(current.format(formatter), userId)
        createMealsMacroList("2022-02-21", userId)
        return current
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupMainMealsScreen(data: MutableList<List<Double>>) {
        val meals = createMeals(data)
        rvAdapter = DisplayMealsAdapter(this, meals, object: DisplayMealsAdapter.MealClickListener{
            override fun onMealClicked(position: Int) {
                val intent = Intent(this@MainActivity, MealActivity::class.java)
                intent.putExtra(CLICKED_MEAL_POSITION, position)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }, object : DisplayMealsAdapter.AddProductClickListener{
             override fun onAddProductClicked(position: Int) {
                 val intent = Intent(this@MainActivity, SearchProduct::class.java)
                 intent.putExtra(CLICKED_MEAL_POSITION, position)
                 startActivity(intent)
             }
         }, object : DisplayMealsAdapter.OptionsButtonClickListener{
            override fun onOptionsButtonClicked(position: Int) {
                val optionsDialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.meal_options_dialog, null)
                showAlertDialog("Meal Options", optionsDialogView, View.OnClickListener {
                    Toast.makeText(this@MainActivity, "You tapped on OK button", Toast.LENGTH_SHORT).show()
                })
            }
        })
        rvMeals.adapter = rvAdapter
        rvMeals.layoutManager = LinearLayoutManager(this)
    }

    private fun createMeals(data: MutableList<List<Double>>): List<Meal> {
        val meals = mutableListOf<Meal>()
        for (i in 0..4) meals.add(Meal(getString(DEFAULT_MEALS[i]), data[i][0].toInt(), data[i][1], data[i][2], data[i][3]))
        return meals
    }


    private fun showAlertDialog(title : String, view : View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Ok"){_,_->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun createMealsMacroList(date: String, userId: String) {
        val defaultListWithZeroValues: List<List<Double>> = listOf(
            listOf(0.0, 0.0, 0.0, 0.0),
            listOf(0.0, 0.0, 0.0, 0.0),
            listOf(0.0, 0.0, 0.0, 0.0),
            listOf(0.0, 0.0, 0.0, 0.0),
            listOf(0.0, 0.0, 0.0, 0.0)
        )
        mainProgessBar.isVisible = true
        RetrofitInstance.api.getMealTotKcal("dsf34t4t34tdfg", 0, date).enqueue(object: Callback<List<List<Double>>> {
            override fun onResponse(call: Call<List<List<Double>>>, response: Response<List<List<Double>>>) {
                for (item in response.body()!!){
                    if (item[0] == null) {
                        setupMainMealsScreen(defaultListWithZeroValues as MutableList<List<Double>>)
                    }else{
                        setupMainMealsScreen(response.body()!! as MutableList<List<Double>>)
                    }
                }
                mainProgessBar.isVisible = false
            }

            override fun onFailure(call: Call<List<List<Double>>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                mainProgessBar.isVisible = false
            }

        })

    }
}

