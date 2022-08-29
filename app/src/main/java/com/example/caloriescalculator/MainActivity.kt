package com.example.caloriescalculator

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.api.RetrofitInstance
import com.example.caloriescalculator.model.ProfileResponse
import com.example.caloriescalculator.profile_setup.ActivitySetupProfile
import com.example.caloriescalculator.utils.CLICKED_MEAL_POSITION
import com.example.caloriescalculator.utils.DEFAULT_MEALS
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    companion object{
        private const val REQUEST_CODE = 1909
        private const val TAG = "MainActivity"
        private const val USER_ID_EXT = "USER_ID"
        private const val DATE_TO_SHOW = "DATE_TO_SHOW"
    }

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userId: String

    private lateinit var today: LocalDateTime
    private var profileCalories by Delegates.notNull<Double>()
    private var profileProteins by Delegates.notNull<Double>()
    private var profileFat by Delegates.notNull<Double>()
    private var profileCarbs by Delegates.notNull<Double>()

    private lateinit var tvProfileName: TextView
    private lateinit var tvTodayDate: TextView
    private lateinit var tvCaloriesTotal: TextView
    private lateinit var tvProteinsTotal: TextView
    private lateinit var tvFatTotal: TextView
    private lateinit var tvCarbsTotal: TextView
    private lateinit var cvYesterday: CardView
    private lateinit var cvToday: CardView
    private lateinit var cvTomorrow: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var rvMeals : RecyclerView
    private lateinit var rvAdapter : DisplayMealsAdapter
    private lateinit var mainProgressBar: ProgressBar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //userId = intent.getStringExtra("user_id").toString()
        userId = "sERSGZArqEP4CvSnmrhVkFI4w8w1"
        //userId = "MJQN9CWaEWO2W0KhKFl8ts3rfF12"
        //userId = "2gclX8w4VYdRHPPqW3ITY8BqIBi2"

        tvCaloriesTotal = findViewById(R.id.tvCaloriesTotal)
        tvProteinsTotal = findViewById(R.id.tvProteinsTotal)
        tvFatTotal = findViewById(R.id.tvFatTotal)
        tvCarbsTotal = findViewById(R.id.tvCarbsTotal)

        tvTodayDate = findViewById(R.id.tvTodayDate)
        mainProgressBar = findViewById(R.id.pbMain)
        mainProgressBar.isVisible = false
        today = setupDate()
        provideMacroData(userId, today.format(DateTimeFormatter.ISO_DATE))
        cvYesterday = findViewById(R.id.cvYesterday)
        cvToday = findViewById(R.id.cvToday)
        cvTomorrow = findViewById(R.id.cvTomorrow)
        cvYesterday.setOnClickListener {
            tvTodayDate.text = "${today.minusDays(1).dayOfMonth} ${today.minusDays(1).month.toString().lowercase().capitalize()}"
            val formatter = DateTimeFormatter.ISO_DATE
            today = today.minusDays(1)
            provideMacroData(userId, today.format(formatter))
            createMealsMacroList(today.format(formatter), userId)
        }
        cvTomorrow.setOnClickListener {
            tvTodayDate.text = "${today.plusDays(1).dayOfMonth} ${today.plusDays(1).month.toString().lowercase().capitalize()}"
            today = today.plusDays(1)
            val formatter = DateTimeFormatter.ISO_DATE
            provideMacroData(userId, today.format(formatter))
            createMealsMacroList(today.format(formatter), userId)
        }
        rvMeals = findViewById(R.id.rvMeals)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        val drawerHeaderView = navView.getHeaderView(0)
        tvProfileName = drawerHeaderView.findViewById(R.id.tvProfileName)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miItem1 -> {
                    val intent = Intent(applicationContext, ActivitySetupProfile::class.java)
                    intent.putExtra(USER_ID_EXT, userId)
                    startActivity(intent)
                }

                R.id.miItem2 -> {
                    val formatter = DateTimeFormatter.ISO_DATE
                    val intent = Intent(applicationContext, ActivityWaterControl::class.java)
                    intent.putExtra(USER_ID_EXT, userId)
                    intent.putExtra(DATE_TO_SHOW, today.format(formatter))
                    startActivity(intent)
                }

                R.id.miItem3 -> {
                    val creditsDialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.credits_dialog, null)
                    showAlertDialog("About:", creditsDialogView, View.OnClickListener {

                    })
                }

                R.id.miItem4 -> {
                    //FirebaseAuth.getInstance().signOut()
                    //finish()
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
        createMealsMacroList(current.format(formatter), userId)
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
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onMealClicked(position: Int) {
                val intent = Intent(this@MainActivity, MealActivity::class.java)
                val formatter = DateTimeFormatter.ISO_DATE
                intent.putExtra(DATE_TO_SHOW, today.format(formatter))
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
        val defaultListWithZeroValues: List<Double> = listOf(
            0.0, 0.0, 0.0, 0.0
        )
        val listToShow: MutableList<List<Double>> = mutableListOf()
        mainProgressBar.isVisible = true
        RetrofitInstance.api.getMealTotKcal(userId, 0, date).enqueue(object: Callback<List<List<Double>>> {
            override fun onResponse(call: Call<List<List<Double>>>, response: Response<List<List<Double>>>) {
                    for(i in 0 until 5){
                        if (response.body()!![i][0] == null) {
                            listToShow.add(i, defaultListWithZeroValues)
                            //setupMainMealsScreen(defaultListWithZeroValues as MutableList<List<Double>>)
                        }else{
                            listToShow.add(i, response.body()!![i])
                            //setupMainMealsScreen(response.body()!! as MutableList<List<Double>>)
                        }
                    }
                setupMainMealsScreen(listToShow)
                mainProgressBar.isVisible = false
            }

            override fun onFailure(call: Call<List<List<Double>>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                mainProgressBar.isVisible = false
            }

        })
    }

    private fun provideMacroData(userID: String, date: String){
        RetrofitInstance.api.getProfile(userID).enqueue(object: Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.body() != null){
                    tvProfileName.text = "${response.body()!!.profileNickname}"
                    profileCalories = response.body()!!.profileTotCal.toDouble()
                    profileProteins = response.body()!!.profileTotProt
                    profileFat = response.body()!!.profileTotFat
                    profileCarbs = response.body()!!.profileTotCarbs
                    showFullDayMacro(userID, date, profileCalories, profileProteins, profileFat, profileCarbs, newUser = false)

                }else{
                    tvProfileName.text = ""
                    profileCalories = 0.0
                    profileProteins = 0.0
                    profileFat = 0.0
                    profileCarbs = 0.0
                    showFullDayMacro(userID, date, profileCalories, profileProteins, profileFat, profileCarbs, newUser = true)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showFullDayMacro(userID: String, date: String, totCal: Double, totProt: Double, totFat: Double, totCarbs: Double, newUser: Boolean) {
        if (newUser){
            tvCaloriesTotal.text = "Calories: \n 0/${totCal.toInt()}kcal"
            tvProteinsTotal.text = "Proteins: \n 0/${totProt}g"
            tvFatTotal.text = "Fat: \n 0/${totFat}g"
            tvCarbsTotal.text = "Carbs: \n 0/${totCarbs}g"
        }

        RetrofitInstance.api.getFullDayMacro(userID, date).enqueue(object: Callback<List<Double>>{
            override fun onResponse(call: Call<List<Double>>, response: Response<List<Double>>) {
                if (response.body()!![0] != null){
                    tvCaloriesTotal.text = "Calories: \n ${response.body()!![0].toInt()}/${totCal.toInt()}kcal"
                    tvProteinsTotal.text = "Proteins: \n ${response.body()!![1]}/${totProt}g"
                    tvFatTotal.text = "Fat: \n ${response.body()!![2]}/${totFat}g"
                    tvCarbsTotal.text = "Carbs: \n ${response.body()!![3]}/${totCarbs}g"
                }else{
                    tvCaloriesTotal.text = "Calories: \n 0/${totCal.toInt()}kcal"
                    tvProteinsTotal.text = "Proteins: \n 0/${totProt}g"
                    tvFatTotal.text = "Fat: \n 0/${totFat}g"
                    tvCarbsTotal.text = "Carbs: \n 0/${totCarbs}g"
                }
            }

            override fun onFailure(call: Call<List<Double>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }
}

