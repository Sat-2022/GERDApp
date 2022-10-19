package com.example.gerdapp.ui.chart

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.adapter.NotificationCardItemAdapter
import com.example.gerdapp.data.*
import com.example.gerdapp.databinding.FragmentChartBinding
import com.example.gerdapp.ui.main.MainFragment
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

        binding.apply {

        }
    }
//
//    private fun getSymptomCurrentApi(): Thread {
//        return Thread {
//            val url = URL(getString(R.string.get_symptoms_record_url, getString(R.string.server_url), MainFragment.User.caseNumber, "19110101", "19110101", "DESC"))
//            val connection = url.openConnection() as HttpURLConnection
//
//            if(connection.responseCode == 200) {
//                val inputSystem = connection.inputStream
//                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
//                val type: java.lang.reflect.Type? = object : TypeToken<List<SymptomCurrent>>() {}.type
//                val symptomData: List<SymptomCurrent> = Gson().fromJson(inputStreamReader, type)
//
//                try {
//                    symptomCurrent = symptomData.first()
//                } catch (e: Exception) {
//                    // TODO: Catch exception when no data
//                }
//
//                inputStreamReader.close()
//                inputSystem.close()
//                Log.e("API Connection", "$symptomCurrent")
//            } else
//                Log.e("API Connection", "failed")
//        }
//    }



}