package com.example.gerdapp.ui.calendar

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.*
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

    private lateinit var barChart: BarChart

    var questions: List<Questions>? = null

    private var currentResult: Questions? = null

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Connect to Api
        testApi().start()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
//        testApi().start()
        UpdateUI()
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

            notificationCard.setOnClickListener {
                // val popupWindow = PopupWindow(layoutInflater.inflate(R.layout.pop_up_window))
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setTitle("繳回機器通知")
                    .setMessage("提醒您，預計於 8 月 8 日須繳回機器\n（中正樓十三樓內視鏡診斷與治療中心）")
                    .setNeutralButton("我知道了") { dialogBuilder, id ->
                        dialogBuilder.dismiss()
                    }
                dialogBuilder.create()
                dialogBuilder.show()
            }

            weeklyQuestionnaire.layout.setOnClickListener{
                findNavController().navigate(R.id.action_calendarFragment_to_questionnaireFragment)
            }
            dailyQuestionnaire.layout.setOnClickListener {
                findNavController().navigate(R.id.action_calendarFragment_to_questionnaireFragment)
            }

//            if(!Notification.notificationOn) {
//                notification.layout.visibility = View.GONE
//            } else {
//                notification.layout.visibility = View.VISIBLE
//            }
//
//            notification.cancelButton.setOnClickListener {
//                notification.layout.visibility = View.GONE
//                Notification.notificationOn = false
//            }

//            notification.cardItemTitle.text = "繳回機器通知"
//            notification.cardItemRecentTime.text = "8 月 15 日"
//            notification.cardItemIcon.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_info_24))
//            notification.cardItemIcon.setColorFilter(Color.parseColor("#F12B2B"))

            weeklyQuestionnaire.cardItemTitle.text = getString(R.string.weekly_questionnaire)
            weeklyQuestionnaire.cardItemRecentTime.text = "8 月 6 日"
            weeklyQuestionnaire.cardItemIcon.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_text_snippet_24))
            weeklyQuestionnaire.cardItemIcon.setColorFilter(Color.parseColor("#09ADEA"))
            weeklyQuestionnaire.layout.visibility = View.GONE

            dailyQuestionnaire.cardItemTitle.text = getString(R.string.daily_questionnaire)
            dailyQuestionnaire.cardItemRecentTime.text = "8 月 5 日"
            dailyQuestionnaire.cardItemIcon.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_text_snippet_24))
            dailyQuestionnaire.cardItemIcon.setColorFilter(Color.parseColor("#09ADEA"))
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

    private fun UpdateUI() {
        activity?.runOnUiThread {
            binding.apply {
                initBarChart()
                dateOfData.text = formatDate(currentResult?.QuestionDate)
            }
        }
    }

    fun formatDate(date: String?): String {
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
                else if (i == 7) formatted += date[i] + " 日 "
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