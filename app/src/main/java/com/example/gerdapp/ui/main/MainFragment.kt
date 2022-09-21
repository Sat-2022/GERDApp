package com.example.gerdapp.ui.main

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.*
import com.example.gerdapp.adapter.CardItemAdapter
import com.example.gerdapp.data.*
import com.example.gerdapp.data.model.TimeRecord
import com.example.gerdapp.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.VISIBLE

    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var notificationRecyclerView: RecyclerView

    private var returnMachine: ReturnMachine? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    object User {
        var caseNumber = ""
        var gender = ""
        var nickname = ""
    }

    val COUGH = 0
    val HEART_BURN = 1
    val ACID_REFLUX = 2
    val CHEST_PAIN = 3
    val SOUR_MOUTH = 4
    val HOARSENESS = 5
    val APPETITE_LOSS = 6
    val STOMACH_GAS = 7

    val TOTAL_SYMPTOMS_NUM = 10

    private var symptomCurrent: SymptomCurrent? = null
    private var drugCurrent: DrugCurrent? = null
    private var sleepCurrent: SleepCurrent? = null
    private var foodCurrent: FoodCurrent? = null
    private var eventCurrent: EventCurrent? = null

    private object SymptomsRecord {
        var symptoms = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var startTime: TimeRecord = TimeRecord()
        var endTime: TimeRecord = TimeRecord()
    }

    private fun setRecord() {
        SymptomsRecord.symptoms = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        SymptomsRecord.startTime = TimeRecord()
        SymptomsRecord.endTime = TimeRecord()
    }

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        editor = preferences.edit()

        User.caseNumber = preferences.getString("caseNumber", "").toString()

        getMachineReturnApi().start()
        getSymptomCurrentApi().start()
        getDrugCurrentApi().start()
        getSleepCurrentApi().start()
        getFoodCurrentApi().start()
        getEventCurrentApi().start()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
