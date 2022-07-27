package com.example.gerdapp.ui.main

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.adapter.CardItemAdapter
import com.example.gerdapp.data.Sleep
import com.example.gerdapp.databinding.FragmentMainBinding
import com.example.gerdapp.viewmodel.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {
    
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var barChart: BarChart

    private var symptomsNum = 10

    private var bottomNavigationViewVisibility = View.VISIBLE

    private lateinit var recyclerView: RecyclerView

    private val sleepViewModel: SleepViewModel by activityViewModels {
        SleepViewModelFactory(
            (activity?.application as BasicApplication).sleepDatabase.sleepDao()
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
        var mainActivity = activity as MainActivity
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
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        barChart = binding.chartCard.barChart
        initBarChart()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        var text = "text no data"
        var sleepRecentData = "sleep data"

        sleepViewModel.getRecentRecord().observe(this.viewLifecycleOwner) {
            sleepRecentData = it.startTime
        }
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
                R.string.food -> MainFragmentDirections.actionMainFragmentToFoodFragment()
                R.string.sleep -> MainFragmentDirections.actionMainFragmentToSleepFragment()
                R.string.others -> MainFragmentDirections.actionMainFragmentToOthersFragment()
                else -> MainFragmentDirections.actionMainFragmentSelf()
            }
            findNavController().navigate(action)
        }, { cardItem ->
            val recentRecord = when (cardItem.stringResourceId) {
                R.string.symptoms -> ""
//                R.string.food -> foodRecentData
                R.string.sleep -> sleepRecentData
//                R.string.others -> othersRecentData
                else -> ""
            }
//            text = recentRecord.toString()
            recentRecord
        })

        recyclerView.adapter = adapter

        binding.apply {
            val calendar = Calendar.getInstance()
            val current = calendar.time
            val formatDate = SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
            val currentDate = formatDate.format(current)
            chartCard.chartDate.text = currentDate.toString()
            testButton.setOnClickListener { view ->
                Snackbar.make(view, sleepRecentData, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    private fun initBarChart(){
        // set data
        setBarChartData()

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
            getString(R.string.cough), getString(R.string.heart_burn), getString(R.string.acid_reflux), getString(R.string.chest_pain),
            getString(R.string.sour_mouth), getString(R.string.hoarseness), getString(R.string.appetite_loss), getString(R.string.stomach_gas),
            getString(R.string.cough_night), getString(R.string.acid_reflux_night))
        val formatter = IAxisValueFormatter{ value, axis ->
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

    private fun setBarChartData() {
        val entries1: MutableList<BarEntry> = ArrayList()
        for (i in 0 until symptomsNum) entries1.add(BarEntry(i.toFloat(), (Math.random()*5f).toInt().toFloat()))

        val data1 = BarDataSet(entries1, "Label")
        //data1.setCircleColor(Color.BLUE)
        data1.setColor(Color.BLUE)

        val dataset = ArrayList<IBarDataSet>()
        dataset.add(data1)

        val data = BarData(dataset)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        data.setDrawValues(true)

        barChart.data = data
        barChart.invalidate()
    }
}
