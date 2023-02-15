package com.example.gerdapp.ui.chart

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gerdapp.R
import com.example.gerdapp.data.*
import com.example.gerdapp.databinding.FragmentMonthlyChartBinding
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.endCalendar
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.startCalendar
import com.example.gerdapp.ui.chart.MonthlyChartFragment.User.caseNumber
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.currentEnd
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.currentStart
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.maxDate
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class MonthlyChartFragment: Fragment() {
    private var _binding: FragmentMonthlyChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var sleepChart: CandleStickChart
    private lateinit var foodChart: CandleStickChart
    private lateinit var symptomsChart: ScatterChart
    private lateinit var drugChart: ScatterChart
    private lateinit var eventChart: ScatterChart

    private var symptomCurrent: List<SymptomCurrent>? = null
    private var drugCurrent: List<DrugCurrent>? = null
    private var sleepCurrent: List<SleepCurrent>? = null
    private var foodCurrent: List<FoodCurrent>? = null
    private var eventCurrent: List<EventCurrent>? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    object DateRange {
        lateinit var startCalendar: Calendar
        lateinit var endCalendar: Calendar
        var maxDate = 0
        var currentStart = ""
        var currentEnd = ""
    }

    object User {
        var caseNumber = ""
        var gender = ""
        var nickname = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        editor = preferences.edit()

        caseNumber = preferences.getString("caseNumber", "").toString()

        startCalendar = Calendar.getInstance()
        endCalendar = Calendar.getInstance()

        updateCurrent()

        callApi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMonthlyChartBinding.inflate(inflater, container, false)

        sleepChart = binding.monthlySleepChart
        foodChart = binding.monthlyFoodChart
        symptomsChart = binding.monthlySymptomChart
        drugChart = binding.monthlyDrugChart
        eventChart = binding.monthlyEventChart

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            selectedDateTv.text = getString(R.string.monthly_chart_date_title, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1)

            rightArrow.setOnClickListener {
                updateCurrent(1)
                selectedDateTv.text = getString(R.string.monthly_chart_date_title, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1)
                callApi()
            }

            leftArrow.setOnClickListener {
                updateCurrent(-1)
                selectedDateTv.text = getString(R.string.monthly_chart_date_title, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1)
                callApi()
            }
        }
    }

    private fun initSymptomsChart() {
        initSymptomsChartData()

        symptomsChart.isHighlightPerDragEnabled = false
        symptomsChart.description.isEnabled = false

        val yAxis = symptomsChart.axisLeft
        val rightAxis = symptomsChart.axisRight

        rightAxis.setDrawLabels(false)

        yAxis.axisMaximum = 240000f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        rightAxis.axisMaximum = 240000f
        rightAxis.axisMinimum = 0f
        rightAxis.labelCount = 6
        rightAxis.setDrawLabels(true)
        symptomsChart.requestDisallowInterceptTouchEvent(true)

        val rightFormatter = IAxisValueFormatter { value, _ ->
            val h = (value / 10000).toInt()
            val m = ((value % 10000) / 100).toInt()
            getString(R.string.time_format, h, m)
        }

        rightAxis.valueFormatter = rightFormatter

        val xAxis = symptomsChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setAvoidFirstLastClipping(true)

        val mActivities = arrayOf("日", "一", "二", "三", "四", "五", "六")
        val xFormatter = IAxisValueFormatter{ value, _ ->
            mActivities[value.toInt() % mActivities.size]
        }
        xAxis.valueFormatter = xFormatter

        val l = symptomsChart.legend
        l.isEnabled = false
    }

    private fun initSymptomsChartData() {
        val entries: java.util.ArrayList<BarEntry> = java.util.ArrayList()

        val tempCalendar = startCalendar.clone() as Calendar
        var dayOfMonth = 1

        if(!symptomCurrent!!.first().isEmpty()) {
            for(symptomData in symptomCurrent!!) {
                while (!symptomData.isSameDate(tempCalendar)) {
                    entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
                    tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    dayOfMonth += 1
                }

                val startTimeRecord = TimeRecord().stringToTimeRecord(symptomData.StartDate)
                entries.add(BarEntry(dayOfMonth.toFloat() - 1, startTimeRecord.timeRecordToFloat()))
            }
        } else {
            entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
            dayOfMonth += 1
        }

        while (dayOfMonth < maxDate) {
            dayOfMonth += 1
            entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
        }

        val barDataSet = ScatterDataSet(entries as List<Entry>?, "")
        barDataSet.color = Color.rgb(147, 208, 109)

        val barData = ScatterData(barDataSet)

        val mv = CandleStickChartMarkerView(context, R.layout.markerview_candle_stick_chart)
        symptomsChart.markerView = mv

        barData.setDrawValues(false)

        symptomsChart.data = barData
        symptomsChart.invalidate()
    }

    private fun initDrugChart() {
        initDrugChartData()

        drugChart.isHighlightPerDragEnabled = false
        drugChart.description.isEnabled = false

        val yAxis = drugChart.axisLeft
        val rightAxis = drugChart.axisRight

        rightAxis.setDrawLabels(false)

        yAxis.axisMaximum = 240000f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        rightAxis.axisMaximum = 240000f
        rightAxis.axisMinimum = 0f
        rightAxis.labelCount = 6
        rightAxis.setDrawLabels(true)
        drugChart.requestDisallowInterceptTouchEvent(true)

        val rightFormatter = IAxisValueFormatter { value, _ ->
            val h = (value / 10000).toInt()
            val m = ((value % 10000) / 100).toInt()
            getString(R.string.time_format, h, m)
        }

        rightAxis.valueFormatter = rightFormatter

        val xAxis = drugChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setAvoidFirstLastClipping(true)

        val mActivities = arrayOf("日", "一", "二", "三", "四", "五", "六")
        val xFormatter = IAxisValueFormatter{ value, _ ->
            mActivities[value.toInt() % mActivities.size]
        }
        xAxis.valueFormatter = xFormatter

        val l = drugChart.legend
        l.isEnabled = false
    }

    private fun initDrugChartData() {
        val entries: java.util.ArrayList<BarEntry> = java.util.ArrayList()

        val tempCalendar = startCalendar.clone() as Calendar
        var dayOfMonth = 1

        if(!drugCurrent!!.first().isEmpty()) {
            for(drugData in drugCurrent!!) {
                while (!drugData.isSameDate(tempCalendar)) {
                    entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
                    tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    dayOfMonth += 1
                }

                val startTimeRecord = TimeRecord().stringToTimeRecord(drugData.MedicationTime)
                entries.add(BarEntry(dayOfMonth.toFloat() - 1, startTimeRecord.timeRecordToFloat()))
            }
        } else {
            entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
            dayOfMonth += 1
        }

        while (dayOfMonth < maxDate) {
            dayOfMonth += 1
            entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
        }

        val barDataSet = ScatterDataSet(entries as List<Entry>?, "")
        barDataSet.color = Color.rgb(241, 43, 43)

        val barData = ScatterData(barDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        barData.setDrawValues(false)

        drugChart.data = barData
        drugChart.invalidate()
    }

    private fun initEventChart() {
        initEventChartData()

        eventChart.isHighlightPerDragEnabled = false
        eventChart.description.isEnabled = false

        val yAxis = eventChart.axisLeft
        val rightAxis = eventChart.axisRight

        rightAxis.setDrawLabels(false)

        yAxis.axisMaximum = 240000f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        rightAxis.axisMaximum = 240000f
        rightAxis.axisMinimum = 0f
        rightAxis.labelCount = 6
        rightAxis.setDrawLabels(true)
        eventChart.requestDisallowInterceptTouchEvent(true)

        val rightFormatter = IAxisValueFormatter { value, _ ->
            val h = (value / 10000).toInt()
            val m = ((value % 10000) / 100).toInt()
            getString(R.string.time_format, h, m)
        }

        rightAxis.valueFormatter = rightFormatter

        val xAxis = eventChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setAvoidFirstLastClipping(true)

        val mActivities = arrayOf("日", "一", "二", "三", "四", "五", "六")
        val xFormatter = IAxisValueFormatter{ value, _ ->
            mActivities[value.toInt() % mActivities.size]
        }
        xAxis.valueFormatter = xFormatter

        val l = eventChart.legend
        l.isEnabled = false
    }

    private fun initEventChartData() {
        val entries: java.util.ArrayList<BarEntry> = java.util.ArrayList()

        val tempCalendar = startCalendar.clone() as Calendar
        var dayOfMonth = 1

        if(!eventCurrent!!.first().isEmpty()) {
            for(eventData in eventCurrent!!) {
                while (!eventData.isSameDate(tempCalendar)) {
                    entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
                    tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    dayOfMonth += 1
                }

                val startTimeRecord = TimeRecord().stringToTimeRecord(eventData.StartDate)
                entries.add(BarEntry(dayOfMonth.toFloat() - 1, startTimeRecord.timeRecordToFloat()))
            }
        } else {
            entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
            dayOfMonth += 1
        }

        while (dayOfMonth < maxDate) {
            dayOfMonth += 1
            entries.add(BarEntry(dayOfMonth.toFloat() - 1, -5f))
        }

        val barDataSet = ScatterDataSet(entries as List<Entry>?, "")
        barDataSet.color = Color.rgb(245, 166, 29)

        val barData = ScatterData(barDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        barData.setDrawValues(false)

        eventChart.data = barData
        eventChart.invalidate()
    }

    private fun initSleepChartData() {
        val entries: ArrayList<CandleEntry> = ArrayList()

        val tempCalendar = startCalendar.clone() as Calendar
        var dayOfMonth = 1

        if(!sleepCurrent!!.first().isEmpty()) {
            for(sleepData in sleepCurrent!!) {
//                Log.e("", "$dayOfMonth")
                while (!sleepData.isSameDate(tempCalendar)) {
                    entries.add(CandleEntry(dayOfMonth.toFloat() - 1, -1f, -1f, -1f, -1f))
                    tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    dayOfMonth += 1
                }

                val startTimeRecord = TimeRecord().stringToTimeRecord(sleepData.StartDate)
                val endTimeRecord = TimeRecord().stringToTimeRecord(sleepData.EndDate)

                if (sleepData.isSameDate(tempCalendar, 1)) { // Crossing date
                    entries.add(
                        CandleEntry(
                            dayOfMonth.toFloat() - 1,
                            startTimeRecord.timeRecordToFloat(), 240000f,
                            startTimeRecord.timeRecordToFloat(), 240000f
                        )
                    )
                    if (dayOfMonth < maxDate) {
                        entries.add(
                            CandleEntry(
                                dayOfMonth.toFloat(),
                                0f, endTimeRecord.timeRecordToFloat(),
                                0f, endTimeRecord.timeRecordToFloat()
                            )
                        )
                    }
                } else {
                    entries.add(
                        CandleEntry(
                            dayOfMonth.toFloat() - 1,
                            startTimeRecord.timeRecordToFloat(),
                            endTimeRecord.timeRecordToFloat(),
                            startTimeRecord.timeRecordToFloat(),
                            endTimeRecord.timeRecordToFloat()
                        )
                    )
                }
            }
        } else {
            entries.add(CandleEntry(dayOfMonth.toFloat() - 1, -1f, -1f, -1f, -1f))
            dayOfMonth += 1
        }

        while (dayOfMonth < maxDate) {
            dayOfMonth += 1
            entries.add(CandleEntry(dayOfMonth.toFloat() - 1, -1f, -1f, -1f, -1f))
        }

        val candleDataSet = CandleDataSet(entries, "")
        candleDataSet.color = Color.rgb(8, 66, 160)
        candleDataSet.shadowColor = Color.LTGRAY
        candleDataSet.shadowWidth = 0.8f
        candleDataSet.decreasingColor = Color.rgb(8, 66, 160)
        candleDataSet.decreasingPaintStyle = Paint.Style.FILL
        candleDataSet.increasingColor = Color.rgb(8, 66, 160)
        candleDataSet.increasingPaintStyle = Paint.Style.FILL
        candleDataSet.neutralColor = Color.TRANSPARENT

        val candleData = CandleData(candleDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        candleData.setDrawValues(false)

        sleepChart.data = candleData
        sleepChart.invalidate()
    }

    private fun initSleepChart() {
        if(!sleepCurrent.isNullOrEmpty()) { initSleepChartData() }

        sleepChart.isHighlightPerDragEnabled = false
        sleepChart.description.isEnabled = false

        val yAxis = sleepChart.axisLeft
        val rightAxis = sleepChart.axisRight

//        yAxis.labelCount = 6
        yAxis.axisMaximum = 240000f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        rightAxis.axisMaximum = 240000f
        rightAxis.axisMinimum = 0f
        rightAxis.labelCount = 6
        rightAxis.setDrawLabels(true)
        sleepChart.requestDisallowInterceptTouchEvent(true)

        val rightFormatter = IAxisValueFormatter { value, _ ->
            val h = (value / 10000).toInt()
            val m = ((value % 10000) / 100).toInt()
            getString(R.string.time_format, h, m)
        }

        rightAxis.valueFormatter = rightFormatter

        val xAxis = sleepChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setAvoidFirstLastClipping(true)

        val xFormatter = IAxisValueFormatter { value, _ ->
//            startCalendar[Calendar.DAY_OF_MONTH].toString() + "/" +
            (value + 1).toInt().toString()
        }
        xAxis.valueFormatter = xFormatter

        val l = sleepChart.legend
        l.isEnabled = false
    }

    private fun initFoodChartData() {
        val entries: ArrayList<CandleEntry> = ArrayList()

        val tempCalendar = startCalendar.clone() as Calendar
        var dayOfMonth = 1

        if(!foodCurrent!!.first().isEmpty()) {
            for(foodData in foodCurrent!!) {
                while (!foodData.isSameDate(tempCalendar)) {
                    entries.add(CandleEntry(dayOfMonth.toFloat() - 1, -1f, -1f, -1f, -1f))
                    tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    dayOfMonth += 1
                }

                val startTimeRecord = TimeRecord().stringToTimeRecord(foodData.StartDate)
                val endTimeRecord = TimeRecord().stringToTimeRecord(foodData.EndDate)

                if(foodData.isSameDate(tempCalendar, 1)) { // Crossing date
//                    Log.e("", "1_$dayOfMonth: $sleepData")
                    entries.add(
                        CandleEntry(
                            dayOfMonth.toFloat() - 1,
                            startTimeRecord.timeRecordToFloat(), 240000f,
                            startTimeRecord.timeRecordToFloat(), 240000f
                        )
                    )
                    if(dayOfMonth < maxDate) {
                        entries.add(
                            CandleEntry(
                                dayOfMonth.toFloat(),
                                0f, endTimeRecord.timeRecordToFloat(),
                                0f, endTimeRecord.timeRecordToFloat()
                            )
                        )
                    }
                } else {
//                    Log.e("", "2_$dayOfMonth: $sleepData")
                    entries.add(
                        CandleEntry(
                            dayOfMonth.toFloat() - 1,
                            startTimeRecord.timeRecordToFloat(), endTimeRecord.timeRecordToFloat(),
                            startTimeRecord.timeRecordToFloat(), endTimeRecord.timeRecordToFloat()
                        )
                    )
                }
            }
        } else {
            entries.add(CandleEntry(dayOfMonth.toFloat() - 1, -1f, -1f, -1f, -1f))
            dayOfMonth += 1
        }

        while (dayOfMonth < maxDate) {
            dayOfMonth += 1
            entries.add(CandleEntry(dayOfMonth.toFloat() - 1, -1f, -1f, -1f, -1f))
        }

        val candleDataSet = CandleDataSet(entries, "")
        candleDataSet.color = Color.rgb(9, 173, 234)
        candleDataSet.shadowColor = Color.LTGRAY
        candleDataSet.shadowWidth = 0.8f
        candleDataSet.decreasingColor = Color.rgb(9, 173, 234)
        candleDataSet.decreasingPaintStyle = Paint.Style.FILL
        candleDataSet.increasingColor = Color.rgb(9, 173, 234)
        candleDataSet.increasingPaintStyle = Paint.Style.FILL
        candleDataSet.neutralColor = Color.TRANSPARENT

        val candleData = CandleData(candleDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        candleData.setDrawValues(false)

        foodChart.data = candleData
        foodChart.invalidate()
    }

    private fun initFoodChart() {
        if(!foodCurrent.isNullOrEmpty()) { initFoodChartData() }

        foodChart.isHighlightPerDragEnabled = false
        foodChart.description.isEnabled = false

        val yAxis = foodChart.axisLeft
        val rightAxis = foodChart.axisRight

//        yAxis.labelCount = 6
        yAxis.axisMaximum = 240000f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        rightAxis.axisMaximum = 240000f
        rightAxis.axisMinimum = 0f
        rightAxis.labelCount = 6
        rightAxis.setDrawLabels(true)
        foodChart.requestDisallowInterceptTouchEvent(true)

        val rightFormatter = IAxisValueFormatter{ value, _ ->
            val h = (value / 10000).toInt()
            val m = ((value % 10000) / 100).toInt()
            getString(R.string.time_format, h, m)
        }

        rightAxis.valueFormatter = rightFormatter

        val xAxis = foodChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setAvoidFirstLastClipping(true)

        val xFormatter = IAxisValueFormatter{ value, _ ->
//            startCalendar[Calendar.DAY_OF_MONTH].toString() + "/" +
            (value + 1).toInt().toString()
        }
        xAxis.valueFormatter = xFormatter

        val l = foodChart.legend
        l.isEnabled = false
    }

    private fun updateSymptomsChart() {
        activity?.runOnUiThread {
            binding.apply {
                initSymptomsChart()
            }
        }
    }

    private fun updateDrugChart() {
        activity?.runOnUiThread {
            binding.apply {
                initDrugChart()
            }
        }
    }

    private fun updateFoodChart() {
        activity?.runOnUiThread {
            binding.apply {
                initFoodChart()
            }
        }
    }

    private fun updateSleepChart() {
        activity?.runOnUiThread {
            binding.apply {
                initSleepChart()
            }
        }
    }

    private fun updateEventChart() {
        activity?.runOnUiThread {
            binding.apply {
                initEventChart()
            }
        }
    }

    private fun getSymptomCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(getString(R.string.get_symptoms_record_url, getString(R.string.server_url),
                    caseNumber,
                    currentStart,
                    currentEnd, "ASC"))
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? =
                        object : TypeToken<List<SymptomCurrent>>() {}.type
                    symptomCurrent = Gson().fromJson(inputStreamReader, type)

                    updateSymptomsChart()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Symptoms API connection success")
//                    Log.e("API Connection", foodCurrent.toString())
                } else {
                    Log.e("API Connection", "Symptom API connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Symptom API not found")
            }
        }
    }

    private fun getDrugCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(getString(R.string.get_drug_record_url, getString(R.string.server_url),
                    caseNumber,
                    currentStart,
                    currentEnd, "ASC"))
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? =
                        object : TypeToken<List<DrugCurrent>>() {}.type
                    drugCurrent = Gson().fromJson(inputStreamReader, type)

                    updateDrugChart()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Drug API connection success")
//                    Log.e("API Connection", foodCurrent.toString())
                } else {
                    Log.e("API Connection", "Drug API connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Drug API not found")
            }
        }
    }

    private fun getEventCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(getString(R.string.get_event_record_url, getString(R.string.server_url),
                    caseNumber,
                    currentStart,
                    currentEnd, "ASC"))
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? =
                        object : TypeToken<List<EventCurrent>>() {}.type
                    eventCurrent = Gson().fromJson(inputStreamReader, type)

                    updateEventChart()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Event API connection success")
