package com.example.gerdapp.ui.chart

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gerdapp.R
import com.example.gerdapp.data.*
import com.example.gerdapp.databinding.FragmentDailyChartBinding
import com.example.gerdapp.ui.chart.DailyChartFragment.DateRange.calendar
import com.example.gerdapp.ui.chart.DailyChartFragment.DateRange.current
import com.example.gerdapp.ui.chart.adapter.*
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class DailyChartFragment: Fragment() {
    private var _binding: FragmentDailyChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var scatterChart: ScatterChart

    private var scatterChartDataList = ArrayList<ScatterDataSet>()

    private var symptomList: List<SymptomCurrent>? = null
    private var drugList: List<DrugCurrent>? = null
    private var sleepList: List<SleepCurrent>? = null
    private var foodList: List<FoodCurrent>? = null
    private var eventList: List<EventCurrent>? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    object User {
        var caseNumber = ""
        var gender = ""
        var nickname = ""
    }

    object DateRange {
        lateinit var calendar: Calendar
        var current = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        editor = preferences.edit()

        User.caseNumber = preferences.getString("caseNumber", "").toString()

        calendar = Calendar.getInstance()
        updateCurrent()

        callApi()
    }

    override fun onResume() {
        super.onResume()
        callApi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDailyChartBinding.inflate(inflater, container, false)

        scatterChart = binding.scatterChart
//        initScatterChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            selectedDateTv.text = getString(R.string.daily_chart_date_title, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])

            rightArrow.setOnClickListener {
                updateCurrent(1)
                selectedDateTv.text = getString(R.string.daily_chart_date_title, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
                callApi()
            }

            leftArrow.setOnClickListener {
                updateCurrent(-1)
                selectedDateTv.text = getString(R.string.daily_chart_date_title, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
                callApi()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateScatterChart() {
        activity?.runOnUiThread {
            initScatterChart()
        }
    }

    private fun initScatterChart() {
        initScatterChartData()

        scatterChart.isHighlightPerDragEnabled = false
        scatterChart.description.isEnabled = false

        val yAxis = scatterChart.axisLeft
        val rightAxis = scatterChart.axisRight

        rightAxis.setDrawLabels(false)

//        yAxis.labelCount = 6
        yAxis.axisMaximum = 5f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        scatterChart.requestDisallowInterceptTouchEvent(true)


        val xAxis = scatterChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(true)
        xAxis.labelCount = 6
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setAvoidFirstLastClipping(true)

        xAxis.axisMaximum = 240000f
        xAxis.axisMinimum = 0f

//        val mActivities = arrayOf("00:00", "06:00", "12:00", "18:00", "24:00")
        val xFormatter = IAxisValueFormatter{ value, _ ->
//            Log.e("", (value.toInt() / (240000 / mActivities.size)).toString())
//            mActivities[value.toInt() / (240000 / mActivities.size)]
            val h = (value / 10000).toInt()
            val m = ((value % 10000) / 100).toInt()
            getString(R.string.time_format, h, m)
        }
        xAxis.valueFormatter = xFormatter

        val l = scatterChart.legend
        l.isEnabled = false
    }

    private fun initScatterChartData() {

        if(!symptomList!!.first().isEmpty()){
            val symptomsEntries: ArrayList<BarEntry> = ArrayList()
            for (d in symptomList!!) {
                symptomsEntries.add(BarEntry(TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat(), 1f))
            }
            val symptomsDataSet = ScatterDataSet(symptomsEntries as List<Entry>?, "")
            symptomsDataSet.color = Color.rgb(147, 208, 109)
            scatterChartDataList.add(symptomsDataSet)
        }


        if(!drugList!!.first().isEmpty()){
            val drugEntries: ArrayList<BarEntry> = ArrayList()
            for (d in drugList!!) {
                drugEntries.add(BarEntry(TimeRecord().stringToTimeRecord(d.MedicationTime).timeRecordToFloat(), 1f))
            }
            val drugDataSet = ScatterDataSet(drugEntries as List<Entry>?, "")
            drugDataSet.color = Color.rgb(147, 208, 109)
            scatterChartDataList.add(drugDataSet)
        }

        if(!foodList!!.first().isEmpty()){
            val foodEntries: ArrayList<BarEntry> = ArrayList()
            for (d in foodList!!) {
                foodEntries.add(BarEntry(TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat(), 1f))
            }
            val foodDataSet = ScatterDataSet(foodEntries as List<Entry>?, "")
            foodDataSet.color = Color.rgb(147, 208, 109)
            Log.e("", "$foodDataSet")
            scatterChartDataList.add(foodDataSet)
        }

        if(!sleepList!!.first().isEmpty()){
            val sleepEntries: ArrayList<BarEntry> = ArrayList()
            for (d in sleepList!!) {
                sleepEntries.add(BarEntry(TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat(), 1f))
            }
            val sleepDataSet = ScatterDataSet(sleepEntries as List<Entry>?, "")
            sleepDataSet.color = Color.rgb(147, 208, 109)
            scatterChartDataList.add(sleepDataSet)
        }

        if(!eventList!!.first().isEmpty()){
            val eventEntries: ArrayList<BarEntry> = ArrayList()
            for (d in eventList!!) {
                eventEntries.add(BarEntry(TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat(), 1f))
            }
            val eventDataSet = ScatterDataSet(eventEntries as List<Entry>?, "")
            eventDataSet.color = Color.rgb(147, 208, 109)
            scatterChartDataList.add(eventDataSet)
        }

        if(scatterChartDataList.isNotEmpty()) {
            val barData = ScatterData(scatterChartDataList as List<IScatterDataSet>?)

            /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
            mv.chartView = lineChart
            lineChart.marker = mv*/

            barData.setDrawValues(false)

            scatterChart.data = barData
            scatterChart.invalidate()
        }
    }

    private fun getSymptomsCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(
                    getString(
                        R.string.get_symptoms_record_url,
                        getString(R.string.server_url),
                        User.caseNumber,
                        current,
                        current,
                        "DESC"
                    )
                )
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {

                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? = object : TypeToken<List<SymptomCurrent>>() {}.type
                    symptomList = Gson().fromJson(inputStreamReader, type)

                    updateSymptoms()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

    private fun updateSymptoms() {
        activity?.runOnUiThread {
            binding.apply {
                val symptomsAdapter = SymptomsAdapter { symptomItem -> }

                if(symptomList != null) {

                    symptomsAdapter.updateSymptomList(symptomList!!)

                    if(symptomList!!.first().isEmpty()) {
                        symptomsRecyclerView.visibility = View.GONE
                        symptomsTitle.visibility = View.GONE
                        checkNullData()
                    } else {
                        symptomsRecyclerView.visibility = View.VISIBLE
                        symptomsTitle.visibility = View.VISIBLE
                    }
                }

                symptomsRecyclerView.adapter = symptomsAdapter
            }
        }
    }

    private fun getDrugCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(
                    getString(
                        R.string.get_drug_record_url,
                        getString(R.string.server_url),
                        User.caseNumber,
                        current,
                        current,
                        "DESC"
                    )
                )
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {

                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? = object : TypeToken<List<DrugCurrent>>() {}.type
                    drugList = Gson().fromJson(inputStreamReader, type)

                    updateDrug()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

    private fun updateDrug() {
        activity?.runOnUiThread {
            binding.apply {
                val drugAdapter = DrugAdapter { drugItem -> }

                if(drugList != null) {
                    drugAdapter.updateDrugList(drugList!!)

                    if(drugList!!.first().isEmpty()) {
                        drugRecyclerView.visibility = View.GONE
                        drugTitle.visibility = View.GONE
                        checkNullData()
                    } else {
                        drugRecyclerView.visibility = View.VISIBLE
                        drugTitle.visibility = View.VISIBLE
                    }
                }

                drugRecyclerView.adapter = drugAdapter
            }
        }
    }

    private fun getSleepCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(
                    getString(
                        R.string.get_sleep_record_url,
                        getString(R.string.server_url),
                        User.caseNumber,
                        current,
                        current,
                        "DESC"
                    )
                )
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {

                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? = object : TypeToken<List<SleepCurrent>>() {}.type

                    sleepList = Gson().fromJson(inputStreamReader, type)

                    updateSleep()
                    updateScatterChart()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Sleep API: Connection success")
                } else {
                    Log.e("API Connection", "Sleep API: Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Sleep API: Service not found")
            }
        }
    }

    private fun updateSleep() {
        activity?.runOnUiThread {
            binding.apply {
                val sleepAdapter = SleepAdapter { sleepItem -> }

                if(sleepList != null) {
                    sleepAdapter.updateSleepList(sleepList!!)

                    if(sleepList!!.first().isEmpty()) {
                        sleepRecyclerView.visibility = View.GONE
                        sleepTitle.visibility = View.GONE
                        checkNullData()
                    } else {
                        sleepRecyclerView.visibility = View.VISIBLE
                        sleepTitle.visibility = View.VISIBLE
                    }
                }

                sleepRecyclerView.adapter = sleepAdapter
            }
        }
    }

    private fun getFoodCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(
                    getString(
                        R.string.get_food_record_url,
                        getString(R.string.server_url),
                        User.caseNumber,
                        current,
                        current,
                        "DESC"
                    )
                )
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {

                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? = object : TypeToken<List<FoodCurrent>>() {}.type
                    foodList = Gson().fromJson(inputStreamReader, type)

                    updateFood()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Food API: Connection success")
                } else {
                    Log.e("API Connection", "Food API: Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Food API: Service not found $e")
            }
        }
    }

    private fun updateFood() {
        activity?.runOnUiThread {
            binding.apply {
                val foodAdapter = FoodAdapter { foodItem -> }

                if(foodList != null) {
                    foodAdapter.updateFoodList(foodList!!)

                    if(foodList!!.first().isEmpty()) {
                        foodRecyclerView.visibility = View.GONE
                        foodTitle.visibility = View.GONE
                        checkNullData()
                    } else {
                        foodRecyclerView.visibility = View.VISIBLE
                        foodTitle.visibility = View.VISIBLE
                    }
                }

                foodRecyclerView.adapter = foodAdapter
            }
        }
    }

    private fun getEventCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(
                    getString(
                        R.string.get_event_record_url,
                        getString(R.string.server_url),
                        User.caseNumber,
                        current,
                        current,
                        "DESC"
                    )
                )
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {

                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? = object : TypeToken<List<EventCurrent>>() {}.type
                    eventList = Gson().fromJson(inputStreamReader, type)

                    updateEvent()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Connection success")
                } else
                    Log.e("API Connection", "Connection failed")
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

    private fun updateEvent() {
        activity?.runOnUiThread {
            binding.apply {
                val eventAdapter = EventAdapter { eventItem -> }

                if(eventList != null) {
                    eventAdapter.updateEventList(eventList!!)

                    if(eventList!!.first().isEmpty()) {
                        eventRecyclerView.visibility = View.GONE
                        eventTitle.visibility = View.GONE
                        checkNullData()
                    } else {
                        eventRecyclerView.visibility = View.VISIBLE
                        eventTitle.visibility = View.VISIBLE
                    }
                }

                eventRecyclerView.adapter = eventAdapter
            }
        }
    }

    private fun callApi() {
        val threadSymptomCurrent = getSymptomsCurrentApi()
        val threadDrugCurrent = getDrugCurrentApi()
        val threadSleepCurrent = getSleepCurrentApi()
        val threadFoodCurrent = getFoodCurrentApi()
        val threadEventCurrent = getEventCurrentApi()

        threadSymptomCurrent.start()
        threadDrugCurrent.start()
        threadSleepCurrent.start()
        threadFoodCurrent.start()
        threadEventCurrent.start()

        try {
            threadSymptomCurrent.join()
            threadDrugCurrent.join()
            threadSleepCurrent.join()
            threadFoodCurrent.join()
            threadEventCurrent.join()
        } catch (_: InterruptedException) {

        }
    }

    private fun checkNullData() {
        binding.apply {
            if (symptomList!!.first().isEmpty() && drugList!!.first().isEmpty() &&
                sleepList!!.first().isEmpty() && foodList!!.first().isEmpty() && eventList!!.first().isEmpty()
            ) {
                noRecordTv.visibility = View.VISIBLE
            } else {
                noRecordTv.visibility = View.GONE
            }
        }
    }

    private fun updateCurrent(inc: Int = 0) {
        if(inc != 0) {
            calendar.add(Calendar.DAY_OF_YEAR, inc)
        }

        current = getString(R.string.input_time_format, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
    }
}