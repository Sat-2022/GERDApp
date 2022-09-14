package com.example.gerdapp.ui.calendar

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
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

    private var returnMachine: ReturnMachine? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Connect to Api
        testApi().start()
        getMachineReturnApi().start()

        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        editor = preferences.edit()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
//        testApi().start()
//        getMachineReturnApi().start()
        updateUi()
        updateMachineReturnTime()
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

            val showNotification = preferences.getBoolean("showNotification", true)
            if(!showNotification) {
                notificationCard.visibility = View.GONE
                notificationHeadline.visibility = View.GONE
            }

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

    private fun updateMachineReturnTime() {
        activity?.runOnUiThread {
            binding.apply {
                val returnTime = dateTimeString(returnMachine?.ReturnDate)

                cardItemRecentTime.text = returnTime

                notificationCard.setOnClickListener {
                    // val popupWindow = PopupWindow(layoutInflater.inflate(R.layout.pop_up_window))
                    var notificationClosed = false
                    val inflater = requireActivity().layoutInflater
                    val checkBoxView = inflater.inflate(R.layout.checkbox, null)
                    val checkBox = checkBoxView.findViewById<CheckBox>(R.id.checkbox)
                    checkBox.setOnCheckedChangeListener { compoundButton, b ->
                        editor.putBoolean("showNotification", !b)
                        editor.commit()
                        notificationClosed = b
                    }

                    val dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setView(checkBoxView)
                        .setTitle(R.string.notification_title)
                        .setMessage(getString(R.string.notification_message, returnTime))
                        .setPositiveButton(R.string.notification_neutral_button) { dialog, _ ->
                            dialog.dismiss()
                            if(notificationClosed) {
                                notificationCard.visibility = View.GONE
                                notificationHeadline.visibility = View.GONE
                            }
                        }
                    dialogBuilder.create()
                    dialogBuilder.show()
                }
            }
        }
    }

    private fun getMachineReturnApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_return_machine_url, getString(R.string.server_url), "R099"))
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<ReturnMachine>>() {}.type
                val list: List<ReturnMachine> = Gson().fromJson(inputStreamReader, type)
                try{
                    returnMachine = list.first()
                    updateMachineReturnTime()
                } catch (e: Exception) {
                    // TODO: Handle exception
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$returnMachine")
            } else
                Log.e("API Connection", "failed")
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


    private fun testApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_record_url, getString(R.string.server_url), "T010", "20220801"))
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<Questions>>() {}.type
                questions = Gson().fromJson(inputStreamReader, type)
                try{
                    currentResult = questions?.first()
                    updateUi()
                } catch (e: Exception) {
                    // TODO: Handle empty list exception
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$questions")
            } else
                Log.e("API Connection", "failed")
        }
    }

    private fun updateUi() {
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