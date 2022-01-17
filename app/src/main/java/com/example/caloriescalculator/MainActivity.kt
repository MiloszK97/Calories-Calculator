package com.example.caloriescalculator

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.utils.CLICKED_MEAL_POSITION
import com.example.caloriescalculator.utils.DEFAULT_MEALS
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    companion object{
        private const val REQUEST_CODE = 1909
    }

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var tvProfileName: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var rvMeals : RecyclerView
    private lateinit var rvAdapter : DisplayMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")
        /*To logout -> FirebaseAuth.getInstance().signOut()
        startActivity()
        finish()
        */
        rvMeals = findViewById(R.id.rvMeals)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        val drawerHeaderView = navView.getHeaderView(0)
        tvProfileName = drawerHeaderView.findViewById(R.id.tvProfileName)
        tvProfileName.text = userId

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setupMainMealsScreen()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupMainMealsScreen() {
        val meals = createMeals()
        rvAdapter = DisplayMealsAdapter(this, meals, object: DisplayMealsAdapter.MealClickListener{
            override fun onMealClicked(position: Int) {
                val intent = Intent(this@MainActivity, MealActivity::class.java)
                intent.putExtra(CLICKED_MEAL_POSITION, position)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }, object : DisplayMealsAdapter.AddProductClickListener{
             override fun onAddProductClicked(position: Int) {
                 val intent = Intent(this@MainActivity, SearchProduct::class.java)
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

    private fun createMeals(): List<Meal> {
        val meals = mutableListOf<Meal>()
        for (i in 0..4) meals.add(Meal(getString(DEFAULT_MEALS[i]), 1234, 111.1, 56.9, 199.4))
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
}
