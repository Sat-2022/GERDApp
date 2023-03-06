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

    private var symptomList: List<SymptomCurrent>? = null
    private var drugList: List<DrugCurrent>? = null
    private var sleepList: List<SleepCurrent>? = null
    private var foodList: List<FoodCurrent>? = null
    private var eventList: List<EventCurrent>? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val STEP = 5

    private val symptomY = 2.5f
    private val sleepY = 2f
    private val foodY = 1.5f
    private val eventY = 1f
    private val drugY = 0.5f

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

        // get current date
        calendar = Calendar.getInstance()
        updateCurrent()

        callApi() // get records
    }

    override fun onResume() {
        super.onResume()
        callApi() // get records
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDailyChartBinding.inflate(inflater, container, false)

        scatterChart = binding.scatterChart
        initScatterChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            selectedDateTv.text = getString(R.string.daily_chart_date_title, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])

            // Proceed to the next day
            rightArrow.setOnClickListener {
                updateCurrent(1)
                selectedDateTv.text = getString(R.string.daily_chart_date_title, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
                clearScatterChart()
                callApi() // refresh records
            }

            // Proceed to previous day
            leftArrow.setOnClickListener {
                updateCurrent(-1)
                selectedDateTv.text = getString(R.string.daily_chart_date_title, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
                clearScatterChart()
                callApi() // refresh records
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun clearScatterChart() {
        scatterChart.clear()
        scatterChart.invalidate()
        initScatterChart()
    }

    /*
     * Set the scatter chart
     */
    private fun initScatterChart() {
        initScatterChartData()

        // scatter chart settings
        scatterChart.isHighlightPerDragEnabled = false
        scatterChart.description.isEnabled = false

        val yAxis = scatterChart.axisLeft
        val rightAxis = scatterChart.axisRight

        rightAxis.setDrawLabels(false)
        rightAxis.axisMaximum = 4f
        rightAxis.axisMinimum = 0f

        yAxis.axisMaximum = 4f
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

        val xFormatter = IAxisValueFormatter{ value, _ ->
            val h = (value / 10000).toInt()
            val m = ((value % 10000) / 100).toInt()
            getString(R.string.time_format, h, m)
        }
        xAxis.valueFormatter = xFormatter

        val l = scatterChart.legend
        l.isEnabled = false
    }

    /*
     * Init the dataset for scatter plot
     */
    private fun initScatterChartData() {
        val scatterChartDataList = ArrayList<ScatterDataSet>()
        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(-5f, -5f))
        val dataSet = ScatterDataSet(entries as List<Entry>?, "")
        dataSet.color = Color.TRANSPARENT
        scatterChartDataList.add(dataSet)

        val scatterData = ScatterData(scatterChartDataList as List<IScatterDataSet>?)
        scatterData.setDrawValues(false)
        scatterData.notifyDataChanged()

        scatterChart.data = scatterData
        scatterChart.notifyDataSetChanged()
        scatterChart.invalidate()
    }

    /*
     * Thread for calling symptom record API
     */
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
                    // Log.e("API Connection", "Symptom API connection success")
                } else {
                    // Connection failed
                    // Log.e("API Connection", "Symptom API connection failed")
                }
            } catch (e: Exception) {
                // Handle exception
                // Log.e("API Connection", "Symptom API not found")
            }
        }
    }

    /*
     * Update the symptom list
     */
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

                val data = scatterChart.data
                if(!symptomList!!.first().isEmpty()) {
                    Log.e("", "symptom")
                    val entries: ArrayList<BarEntry> = ArrayList()
                    for(d in symptomList!!) entries.add(BarEntry(TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat(), symptomY))
                    val dataSet = ScatterDataSet(entries as List<Entry>?, "")
                    dataSet.color = Color.rgb(147, 208, 109)
                    data.addDataSet(dataSet)
                    data.setDrawValues(false)
                    data.notifyDataChanged()
                    scatterChart.notifyDataSetChanged()
                    scatterChart.invalidate()
                }
            }
        }
    }

    /*
     * Thread for calling dug record API
     */
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
                    // Log.e("API Connection", "Drug API connection success")
                } else {
                    // Connection failed
                    // Log.e("API Connection", "Drug API connection failed")
                }
            } catch (e: Exception) {
                // Handle exception
                // Log.e("API Connection", "Drug API not found")
            }
        }
    }

    /*
     * Update the drug list
     */
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

                val data = scatterChart.data
                if(!drugList!!.first().isEmpty()) {
                    Log.e("", "drug")
                    val entries: ArrayList<BarEntry> = ArrayList()
                    for(d in drugList!!) entries.add(BarEntry(TimeRecord().stringToTimeRecord(d.MedicationTime).timeRecordToFloat(), drugY))
                    val dataSet = ScatterDataSet(entries as List<Entry>?, "")
                    dataSet.color = Color.rgb(241, 43, 43)
                    data.addDataSet(dataSet)
                    data.setDrawValues(false)
                    data.notifyDataChanged()
                    scatterChart.notifyDataSetChanged()
                    scatterChart.invalidate()
                }
            }
        }
    }

    /*
     * Thread for calling sleep record API
     */
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

                    inputStreamReader.close()
                    inputSystem.close()
                    // Log.e("API Connection", "Sleep API connection success")
                } else {
                    // Connection failed
                    // Log.e("API Connection", "Sleep API connection failed")
                }
            } catch (e: Exception) {
                // Handle exception
                // Log.e("API Connection", "Sleep API not found")
            }
        }
    }

    /*
     * Update the sleep list
     */
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

                val data = scatterChart.data
                if(!sleepList!!.first().isEmpty()) {
                    val entries: ArrayList<BarEntry> = ArrayList()
                    for(d in sleepList!!) {
                        if(d.isEqual(calendar) && d.isSameDate()) {
                            val start = TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat().toInt()
                            val end = TimeRecord().stringToTimeRecord(d.EndDate).timeRecordToFloat().toInt()
                            for (i in start..end step STEP) entries.add(BarEntry(i.toFloat(), sleepY))
                        } else {
                            if(d.isBefore(calendar)) {
                                val start = 0
                                val end = TimeRecord().stringToTimeRecord(d.EndDate).timeRecordToFloat().toInt()
                                for (i in start..end step STEP) entries.add(BarEntry(i.toFloat(), sleepY))
                            } else if(d.isAfter(calendar)) {
                                val start = TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat().toInt()
                                val end = 240000
                                for (i in start..end step STEP) entries.add(BarEntry(i.toFloat(), sleepY))
                            }
                        }
                    }
                    val dataSet = ScatterDataSet(entries as List<Entry>?, "")
                    dataSet.color = Color.rgb(8, 66, 160)
                    data.addDataSet(dataSet)
                    data.setDrawValues(false)
                    data.notifyDataChanged()
                    scatterChart.notifyDataSetChanged()
                    scatterChart.invalidate()
                }
            }
        }
    }

    /*
     * Thread for calling food record API
     */
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
                    // Log.e("API Connection", "Food API connection success")
                } else {
                    // Connection failed
                    // Log.e("API Connection", "Food API connection failed")
                }
            } catch (e: Exception) {
                // Handle exception
                // Log.e("API Connection", "Food API not found")
            }
        }
    }

    /*
     * Update the food list
     */
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


                val data = scatterChart.data
                if(!foodList!!.first().isEmpty()) {
                    Log.e("", "food")
                    val entries: ArrayList<BarEntry> = ArrayList()
                    for(d in foodList!!) {
                        if(d.isEqual(calendar) && d.isSameDate()) {
                            val start = TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat().toInt()
                            val end = TimeRecord().stringToTimeRecord(d.EndDate).timeRecordToFloat().toInt()
                            for (i in start..end step STEP) entries.add(BarEntry(i.toFloat(), foodY))
                        } else {
                            if(d.isBefore(calendar)) {
                                val start = 0
                                val end = TimeRecord().stringToTimeRecord(d.EndDate).timeRecordToFloat().toInt()
                                for (i in start..end step STEP) entries.add(BarEntry(i.toFloat(), foodY))
                            }
                            if(d.isAfter(calendar)) {
                                val start = TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat().toInt()
                                val end = 240000
                                for (i in start..end step STEP) entries.add(BarEntry(i.toFloat(), foodY))
                            }
                        }
                    }
                    val dataSet = ScatterDataSet(entries as List<Entry>?, "")
                    dataSet.color = Color.rgb(9, 173, 234)
                    data.addDataSet(dataSet)
                    data.setDrawValues(false)
                    data.notifyDataChanged()
                    scatterChart.notifyDataSetChanged()
                    scatterChart.invalidate()
                }
            }
        }
    }

    /*
     * Thread for calling event record API
     */
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
                    // Log.e("API Connection", "Event API connection success")
                } else {
                    // Connection failed
                    // Log.e("API Connection", "Event API connection failed")
                }
            } catch (e: Exception) {
                // Handle exception
                // Log.e("API Connection", "Event API not found")
            }
        }
    }

    /*
     * Update the event list
     */
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

                val data = scatterChart.data
                if(!eventList!!.first().isEmpty()) {
                    Log.e("", "event")
                    val eventEntries: ArrayList<BarEntry> = ArrayList()
                    for(d in eventList!!) eventEntries.add(BarEntry(TimeRecord().stringToTimeRecord(d.StartDate).timeRecordToFloat(), eventY))
                    val eventDataSet = ScatterDataSet(eventEntries as List<Entry>?, "")
                    eventDataSet.color = Color.rgb(245, 166, 29)
                    data.addDataSet(eventDataSet)
                    data.setDrawValues(false)
                    data.notifyDataChanged()
                    scatterChart.notifyDataSetChanged()
                    scatterChart.invalidate()
                }
            }
        }
    }

    /*
     * Call APIs to get the records
     */
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
            // Wait for all threads joining
            threadSymptomCurrent.join()
            threadDrugCurrent.join()
            threadSleepCurrent.join()
            threadFoodCurrent.join()
            threadEventCurrent.join()
        } catch (_: InterruptedException) {
            // Handle exception
        }
    }

    /*
     * Check if there is any data and show the text
     */
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

    /*
     * Update the calendar by inc and then update current
     */
    private fun updateCurrent(inc: Int = 0) {
        if(inc != 0) {
            calendar.add(Calendar.DAY_OF_YEAR, inc)
        }
        current = getString(R.string.input_time_format, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
    }
}