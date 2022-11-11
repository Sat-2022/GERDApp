package com.example.gerdapp.ui.chart

import android.content.SharedPreferences
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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.example.gerdapp.ui.chart.DailyChartFragment.DateRange.calendar
import com.example.gerdapp.ui.chart.DailyChartFragment.DateRange.current
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class DailyChartFragment: Fragment() {
    private var _binding: FragmentDailyChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var candleStickChart: CandleStickChart

    private var symptomList: List<SymptomCurrent>? = null
    private var drugList: List<DrugCurrent>? = null
    private var sleepList: List<SleepCurrent>? = null
    private var foodList: List<FoodCurrent>? = null
    private var eventList: List<EventCurrent>? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

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

        calendar = Calendar.getInstance()
        current = getString(R.string.input_time_format, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])

        callApi()
    }

    override fun onResume() {
        super.onResume()
        callApi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDailyChartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            selectedDateTv.text = getString(R.string.date_time_format_ch, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])

            rightArrow.setOnClickListener {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                current = getString(R.string.input_time_format, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
                selectedDateTv.text = getString(R.string.date_time_format_ch, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])

                callApi()
            }

            leftArrow.setOnClickListener {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                current = getString(R.string.input_time_format, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
                selectedDateTv.text = getString(R.string.date_time_format_ch, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])

                callApi()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

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
                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

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
            }
        }
    }

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
                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

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
                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

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
                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

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
            }
        }
    }

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
                    Log.e("API Connection", "Connection success")
                } else
                    Log.e("API Connection", "Connection failed")
            } catch (e: Exception) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

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
            }
        }
    }

    private fun callApi() {
        getSymptomsCurrentApi().start()
        getDrugCurrentApi().start()
        getDrugCurrentApi().start()
        getSleepCurrentApi().start()
        getFoodCurrentApi().start()
        getEventCurrentApi().start()
    }

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
}