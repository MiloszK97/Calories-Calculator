package com.example.caloriescalculator.profile_setup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.caloriescalculator.R
import com.example.caloriescalculator.utils.PROFILE_INFO_LABELS

class ActivitySetupProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_profile)

        /*val adapter = ViewPagerAdapter(PROFILE_INFO_LABELS)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = adapter*/
    }
}