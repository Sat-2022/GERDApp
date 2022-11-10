package com.example.gerdapp.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gerdapp.MainActivity
import com.example.gerdapp.databinding.FragmentChartBinding
import com.google.android.material.tabs.TabLayoutMediator

class ChartFragment: Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.VISIBLE
    private var actionbarTitleEnable = true

    private lateinit var pagerAdapter: ChartAdapter

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)

        mainActivity.setActionBarTitleEnable(actionbarTitleEnable)
        mainActivity.setActionBarTitle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        pagerAdapter = ChartAdapter(childFragmentManager, lifecycle)
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = pagerAdapter

        val title: ArrayList<String> = arrayListOf("日", "週", "月")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = title[position]
        }.attach()
    }
}