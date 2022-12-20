package com.example.gerdapp.ui.calendar

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.*
import com.example.gerdapp.data.Questions
import com.example.gerdapp.databinding.FragmentCalendarBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CalendarFragment: Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.VISIBLE
    private var actionbarTitleEnable = true

    private lateinit var barChart: BarChart

    var questions: List<Questions>? = null

    private var currentResult: Questions? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    data class QuestionnaireStatus(
        val ResultContent: String
    )

    object User {
        var caseNumber = ""
        var gender = ""
        var nickname = ""
    }

    private var currentQuestionnaireStatus: QuestionnaireStatus ?= null

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)

        mainActivity.setActionBarTitleEnable(actionbarTitleEnable)
        mainActivity.setActionBarTitle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        editor = preferences.edit()

        User.caseNumber = preferences.getString("caseNumber", "").toString()
        // Connect to Api
        getQuestionnaireResultApi().start()
        getQuestionnaireStatus().start()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
//        testApi().start()
//        getMachineReturnApi().start()
        updateBarChart()
        updateQuestionnaireStatus()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        barChart = binding.barChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            dailyQuestionnaire.layout.setOnClickListener {
                findNavController().navigate(R.id.action_calendarFragment_to_questionnaireFragment)
            }

            dailyQuestionnaire.cardItemTitle.text = getString(R.string.daily_questionnaire)
            dailyQuestionnaire.cardItemRecentTime.text = "點選進入問卷頁面"
            dailyQuestionnaire.cardItemIcon.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_text_snippet_24))
            dailyQuestionnaire.cardItemIcon.setColorFilter(Color.parseColor("#09ADEA"))
        }
    }

    private fun updateQuestionnaireStatus() {
        activity?.runOnUiThread {
            binding.apply {
                if(currentQuestionnaireStatus?.ResultContent == "1") {
                    dailyQuestionnaire.layout.visibility = View.GONE
                    tvQuestionnaireComplete.visibility = View.VISIBLE
                }
                else {
                    dailyQuestionnaire.layout.visibility = View.VISIBLE
                    tvQuestionnaireComplete.visibility = View.GONE
                }
            }
        }
    }

    private fun getQuestionnaireStatus(): Thread {
        return Thread {
            try {
                val url = URL(
                    getString(
                        R.string.get_questionnaire_status_url,
                        getString(R.string.server_url),
                        User.caseNumber
                    )
                )
                val connection = url.openConnection() as HttpURLConnection

                Log.e("questionnaire status", User.caseNumber)

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? =
                        object : TypeToken<List<QuestionnaireStatus>>() {}.type
                    val list: List<QuestionnaireStatus> = Gson().fromJson(inputStreamReader, type)

                    currentQuestionnaireStatus = list.first()
                    updateQuestionnaireStatus()

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

    private fun dateTimeString(dateTime: String?): String{
        var formatted = ""

        // 0123456789012345678901
        // yyyy-mm-ddTHH:mm:ss.ss
        if(dateTime != null){
            for (i in 0..21) {
                if (i == 5 && dateTime[i] != '0') formatted += dateTime[i]
                else if (i == 6) formatted += dateTime[i] + " " + getString(R.string.month) + " "
                else if (i == 8 && dateTime[i] != '0') formatted += dateTime[i]
                else if (i == 9) formatted += dateTime[i] + " " + getString(R.string.date)
            }
        }

        return formatted
    }

    private fun getQuestionnaireResultApi(): Thread {
        return Thread {
            try {
                val url = URL(getString(
                        R.string.get_record_url,
                        getString(R.string.server_url),
                        User.caseNumber
                    ))
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? =
                        object : TypeToken<List<Questions>>() {}.type
                    questions = Gson().fromJson(inputStreamReader, type)

                    currentResult = questions?.first()
                    updateBarChart()

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

    private fun updateBarChart() {
        activity?.runOnUiThread {
            binding.apply {
                initBarChart()
                dateOfData.text = formatDate(currentResult?.QuestionDate)
            }
        }
    }

    private fun formatDate(date: String?): String {
        var formatted = ""

        if (date != null) {
            for (i in 0..7) {
                if (i in 0..2) formatted += date[i]
                else if (i == 3) formatted += date[i] + " 年 "
                else if (i == 4) {
                    if (date[i] == '0') formatted = formatted
                    else formatted += date[i]
                }
                else if (i == 5) formatted += date[i] + " 月 "
                else if (i == 6) {
                    if (date[i] == '0') formatted = formatted
                    else formatted += date[i]
                }
                else if (i == 7) formatted += date[i] + " 日 問卷結果"
            }
            return formatted
        }
        else return "Unavailable"
    }

    private fun initBarChart() {
        // set data
        initBarChartData()

        barChart.setBackgroundColor(Color.WHITE)
        barChart.description.isEnabled = false
//        chart.setTouchEnabled(false)
//        chart.isDragEnabled = false


        // add animation
//        barChart.animateY(1400)

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
        barDataSet.color = Color.rgb(147, 208, 109)

        val barData = BarData(barDataSet)

        /*val mv = RadarMarkerView(this, R.layout.radar_markerview, entries)
        mv.chartView = lineChart
        lineChart.marker = mv*/

        barData.setDrawValues(true)

        barChart.data = barData
        barChart.invalidate()
    }
}