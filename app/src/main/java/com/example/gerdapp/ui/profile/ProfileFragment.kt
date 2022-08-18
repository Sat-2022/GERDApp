package com.example.gerdapp.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gerdapp.LoginActivity
import com.example.gerdapp.Questions
import com.example.gerdapp.R
import com.example.gerdapp.UserData
import com.example.gerdapp.databinding.FragmentProfileBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var barChart: BarChart

    private var postResult = ""

    var questions: List<Questions>? = null

    private var currentResult: Questions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        testApi().start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        barChart = binding.barChart
//        initBarChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            startTestPost.setOnClickListener {
                postApi().start()
            }
            login.setOnClickListener {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun testApi(): Thread {
        return Thread {
            val url = URL("http://120.126.40.203/GERD_API/api/test/${UserData.userNo}&20220801")
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<Questions>>() {}.type
                questions = Gson().fromJson(inputStreamReader, type)
                currentResult = questions?.first()
                UpdateUI()
                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$questions")
            } else
                Log.e("API Connection", "failed")
        }
    }

    private fun postApi(): Thread {
        return Thread {
            val url = URL("http://120.126.40.203/GERD_API/api/Test/")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true

            val outputSystem = connection.outputStream
            val outputStream = DataOutputStream(outputSystem)
            val jsonString = "{\n" +
                    "    \"CaseNumber\": \"\"\n" +
                    "}"

            outputStream.writeBytes(jsonString)
            outputStream.flush()
            outputStream.close()
            outputSystem.close()


            val inputSystem = connection.inputStream
            val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
            val reader = BufferedReader(InputStreamReader(inputSystem))

            val line: String = reader.readLine()
            postResult = line
            postUpdateUI()
            inputStreamReader.close()
            inputSystem.close()
        }
    }

    private fun postUpdateUI() {
        activity?.runOnUiThread {
            binding.apply {
                testApi.text = postResult
            }
        }
    }

    private fun UpdateUI() {
        activity?.runOnUiThread {
            binding.apply {
                testApi.text = questions.toString()
                initBarChart()
            }
        }
    }


    private fun initBarChart() {
        // set data
        initBarChartData()

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

    private fun addBarEntry(entries: ArrayList<BarEntry>, index: Int, data: Int?) {
        if (data == null) entries.add(BarEntry(index.toFloat(), 0f))
        else entries.add(BarEntry(index.toFloat(), data.toFloat()))
    }

    private fun initBarChartData() {
        val entries: ArrayList<BarEntry> = ArrayList()

        Log.e("API", currentResult?.Question01?.toInt().toString())

        addBarEntry(entries, 0, currentResult?.Question01?.toInt())
        addBarEntry(entries, 1, currentResult?.Question02?.toInt())
        addBarEntry(entries, 2, currentResult?.Question03?.toInt())
        addBarEntry(entries, 3, currentResult?.Question04?.toInt())
        addBarEntry(entries, 4, currentResult?.Question05?.toInt())
        addBarEntry(entries, 5, currentResult?.Question06?.toInt())
        addBarEntry(entries, 6, currentResult?.Question07?.toInt())
        addBarEntry(entries, 7, currentResult?.Question08?.toInt())
        addBarEntry(entries, 8, currentResult?.Question09?.toInt())
        addBarEntry(entries, 9, currentResult?.Question10?.toInt())

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
}