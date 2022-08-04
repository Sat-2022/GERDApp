package com.example.gerdapp.ui.chart

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentChartBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


class ChartFragment: Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var candleStickChart: CandleStickChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        lineChart = binding.lineChart
        barChart = binding.barChart
        candleStickChart = binding.timeRangeChart
        initLineChart()
        initBarChart()
        initCandleStickChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
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

        randomResult()

        candleStickChart.isHighlightPerDragEnabled = true

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

    private fun randomResult(){
        val entries: ArrayList<CandleEntry> = ArrayList()
        entries.add(CandleEntry(0f, 225.0f, 219.84f, 225.0f, 219.84f))
        entries.add(CandleEntry(1f, 228.35f, 222.57f, 228.35f, 222.57f))
        entries.add(CandleEntry(2f, 226.84f,  222.52f, 226.84f,  222.52f))
        entries.add(CandleEntry(3f, 222.95f, 217.27f, 222.95f, 217.27f))
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
}