//                    Log.e("API Connection", foodCurrent.toString())
                } else {
                    Log.e("API Connection", "Event API connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Event API not found")
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
                        caseNumber,
                        currentStart,
                        currentEnd,
                        "ASC"
                    )
                )
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? =
                        object : TypeToken<List<SleepCurrent>>() {}.type
                    sleepCurrent = Gson().fromJson(inputStreamReader, type)

                    updateSleepChart()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Sleep API connection success")
                } else {
                    Log.e("API Connection", "Sleep API connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Sleep API not found")
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
                        caseNumber,
                        currentStart,
                        currentEnd,
                        "ASC"
                    )
                )
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? =
                        object : TypeToken<List<FoodCurrent>>() {}.type
                    foodCurrent = Gson().fromJson(inputStreamReader, type)

                    updateFoodChart()

                    inputStreamReader.close()
                    inputSystem.close()
                    Log.e("API Connection", "Sleep API connection success")
                } else {
                    Log.e("API Connection", "Sleep API connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Sleep API not found")
            }
        }
    }

    private fun callApi() {
//        val threadNotification = getNotificationApi()
        val threadSymptomCurrent = getSymptomCurrentApi()
        val threadDrugCurrent = getDrugCurrentApi()
        val threadSleepCurrent = getSleepCurrentApi()
        val threadFoodCurrent = getFoodCurrentApi()
        val threadEventCurrent = getEventCurrentApi()

//        threadNotification.start()
        threadSymptomCurrent.start()
        threadDrugCurrent.start()
        threadSleepCurrent.start()
        threadFoodCurrent.start()
        threadEventCurrent.start()

        try {
//            threadNotification.join()
            threadSymptomCurrent.join()
            threadDrugCurrent.join()
            threadSleepCurrent.join()
            threadFoodCurrent.join()
            threadEventCurrent.join()
//            refreshComplete = true
        } catch (_: InterruptedException) {

        }
    }

    private fun updateCurrent(inc: Int = 0) {
        if(inc != 0) {
            startCalendar.add(Calendar.MONTH, inc)
            endCalendar = startCalendar.clone() as Calendar
        }

        maxDate = startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        startCalendar.set(Calendar.DAY_OF_MONTH, 1)
        endCalendar.set(Calendar.DAY_OF_MONTH, maxDate)

        currentStart = getString(R.string.input_time_format, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1, startCalendar[Calendar.DAY_OF_MONTH])
        currentEnd = getString(R.string.input_time_format, endCalendar[Calendar.YEAR], endCalendar[Calendar.MONTH]+1, endCalendar[Calendar.DAY_OF_MONTH])
    }
}