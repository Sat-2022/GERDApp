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
import com.example.gerdapp.data.SleepCurrent
import com.example.gerdapp.data.TimeRecord
import com.example.gerdapp.databinding.FragmentMonthlyChartBinding
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.endCalendar
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.startCalendar
import com.example.gerdapp.ui.chart.MonthlyChartFragment.User.caseNumber
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.currentEnd
import com.example.gerdapp.ui.chart.MonthlyChartFragment.DateRange.currentStart
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

class MonthlyChartFragment: Fragment() {
    private var _binding: FragmentMonthlyChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var candleStickChart: CandleStickChart

    private var sleepCurrent: List<SleepCurrent>? = null

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

        caseNumber = preferences.getString("caseNumber", "").toString()

        startCalendar = Calendar.getInstance()
        endCalendar = Calendar.getInstance()
        updateCalendar()
        updateCurrent()

        callApi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMonthlyChartBinding.inflate(inflater, container, false)

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
            selectedDateTv.text = (startCalendar[Calendar.MONTH]+1).toString() + " 月"

            rightArrow.setOnClickListener {
                startCalendar.add(Calendar.MONTH, 1)
                endCalendar.add(Calendar.MONTH, 1)
                updateCalendar()
                updateCurrent()
                selectedDateTv.text = (startCalendar[Calendar.MONTH]+1).toString() + " 月"

                callApi()
            }

            leftArrow.setOnClickListener {
                startCalendar.add(Calendar.MONTH, -1)
                endCalendar.add(Calendar.MONTH, -1)
                updateCalendar()
                updateCurrent()
                selectedDateTv.text = (startCalendar[Calendar.MONTH]+1).toString() + " 月"

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

    private fun initCandleStickChartData() {
        val entries: ArrayList<CandleEntry> = ArrayList()

        for(i in 0 until sleepCurrent!!.size) {
            Log.e("Entries", "$i")
            val startTimeRecord = TimeRecord().stringToTimeRecord(sleepCurrent!![i].StartDate)
            val endTimeRecord = TimeRecord().stringToTimeRecord(sleepCurrent!![i].EndDate)

            entries.add(
                CandleEntry(
                    i.toFloat(),
                    startTimeRecord.timeRecordToFloat(), endTimeRecord.timeRecordToFloat(),
                    startTimeRecord.timeRecordToFloat(), endTimeRecord.timeRecordToFloat()
                )
            )
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

        candleData.setDrawValues(true)

        candleStickChart.data = candleData
        candleStickChart.invalidate()
    }

    private fun initCandleStickChart() {
        // set data
        // initBarChartData()

//        randomResult()

        if(!sleepCurrent.isNullOrEmpty()) { initCandleStickChartData() }

        candleStickChart.isHighlightPerDragEnabled = false

        val yAxis = candleStickChart.axisLeft
        val rightAxis = candleStickChart.axisRight
        yAxis.setDrawGridLines(false)
        rightAxis.setDrawGridLines(false)
        candleStickChart.requestDisallowInterceptTouchEvent(true)

//        candleStickChart.animateY(1400)

        val xAxis = candleStickChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines

        xAxis.setDrawLabels(false)
        rightAxis.textColor = Color.WHITE
        yAxis.setDrawLabels(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)

        val l = candleStickChart.legend
        l.isEnabled = false
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

    private fun getSleepCurrentApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_sleep_record_url, getString(R.string.server_url), caseNumber, currentStart, currentEnd, "ASC"))
            val connection = url.openConnection() as HttpURLConnection

            Log.e("API Connection", "$url")

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<SleepCurrent>>() {}.type
                sleepCurrent = Gson().fromJson(inputStreamReader, type)
                try{
                    updateSleepChart()
                } catch (e: Exception) {
                    // TODO: Handle exception
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$sleepCurrent")
            } else
                Log.e("API Connection", "failed")
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

    private fun updateCalendar() {
        startCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val maxDate = startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        endCalendar.set(Calendar.DAY_OF_MONTH, maxDate)

        Log.e("Date", "${startCalendar.time} ~ ${endCalendar.time}")
    }

    private fun updateCurrent() {
        currentStart = getString(R.string.input_time_format, startCalendar[Calendar.YEAR], startCalendar[Calendar.MONTH]+1, startCalendar[Calendar.DAY_OF_MONTH])
        currentEnd = getString(R.string.input_time_format, endCalendar[Calendar.YEAR], endCalendar[Calendar.MONTH]+1, endCalendar[Calendar.DAY_OF_MONTH])
    }
}