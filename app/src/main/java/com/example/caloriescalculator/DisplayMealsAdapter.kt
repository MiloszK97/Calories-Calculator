package com.example.caloriescalculator

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DisplayMealsAdapter(
    private val context: Context,
    private val meals: List<Meal>,
    private val mealClickListener: MealClickListener,
    private val addProductClickListener: AddProductClickListener,
    private val optionsButtonClicked: OptionsButtonClickListener)
    : RecyclerView.Adapter<DisplayMealsAdapter.ViewHolder>(){

    companion object{
        private const val TAG = "DisplayMealsAdapter"
    }

    interface MealClickListener{
        fun onMealClicked(position: Int)
    }

    interface AddProductClickListener{
        fun onAddProductClicked(position: Int)
    }

    interface OptionsButtonClickListener{
        fun onOptionsButtonClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_meal_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = meals[position]
        holder.bind(meal)
    }

    override fun getItemCount() = meals.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val cvMeal: CardView = itemView.findViewById(R.id.cvMeal)
        private val ibMealOptions: ImageButton = itemView.findViewById(R.id.ibMealOptions)
        private val fabAddProduct: FloatingActionButton = itemView.findViewById(R.id.fabAddProduct)
        private val tvOneMealView: TextView = itemView.findViewById(R.id.tvOneMealView)
        private val tvSumCalories: TextView = itemView.findViewById(R.id.tvSumCalories)
        private val tvSumProteins: TextView = itemView.findViewById(R.id.tvSumProteins)
        private val tvSumFat: TextView = itemView.findViewById(R.id.tvSumFat)
        private val tvSumCarbs: TextView = itemView.findViewById(R.id.tvSumCarbs)
        fun bind(meal: Meal) {
            tvOneMealView.text = meal.name
            tvSumCalories.text = "${meal.sumCalories} kcal"
            tvSumProteins.text = "${meal.sumProteins} g"
            tvSumFat.text = "${meal.sumFat} g"
            tvSumCarbs.text = "${meal.sumCarbs} g"

            cvMeal.setOnClickListener {
                mealClickListener.onMealClicked(position)
            }

            ibMealOptions.setOnClickListener {
                optionsButtonClicked.onOptionsButtonClicked(position)
                Log.i(TAG, "Clicked on three dots")
            }

            fabAddProduct.setOnClickListener {
                addProductClickListener.onAddProductClicked(position)
                Log.i(TAG, "Clicked on add product FAB")
            }
        }
    }
}
