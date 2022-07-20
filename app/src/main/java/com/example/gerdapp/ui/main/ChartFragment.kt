package com.example.gerdapp.ui.main

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentChartBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartFragment: Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        lineChart = binding.lineChart
        initLineChart()

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
}