//        getMachineReturnApi().start()
        updateMachineReturnTime()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.actionBar?.title = "hello world"

        mainRecyclerView = binding.mainRecyclerView

        val adapter = CardItemAdapter({ cardItem ->
            val action = when (cardItem.stringResourceId) {
                R.string.symptoms -> MainFragmentDirections.actionMainFragmentToSymptomsFragment()
                R.string.medicine -> MainFragmentDirections.actionMainFragmentToDrugRecordFragment()
                R.string.food -> MainFragmentDirections.actionMainFragmentToFoodFragment()
                R.string.sleep -> MainFragmentDirections.actionMainFragmentToSleepFragment()
                R.string.event -> MainFragmentDirections.actionMainFragmentToEventFragment()
                else -> MainFragmentDirections.actionMainFragmentSelf()
            }
            findNavController().navigate(action)
        }) { cardItem ->
            val recentRecord = when (cardItem.stringResourceId) {
                R.string.symptoms -> {
                    if(symptomCurrent != null) {
                        val timeRecord = TimeRecord().stringToTimeRecord(symptomCurrent!!.StartDate)
                        timeRecord.toString()
                    } else { "No Data" }
                }

                R.string.medicine -> {
                    if(drugCurrent != null) {
                        val timeRecord = TimeRecord().stringToTimeRecord(drugCurrent!!.MedicationTime)
                        timeRecord.toString()
                    } else { "No Data" }
                }

                R.string.sleep -> {
                    if(sleepCurrent != null) {
                        val timeRecord = TimeRecord().stringToTimeRecord(sleepCurrent!!.StartDate)
                        timeRecord.toString()
                    } else { "No Data" }
                }

                R.string.food -> {
                    if(foodCurrent != null) {
                        val timeRecord = TimeRecord().stringToTimeRecord(foodCurrent!!.StartDate)
                        timeRecord.toString()
                    } else { "No Data" }
                }

                R.string.event -> {
                    if(eventCurrent != null) {
                        val timeRecord = TimeRecord().stringToTimeRecord(eventCurrent!!.StartDate)
                        timeRecord.toString()
                    } else { "No Data" }
                }

                else -> "No Data"
            }
            recentRecord
        }

        mainRecyclerView.adapter = adapter

        notificationRecyclerView = binding.notificationRecyclerView

        binding.apply {
            val calendar = Calendar.getInstance()
            val current = calendar.time
            val formatDate = SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
            val currentDate = formatDate.format(current)
            testButton.setOnClickListener { view ->
                Snackbar.make(view, "", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }

            setSymptomsCard()

            val showNotification = preferences.getBoolean("showNotification", true)
            if(!showNotification) {
                notificationCard.visibility = View.GONE
                notificationHeadline.visibility = View.GONE
            }
        }
    }

    private fun isRecordEmpty(): Boolean {
        for(i in 0 until TOTAL_SYMPTOMS_NUM) {
            if(SymptomsRecord.symptoms[i] != 0) return false
        }
        return true
    }

    private fun postRecordApi(): Thread {
        return Thread {
            if(!isRecordEmpty()){
                try {
                    val url = URL(getString(R.string.post_symptoms_record_url, getString(R.string.server_url)))
                    val connection = url.openConnection() as HttpURLConnection

                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.doOutput = true
                    connection.doInput = true

                    val outputSystem = connection.outputStream
                    val outputStream = DataOutputStream(outputSystem)

                    val data: ByteArray = recordToJson()
                    outputStream.write(data)
                    outputStream.flush()
                    outputStream.close()
                    outputSystem.close()

                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val reader = BufferedReader(InputStreamReader(inputSystem))
                    val line: String = reader.readLine()
                    postUpdateUi(line)
                    inputStreamReader.close()
                    inputSystem.close()

                } catch (e: FileNotFoundException) {

                    Log.e("API Connection", "Service not found at ${e.message}")
                    Log.e("API Connection", e.toString())

                }
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(context, R.string.symptoms_added_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun recordToJson(): ByteArray {
        var recordString = "{"
        recordString += "\"CaseNumber\": \"${User.caseNumber}\", "
        recordString += "\"SymptomItem\": \"" + symptomsToString() + "\","
        recordString += "\"SymptomOther\": \"\","
        recordString += "\"StartDate\": \"" + getString(R.string.date_time_format, SymptomsRecord.startTime.YEAR, SymptomsRecord.startTime.MONTH+1, SymptomsRecord.startTime.DAY, SymptomsRecord.startTime.HOUR, SymptomsRecord.startTime.MIN, SymptomsRecord.startTime.SEC) + "\", "
        recordString += "\"EndDate\": \"" + getString(R.string.date_time_format, SymptomsRecord.endTime.YEAR, SymptomsRecord.endTime.MONTH+1, SymptomsRecord.endTime.DAY, SymptomsRecord.endTime.HOUR, SymptomsRecord.endTime.MIN, SymptomsRecord.endTime.SEC) + "\", "
        recordString += "\"SymptomNote\": \"\""
        recordString += "}"

        return recordString.encodeToByteArray()
    }

    private fun symptomsToString(): String {
        var symptomsString = ""

        for(i in 1..TOTAL_SYMPTOMS_NUM) {
            if(SymptomsRecord.symptoms[i-1] == 1) {
                symptomsString += "$i,"
            }
        }

        return symptomsString
    }

    private fun postUpdateUi(line: String) {
        activity?.runOnUiThread {
            binding.apply {
                if(line == "\"1\"") {
                    Toast.makeText(context, R.string.symptoms_added_successfully, Toast.LENGTH_SHORT).show()
                    setRecord()
                }else {
                    setRecord()
                    Toast.makeText(context, R.string.symptoms_added_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateMachineReturnTime() {
        activity?.runOnUiThread {
            binding.apply {
                val timeRecord = TimeRecord().stringToTimeRecord(returnMachine?.ReturnDate!!)

                cardItemRecentTime.text = timeRecord.toString()

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
                        .setMessage(getString(R.string.notification_message, timeRecord.toString()))
                        .setPositiveButton(R.string.notification_neutral_button) { dialog, _ ->
                            dialog.dismiss()
                            if(notificationClosed) {
                                notificationCard.visibility = View.GONE
                                notificationHeadline.visibility = View.GONE
                            }
                        }
//                        .setOnDismissListener {
//                            (checkBoxView.parent as ViewGroup).removeView(checkBoxView)
//                        }
                    dialogBuilder.create()
                    dialogBuilder.show()
                }
            }
        }
    }

    private fun getMachineReturnApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_return_machine_url, getString(R.string.server_url), User.caseNumber))
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

    private fun setSymptomsCard() {
        binding.apply {
            symptomsButtons.symptomsCough.setOnClickListener {
                if(SymptomsRecord.symptoms[COUGH] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[COUGH] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[COUGH] = 0
                }
            }
            symptomsButtons.symptomsHeartBurn.setOnClickListener{
                if(SymptomsRecord.symptoms[HEART_BURN] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[HEART_BURN] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[HEART_BURN] = 0
                }
            }
            symptomsButtons.symptomsAcidReflux.setOnClickListener {
                if(SymptomsRecord.symptoms[ACID_REFLUX] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[ACID_REFLUX] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[ACID_REFLUX] = 0
                }
            }
            symptomsButtons.symptomsChestPain.setOnClickListener{
                if(SymptomsRecord.symptoms[CHEST_PAIN] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[CHEST_PAIN] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[CHEST_PAIN] = 0
                }
            }
            symptomsButtons.symptomsSourMouth.setOnClickListener {
                if(SymptomsRecord.symptoms[SOUR_MOUTH] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[SOUR_MOUTH] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[SOUR_MOUTH] = 0
                }
            }
            symptomsButtons.symptomsHoarseness.setOnClickListener{
                if(SymptomsRecord.symptoms[HOARSENESS] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[HOARSENESS] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[HOARSENESS] = 0
                }
            }
            symptomsButtons.symptomsAppetiteLoss.setOnClickListener {
                if(SymptomsRecord.symptoms[APPETITE_LOSS] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[APPETITE_LOSS] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[APPETITE_LOSS] = 0
                }
            }
            symptomsButtons.symptomsStomachGas.setOnClickListener{
                if(SymptomsRecord.symptoms[STOMACH_GAS] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[STOMACH_GAS] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[STOMACH_GAS] = 0
                }
            }

            addSymptoms.setOnClickListener {
                val calendar = Calendar.getInstance()

                SymptomsRecord.startTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
                    calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], calendar[Calendar.SECOND])
                SymptomsRecord.endTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
                    calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]+1, calendar[Calendar.SECOND])

                postRecordApi().start()

                symptomsButtons.symptomsCough.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsHeartBurn.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsAcidReflux.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsChestPain.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsSourMouth.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsHoarseness.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsAppetiteLoss.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsStomachGas.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun getSymptomCurrentApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_symptoms_record_url, getString(R.string.server_url), User.caseNumber))
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<SymptomCurrent>>() {}.type
                val symptomData: List<SymptomCurrent> = Gson().fromJson(inputStreamReader, type)

                try {
                    symptomCurrent = symptomData.first()
                } catch (e: Exception) {
                    // TODO: Catch exception when no data
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$symptomCurrent")
            } else
                Log.e("API Connection", "failed")
        }
    }

    private fun getDrugCurrentApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_drug_record_url, getString(R.string.server_url), User.caseNumber))
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<DrugCurrent>>() {}.type
                val drugData: List<DrugCurrent> = Gson().fromJson(inputStreamReader, type)

                try {
                    drugCurrent = drugData.first()
                } catch (e: Exception) {
                    // TODO: Catch exception when no data
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$drugCurrent")
            } else
                Log.e("API Connection", "failed")
        }
    }

    private fun getSleepCurrentApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_sleep_record_url, getString(R.string.server_url), User.caseNumber))
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<SleepCurrent>>() {}.type
                val sleepData: List<SleepCurrent> = Gson().fromJson(inputStreamReader, type)

                try {
                    sleepCurrent = sleepData.first()
                } catch (e: Exception) {
                    // TODO: Catch exception when no data
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$sleepCurrent")
            } else
                Log.e("API Connection", "failed")
        }
    }

    private fun getFoodCurrentApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_food_record_url, getString(R.string.server_url), User.caseNumber))
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<FoodCurrent>>() {}.type
                val foodData: List<FoodCurrent> = Gson().fromJson(inputStreamReader, type)

                try {
                    foodCurrent = foodData.first()
                } catch (e: Exception) {
                    // TODO: Catch exception when no data
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$foodCurrent")
            } else
                Log.e("API Connection", "failed")
        }
    }

    private fun getEventCurrentApi(): Thread {
        return Thread {
            val url = URL(getString(R.string.get_event_record_url, getString(R.string.server_url), User.caseNumber))
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<EventCurrent>>() {}.type
                val eventData: List<EventCurrent> = Gson().fromJson(inputStreamReader, type)

                try {
                    eventCurrent = eventData.first()
                } catch (e: Exception) {
                    // TODO: Catch exception when no data
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$eventCurrent")
            } else
                Log.e("API Connection", "failed")
        }
    }
}