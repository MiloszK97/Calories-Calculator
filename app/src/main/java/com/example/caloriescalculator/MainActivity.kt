package com.example.caloriescalculator

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.utils.CLICKED_MEAL_POSITION
import com.example.caloriescalculator.utils.DEFAULT_MEALS

class MainActivity : AppCompatActivity() {

    companion object{
        private const val REQUEST_CODE = 1909
    }

    private lateinit var clRoot: CoordinatorLayout
    private lateinit var rvMeals : RecyclerView
    private lateinit var rvAdapter : DisplayMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

/*      val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")
        To logout -> FirebaseAuth.getInstance().signOut()
        startActivity()
        finish()
        */

        clRoot = findViewById(R.id.clRoot)
        rvMeals = findViewById(R.id.rvMeals)

        setupMainMealsScreen()
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
