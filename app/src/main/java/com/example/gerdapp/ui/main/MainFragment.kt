package com.example.gerdapp.ui.main

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.adapter.CardItemAdapter
import com.example.gerdapp.data.Result
import com.example.gerdapp.databinding.FragmentMainBinding
import com.example.gerdapp.viewmodel.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var barChart: BarChart

    private var symptomsNum = 10

    private val testBarChart = true

    private var bottomNavigationViewVisibility = View.VISIBLE

    private lateinit var recyclerView: RecyclerView

    private var currentResult = ArrayList<Result>()

    private val sleepViewModel: SleepViewModel by activityViewModels {
        SleepViewModelFactory(
            (activity?.application as BasicApplication).sleepDatabase.sleepDao()
        )
    }

    private val resultViewModel: ResultViewModel by activityViewModels {
        ResultViewModelFactory(
            (activity?.application as BasicApplication).resultDatabase.resultDao()
        )
    }

//    private val othersViewModel: OthersViewModel by activityViewModels {
//        OthersViewModelFactory(
//            (activity?.application as BasicApplication).othersDatabase.othersDao()
//        )
//    }
//
//    private val foodViewModel: FoodViewModel by activityViewModels {
//        FoodViewModelFactory(
//            (activity?.application as BasicApplication).foodDatabase.foodDao()
//        )
//    }

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        barChart = binding.chartCard.barChart
//        currentResult = setResultData(1)
        initBarChart()
//        initBarChartData()
//        initResultData()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var text = "text no data"
        try {
            resultViewModel.getResultById(1).observe(this.viewLifecycleOwner) {
                text = it.symptomAcidReflux.toString()
            }
        } catch(e: NullPointerException) {
            text = "No current data"
        }

        // initBarChartData()



        recyclerView = binding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        var sleepRecentData = "sleep data"

