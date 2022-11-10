package com.example.gerdapp.ui.chart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChartAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
   var fragments: ArrayList<Fragment> = arrayListOf(
        DailyChartFragment(),
        WeeklyChartFragment(),
        MonthlyChartFragment()
    )

//    override fun getCount(): Int {
//        return fragments.size
//    }
//
//    override fun getItem(position: Int): Fragment {
//        return fragments[position]
//    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}