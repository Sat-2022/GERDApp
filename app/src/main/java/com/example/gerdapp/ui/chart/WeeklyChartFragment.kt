package com.example.gerdapp.ui.chart

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gerdapp.R
import com.example.gerdapp.data.Questions
import com.example.gerdapp.data.SleepCurrent
import com.example.gerdapp.data.TimeRecord
import com.example.gerdapp.databinding.FragmentWeeklyChartBinding
import com.example.gerdapp.ui.calendar.CalendarFragment
import com.example.gerdapp.ui.chart.WeeklyChartFragment.DateRange.startCalendar
import com.example.gerdapp.ui.chart.WeeklyChartFragment.DateRange.currentEnd
import com.example.gerdapp.ui.chart.WeeklyChartFragment.DateRange.currentStart
import com.example.gerdapp.ui.chart.WeeklyChartFragment.DateRange.endCalendar
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class WeeklyChartFragment: Fragment() {
    private var _binding: FragmentWeeklyChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var candleStickChart: CandleStickChart

    private lateinit var questionnaireResult: List<Questions>

    private var sleepCurrent: List<SleepCurrent>? = null
    private var sleepChartData: Array<MutableList<SleepCurrent>?> = arrayOfNulls(7)

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    object DateRange {
        lateinit var startCalendar: Calendar
        lateinit var endCalendar: Calendar
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

        User.caseNumber = preferences.getString("caseNumber", "").toString()

        startCalendar = Calendar.getInstance()
        endCalendar = startCalendar.clone() as Calendar

        startCalendar.set(Calendar.DAY_OF_WEEK, 1)
        endCalendar.set(Calendar.DAY_OF_WEEK, 7)

        updateCurrent()

        callApi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentWeeklyChartBinding.inflate(inflater, container, false)

        lineChart = binding.lineChart
        barChart = binding.barChart
        candleStickChart = binding.timeRangeChart
        initLineChart()
        initBarChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            selectedDateTv.text = getString(R.string.date_time_format_ch, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1, startCalendar[Calendar.DAY_OF_MONTH]) +
                    " ~ " + getString(R.string.date_time_format_ch, endCalendar[Calendar.YEAR], endCalendar[Calendar.MONTH]+1, endCalendar[Calendar.DAY_OF_MONTH])

            rightArrow.setOnClickListener {
                startCalendar.roll(Calendar.WEEK_OF_YEAR, true)
                endCalendar = startCalendar.clone() as Calendar
                startCalendar.set(Calendar.DAY_OF_WEEK, 1)
                endCalendar.set(Calendar.DAY_OF_WEEK, 7)

                updateCurrent()
                selectedDateTv.text = getString(R.string.date_time_format_ch, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1, startCalendar[Calendar.DAY_OF_MONTH]) +
                        " ~ " + getString(R.string.date_time_format_ch, endCalendar[Calendar.YEAR], endCalendar[Calendar.MONTH]+1, endCalendar[Calendar.DAY_OF_MONTH])

                callApi()
            }

            leftArrow.setOnClickListener {
                startCalendar.roll(Calendar.WEEK_OF_YEAR, false)
                endCalendar.roll(Calendar.WEEK_OF_YEAR, false)
                startCalendar.set(Calendar.DAY_OF_WEEK, 1)
                endCalendar.set(Calendar.DAY_OF_WEEK, 7)

                updateCurrent()
                selectedDateTv.text = getString(R.string.date_time_format_ch, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1, startCalendar[Calendar.DAY_OF_MONTH]) +
                        " ~ " + getString(R.string.date_time_format_ch, endCalendar[Calendar.YEAR], endCalendar[Calendar.MONTH]+1, endCalendar[Calendar.DAY_OF_MONTH])

                callApi()
            }
        }
    }

    private fun initLineChart(){
        // set data
        setLineChartData()

        lineChart.setBackgroundColor(Color.WHITE)
        lineChart.description.isEnabled = false
//        chart.setTouchEnabled(false)
//        chart.isDragEnabled = false


        // add animation
        lineChart.animateY(1400)

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
        lineChart.legend.isEnabled = false

        val xAxis: XAxis = lineChart.xAxis
        xAxis.typeface = Typeface.DEFAULT
        xAxis.textSize = 12f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.setDrawGridLines(false)
//        xAxis.textColor = Color.BLACK
//        xAxis.setDrawGridLines(true)
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setLabelCount(5, true)

        val mActivities = arrayOf("一", "二", "三", "四", "五", "六", "日")
        val formatter = IAxisValueFormatter{ value, axis ->
            mActivities[value.toInt() % mActivities.size]
        }
        xAxis.valueFormatter = formatter

        val yAxis = lineChart.axisLeft
        yAxis.axisMaximum = 5f
        yAxis.axisMinimum = 0f
        yAxis.setLabelCount(4, false)
        yAxis.setDrawAxisLine(false)
        //yAxis.setDrawLabels(false)
        lineChart.axisRight.isEnabled = false
    }

    private fun setLineChartData() {
        val entries1: MutableList<Entry> = ArrayList()
        val entries2: MutableList<Entry> = ArrayList()
        for (i in 0..6) entries1.add(Entry(i.toFloat(), (Math.random()*5f).toInt().toFloat()))
        for (i in 0..6) entries2.add(Entry(i.toFloat(), (Math.random()*5f).toInt().toFloat()))

        val data1 = LineDataSet(entries1, "Label")
        data1.setCircleColor(Color.BLUE)
        data1.setColor(Color.BLUE)
//        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
//        set2.setColor(Color.RED);
//        set2.setCircleColor(Color.WHITE);
//        set2.setLineWidth(2f);
//        set2.setCircleRadius(3f);
//        set2.setFillAlpha(65);
//        set2.setFillColor(Color.RED);
//        set2.setDrawCircleHole(false);
//        set2.setHighLightColor(Color.rgb(244, 117, 117));

        val data2 = LineDataSet(entries2, "Label")
        data2.setCircleColor(Color.RED)
        data2.setColor(Color.RED)

        val dataset = ArrayList<ILineDataSet>()
        dataset.add(data1)
        dataset.add(data2)

        val data = LineData(dataset)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        data.setDrawValues(true)

        lineChart.data = data
        lineChart.invalidate()
    }

    private fun initBarChart() {
        // set data
        // initBarChartData()

        setRandomResult()

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

    private fun initCandleStickChart() {
        // set data
        // initBarChartData()
//
//        randomResult()

        if(!sleepCurrent.isNullOrEmpty()) { initCandleStickChartData() }

        candleStickChart.isHighlightPerDragEnabled = false
        candleStickChart.description.isEnabled = false

        val yAxis = candleStickChart.axisLeft
        val rightAxis = candleStickChart.axisRight

//        yAxis.labelCount = 6
        yAxis.axisMaximum = 240000f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        rightAxis.axisMaximum = 240000f
        rightAxis.axisMinimum = 0f
        rightAxis.labelCount = 6
        rightAxis.setDrawLabels(true)
        candleStickChart.requestDisallowInterceptTouchEvent(true)

//        val rightActivities = arrayOf("00:00", "06:00", "12:00", "18:00", "24:00")
//        val rightFormatter = IAxisValueFormatter{ value, axis ->
//            rightActivities[value.toInt() % rightActivities.size]
//        }
//
//        rightAxis.valueFormatter = rightFormatter

//        candleStickChart.animateY(1400)


        val xAxis = candleStickChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.granularity = 1f
//        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)

        val mActivities = arrayOf("日", "一", "二", "三", "四", "五", "六")
        val xFormatter = IAxisValueFormatter{ value, axis ->
            mActivities[value.toInt() % mActivities.size]
        }
        xAxis.valueFormatter = xFormatter

        val l = candleStickChart.legend
        l.isEnabled = false
    }

    private fun initCandleStickChartData() {
        val entries: ArrayList<CandleEntry> = ArrayList()

        val tempCalendar = startCalendar.clone() as Calendar
        var tempDayCount = 0
        if(!sleepCurrent!!.first().isEmpty()) {
            for(sleepData in sleepCurrent!!) {
                while (!sleepData.isEqualDate(tempCalendar) && tempDayCount < 6) {
                    tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    tempDayCount += 1
                    entries.add(CandleEntry(tempDayCount.toFloat() - 1, -1f, -1f, -1f, -1f))
                }

                val startTimeRecord = TimeRecord().stringToTimeRecord(sleepData.StartDate)
                val endTimeRecord = TimeRecord().stringToTimeRecord(sleepData.EndDate)

                entries.add(
                    CandleEntry(
                        tempDayCount.toFloat(),
                        startTimeRecord.timeRecordToFloat(), endTimeRecord.timeRecordToFloat(),
                        startTimeRecord.timeRecordToFloat(), endTimeRecord.timeRecordToFloat()
                    )
                )

                Log.e("Chart Data", "$tempDayCount: $sleepData")
            }
        }

        while (tempDayCount < 7) {
            tempDayCount += 1
            entries.add(CandleEntry(tempDayCount.toFloat() - 1, -1f, -1f, -1f, -1f))
        }

        val candleDataSet = CandleDataSet(entries, "")
        candleDataSet.color = Color.BLUE
        candleDataSet.shadowColor = Color.LTGRAY
        candleDataSet.shadowWidth = 0.8f
        candleDataSet.decreasingColor = Color.RED
        candleDataSet.decreasingPaintStyle = Paint.Style.FILL
        candleDataSet.increasingColor = Color.CYAN
        candleDataSet.increasingPaintStyle = Paint.Style.FILL
        candleDataSet.neutralColor = Color.DKGRAY

        val candleData = CandleData(candleDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        candleData.setDrawValues(false)

        candleStickChart.data = candleData
        candleStickChart.invalidate()
    }

    private fun addBarEntry(entries: ArrayList<BarEntry>, index: Int, data: Int?) {
        if (data == null) entries.add(BarEntry(index.toFloat(), 0f))
        else entries.add(BarEntry(index.toFloat(), data.toFloat()))
    }

    private fun setRandomResult() {
        val entries: ArrayList<BarEntry> = ArrayList()
        for(i in 0 until 10) {
            addBarEntry(entries, i, (0..5).random())
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.color = Color.BLUE

        val barData = BarData(barDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        barDataSet.setDrawValues(false)

        barChart.data = barData
        barChart.invalidate()
    }

    private fun updateSleepChart() {
        activity?.runOnUiThread {
            binding.apply {
                initCandleStickChart()
            }
        }
    }

    private fun updateQuestionnaireChart() {
        activity?.runOnUiThread {
            binding.apply {
                initLineChart()
            }
        }
    }

    private fun getSleepCurrentApi(): Thread {
        return Thread {
            try {
                val url = URL(getString(R.string.get_sleep_record_url, getString(R.string.server_url),
                        User.caseNumber, currentStart, currentEnd, "ASC"))
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
                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

    private fun getQuestionnaireResultApi(): Thread {
        return Thread {
            try {
                val url = URL(getString(R.string.get_record_url, getString(R.string.server_url), CalendarFragment.User.caseNumber))
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? = object : TypeToken<List<Questions>>() {}.type
                    questionnaireResult = Gson().fromJson(inputStreamReader, type)

                    updateQuestionnaireChart()

                    inputStreamReader.close()
                    inputSystem.close()

                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch(e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

    private fun callApi() {
//        getSymptomsCurrentApi().start()
//        getDrugCurrentApi().start()
//        getDrugCurrentApi().start()
        getSleepCurrentApi().start()
//        getFoodCurrentApi().start()
//        getEventCurrentApi().start()
    }

    private fun updateCurrent() {
        currentStart = getString(R.string.input_time_format, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1, startCalendar[Calendar.DAY_OF_MONTH])
        currentEnd = getString(R.string.input_time_format, endCalendar[Calendar.YEAR], endCalendar[Calendar.MONTH]+1, endCalendar[Calendar.DAY_OF_MONTH])
    }
}