//        sleepViewModel.getRecentRecord().observe(this.viewLifecycleOwner) {
//            sleepRecentData = it.startTime
//        }
//        var othersRecentData = "others data"
//        othersViewModel.getRecentRecord().observe(this.viewLifecycleOwner) {
//            othersRecentData = it.startTime
//        }
//        var foodRecentData = ""
//        foodViewModel.getRecentRecord().observe(this.viewLifecycleOwner) {
//            foodRecentData = it.startTime
//        }

        val adapter = CardItemAdapter({ cardItem ->
            val action = when (cardItem.stringResourceId) {
                R.string.symptoms -> MainFragmentDirections.actionMainFragmentToSymptomsFragment()
                R.string.medicine -> MainFragmentDirections.actionMainFragmentToDrugRecordFragment()
                R.string.food -> MainFragmentDirections.actionMainFragmentToFoodFragment()
                R.string.sleep -> MainFragmentDirections.actionMainFragmentToSleepFragment()
                R.string.others -> MainFragmentDirections.actionMainFragmentToOthersFragment()
                else -> MainFragmentDirections.actionMainFragmentSelf()
            }
            findNavController().navigate(action)
        }) { cardItem ->
            val recentRecord = when (cardItem.stringResourceId) {
                R.string.symptoms -> ""
//                R.string.food -> foodRecentData
                R.string.sleep -> {
//                    try {
//                        sleepViewModel.getRecentRecord().value?.startTime
//                    } catch (e: NullPointerException) {
//                        "No Current Data"
//                    }
                    ""
                }
//                R.string.others -> othersRecentData
                else -> ""
            }
//            text = recentRecord.toString()
            recentRecord
        }

        recyclerView.adapter = adapter

        binding.apply {
            val calendar = Calendar.getInstance()
            val current = calendar.time
            val formatDate =
                SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
            val currentDate = formatDate.format(current)
            chartCard.chartDate.text = currentDate.toString()
            testButton.setOnClickListener { view ->
                Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    private fun initBarChart() {
        // set data
        // initBarChartData()

        if(testBarChart) setRandomResult()

        barChart.setBackgroundColor(Color.WHITE)
        barChart.description.isEnabled = false
//        chart.setTouchEnabled(false)
//        chart.isDragEnabled = false


        // add animation
        barChart.animateY(1400)

//        val l: Legend = chart.legend
//        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//        l.orientation = Legend.LegendOrientation.HORIZONTAL
//        l.setDrawInside(false)
//        l.typeface = Typeface.DEFAULT //
//        l.xEntrySpace = 7f
//        l.yEntrySpace = 5f
//        //l.form = Legend.LegendForm.LINE
//        l.textColor = Color.BLACK
        // remove legend
        barChart.legend.isEnabled = false

        val xAxis: XAxis = barChart.xAxis
        xAxis.typeface = Typeface.DEFAULT
        xAxis.textSize = 12f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.setDrawGridLines(false)
//        xAxis.textColor = Color.BLACK
//        xAxis.setDrawGridLines(true)
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setLabelCount(5, true)

        val mActivities = arrayOf(
            getString(R.string.cough),
            getString(R.string.heart_burn),
            getString(R.string.acid_reflux),
            getString(R.string.chest_pain),
            getString(R.string.sour_mouth),
            getString(R.string.hoarseness),
            getString(R.string.appetite_loss),
            getString(R.string.stomach_gas),
            getString(R.string.cough_night),
            getString(R.string.acid_reflux_night)
        )
        val formatter = IAxisValueFormatter { value, axis ->
            mActivities[value.toInt() % mActivities.size]
        }
        xAxis.valueFormatter = formatter

        val yAxis = barChart.axisLeft
        yAxis.axisMaximum = 5f
        yAxis.axisMinimum = 0f
        yAxis.setLabelCount(4, false)
        yAxis.setDrawAxisLine(false)
        //yAxis.setDrawLabels(false)
        barChart.axisRight.isEnabled = false
    }

    private fun initBarChartData() {
        val entries: ArrayList<BarEntry> = ArrayList()
        addBarEntry(entries, 0, currentResult.first().symptomCough)
        addBarEntry(entries, 1, currentResult.first().symptomHeartBurn)
        addBarEntry(entries, 2, currentResult.first().symptomAcidReflux)
        addBarEntry(entries, 3, currentResult.first().symptomHeartBurn)
        addBarEntry(entries, 4, currentResult.first().symptomHeartBurn)
        addBarEntry(entries, 5, currentResult.first().symptomHeartBurn)
        addBarEntry(entries, 6, currentResult.first().symptomHeartBurn)
        addBarEntry(entries, 7, currentResult.first().symptomHeartBurn)
        addBarEntry(entries, 8, currentResult.first().symptomHeartBurn)
        addBarEntry(entries, 9, currentResult.first().symptomHeartBurn)

        val barDataSet = BarDataSet(entries, "")
        barDataSet.color = Color.BLUE

        val barData = BarData(barDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        barData.setDrawValues(true)

        barChart.data = barData
        barChart.invalidate()
    }

    private fun initResultData() {
        resultViewModel.addResultRecord(
            "2022/07/20 03:01",
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
            (Math.random() * 5f).toInt(),
        )
    }

    private fun addBarEntry(entries: ArrayList<BarEntry>, index: Int, data: Int?) {
        if (data == null) entries.add(BarEntry(index.toFloat(), 0f))
        else entries.add(BarEntry(index.toFloat(), data.toFloat()))
    }

    private fun setRandomResult() {
        val entries: ArrayList<BarEntry> = ArrayList()
        for(i in 0 until symptomsNum) {
            addBarEntry(entries, i, (0..5).random())
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.color = Color.BLUE

        val barData = BarData(barDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        barData.setDrawValues(true)

        barChart.data = barData
        barChart.invalidate()
    }

    private fun setResultData(id: Int): ArrayList<Result> {
        val recentResults: LiveData<List<Result>> = resultViewModel.getRecentResult()
        val data: List<Result> = recentResults.value!!
        currentResult.add(Result(data.first().id, data.first().time,
            data.first().symptomCough, data.first().symptomHeartBurn, data.first().symptomAcidReflux, data.first().symptomChestHurt,
            data.first().symptomAcidMouth, data.first().symptomHoarse, data.first().symptomLoseAppetite, data.first().symptomStomachGas,
            data.first().symptomCoughNight, data.first().symptomAcidRefluxNight))
//        if (data != null) {
//            addBarEntry(0, data.symptomCough)
//            addBarEntry(1, data.symptomHeartBurn)
//            addBarEntry(2, data.symptomAcidReflux)
//            addBarEntry(3, data.symptomHeartBurn)
//            addBarEntry(4, data.symptomHeartBurn)
//            addBarEntry(5, data.symptomHeartBurn)
//            addBarEntry(6, data.symptomHeartBurn)
//            addBarEntry(7, data.symptomHeartBurn)
//            addBarEntry(8, data.symptomHeartBurn)
//            addBarEntry(9, data.symptomHeartBurn)
//        } else {
//            for (i in 0 until symptomsNum) addBarEntry(i, 5)
//        }
//        return entries

        //        resultViewModel.getResultById(id).observe(this.viewLifecycleOwner) {
//            addBarEntry(entries, 0, it.symptomCough)
//            addBarEntry(entries, 1, it.symptomHeartBurn)
//            addBarEntry(entries, 2, it.symptomAcidReflux)
//            addBarEntry(entries, 3, it.symptomHeartBurn)
//            addBarEntry(entries, 4, it.symptomHeartBurn)
//            addBarEntry(entries, 5, it.symptomHeartBurn)
//            addBarEntry(entries, 6, it.symptomHeartBurn)
//            addBarEntry(entries, 7, it.symptomHeartBurn)
//            addBarEntry(entries, 8, it.symptomHeartBurn)
//            addBarEntry(entries, 9, it.symptomHeartBurn)
//        }
        return currentResult
    }
}