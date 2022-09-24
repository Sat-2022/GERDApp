package com.example.gerdapp.ui.main.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.data.model.TimeRecord
import com.example.gerdapp.databinding.FragmentSymptomsRecordBinding
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class SymptomsRecordFragment: Fragment() {
    private var _binding: FragmentSymptomsRecordBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE
    private var actionbarTitleEnable = false

    private lateinit var preferences: SharedPreferences

    val COUGH = 0
    val HEART_BURN = 1
    val ACID_REFLUX = 2
    val CHEST_PAIN = 3
    val SOUR_MOUTH = 4
    val HOARSENESS = 5
    val APPETITE_LOSS = 6
    val STOMACH_GAS = 7

    val TOTAL_SYMPTOMS_NUM = 10

    private object SymptomsRecord {
        var symptoms = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var othersSymptoms: String? = null
        var startTime: TimeRecord = TimeRecord()
        var endTime: TimeRecord = TimeRecord()
        var note: String? = null
    }

    private fun setRecord() {
        SymptomsRecord.symptoms = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        SymptomsRecord.note = null
        SymptomsRecord.startTime = TimeRecord()
        SymptomsRecord.endTime = TimeRecord()
    }

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
        mainActivity.setActionBarExpanded(false)
        mainActivity.setActionBarTitleEnable(actionbarTitleEnable)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setBottomNavigationVisibility()
        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSymptomsRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            noteCard.addNote.userInputText.hint = getString(R.string.add_note)
            symptomsCard.addOtherSymptoms.userInputText.hint = getString(R.string.symptoms_record_add_other_symptom)

            symptomsCard.addOtherSymptoms.userInputText.setOnEditorActionListener { textView, actionId, keyEvent ->
                return@setOnEditorActionListener when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if(validateInputText(textView)) {
                            SymptomsRecord.othersSymptoms = textView.text.toString()
                        }
                        false
                    }
                    else -> false
                }
            }

            noteCard.addNote.userInputText.setOnEditorActionListener { textView, actionId, keyEvent ->
                return@setOnEditorActionListener when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if(validateInputText(textView)) {
                             SymptomsRecord.note = textView.text.toString()
                        }
                        false
                    }
                    else -> false
                }
            }

            completeButton.setOnClickListener {
                postRecordApi().start()
            }

            symptomsCard.addSymptomsButton.setOnClickListener {
                symptomsCard.addOtherSymptoms.layout.visibility = View.VISIBLE
                symptomsCard.addSymptomsButton.visibility = View.GONE
            }
            noteCard.addNoteButton.setOnClickListener {
                noteCard.addNote.layout.visibility = View.VISIBLE
                noteCard.addNoteButton.visibility = View.GONE
            }

            symptomsCard.addOtherSymptoms.cancel.setOnClickListener {
                symptomsCard.addOtherSymptoms.layout.visibility = View.GONE
                symptomsCard.addSymptomsButton.visibility = View.VISIBLE
                symptomsCard.addOtherSymptoms.userInputText.text = null
                symptomsCard.addOtherSymptoms.userInputText.error = null
                SymptomsRecord.othersSymptoms = null
            }

            noteCard.addNote.cancel.setOnClickListener {
                noteCard.addNote.layout.visibility = View.GONE
                noteCard.addNoteButton.visibility = View.VISIBLE
                noteCard.addNote.userInputText.text = null
                noteCard.addNote.userInputText.error = null
                SymptomsRecord.note = null
            }
        }

        initDateTimeText()
        setDateTimePicker()
        setSymptomsCard()
    }

    private fun isRecordEmpty(): Boolean {
        for(i in 0 until TOTAL_SYMPTOMS_NUM) {
            if(SymptomsRecord.symptoms[i] != 0) return false
        }

        return SymptomsRecord.othersSymptoms.isNullOrBlank() || SymptomsRecord.startTime.isTimeRecordEmpty() || SymptomsRecord.endTime.isTimeRecordEmpty()
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
        val caseNumber = preferences.getString("caseNumber", "")
        var recordString = "{"
        recordString += "\"CaseNumber\": \"$caseNumber\", "
        recordString += "\"SymptomItem\": \"" + symptomsToString() + "\","
        recordString += "\"SymptomOther\": \"${SymptomsRecord.othersSymptoms}\","
        recordString += "\"StartDate\": \"" + getString(R.string.date_time_format, SymptomsRecord.startTime.YEAR, SymptomsRecord.startTime.MONTH+1, SymptomsRecord.startTime.DAY, SymptomsRecord.startTime.HOUR, SymptomsRecord.startTime.MIN, SymptomsRecord.startTime.SEC) + "\", "
        recordString += "\"EndDate\": \"" + getString(R.string.date_time_format, SymptomsRecord.endTime.YEAR, SymptomsRecord.endTime.MONTH+1, SymptomsRecord.endTime.DAY, SymptomsRecord.endTime.HOUR, SymptomsRecord.endTime.MIN, SymptomsRecord.endTime.SEC) + "\", "
        recordString += "\"SymptomNote\": \"${SymptomsRecord.note}\""
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
                    findNavController().navigate(R.id.action_symptomsFragment_to_mainFragment)
                }else {
                    setRecord()
                    Toast.makeText(context, R.string.symptoms_added_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputText(textView: TextView): Boolean {
        if(textView.text.length > 20) {
            textView.error = getString(R.string.error_input_too_long)
            return false
        }
        if(isInvalidString(textView.text.toString()))
            textView.error = getString(R.string.error_invalid_input)
        return true
    }

    private fun isInvalidString(string: String): Boolean {
        if(string.contains(getString(R.string.check_input_valid).toRegex())) {
            return true
        }
        return false
    }

    private fun setDateTimePicker() {
        binding.apply {
            timeCard.startDate.setOnClickListener {
                setDatePicker(timeCard.startDate, SymptomsRecord.startTime, 0).show()
            }

            timeCard.endDate.setOnClickListener {
                setDatePicker(timeCard.endDate, SymptomsRecord.endTime, 0).show()
            }

            timeCard.startTime.setOnClickListener {
                setTimePicker(timeCard.startTime, SymptomsRecord.startTime, 0).show()
            }

            timeCard.endTime.setOnClickListener {
                setTimePicker(timeCard.endTime, SymptomsRecord.endTime, 0).show()
            }
        }
    }

    private fun setSymptomsCard() {
        binding.apply {
            symptomsCard.symptomsButtons.symptomsCough.setOnClickListener {
                if(SymptomsRecord.symptoms[COUGH] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[COUGH] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[COUGH] = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsHeartBurn.setOnClickListener{
                if(SymptomsRecord.symptoms[HEART_BURN] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[HEART_BURN] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[HEART_BURN] = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsAcidReflux.setOnClickListener {
                if(SymptomsRecord.symptoms[ACID_REFLUX] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[ACID_REFLUX] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[ACID_REFLUX] = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsChestPain.setOnClickListener{
                if(SymptomsRecord.symptoms[CHEST_PAIN] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[CHEST_PAIN] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[CHEST_PAIN] = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsSourMouth.setOnClickListener {
                if(SymptomsRecord.symptoms[SOUR_MOUTH] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[SOUR_MOUTH] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[SOUR_MOUTH] = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsHoarseness.setOnClickListener{
                if(SymptomsRecord.symptoms[HOARSENESS] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[HOARSENESS] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[HOARSENESS] = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsAppetiteLoss.setOnClickListener {
                if(SymptomsRecord.symptoms[APPETITE_LOSS] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[APPETITE_LOSS] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[APPETITE_LOSS] = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsStomachGas.setOnClickListener{
                if(SymptomsRecord.symptoms[STOMACH_GAS] == 0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.symptoms[STOMACH_GAS] = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.symptoms[STOMACH_GAS] = 0
                }
            }
        }
    }

    private fun setDatePicker(textView: TextView, timeRecord: TimeRecord, tag: Int = 0): DatePickerDialog {
        val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
            run {
                textView.text = getString(R.string.date_format, year, month+1, day)
                timeRecord.YEAR = year
                timeRecord.MONTH = month
                timeRecord.DAY = day
            }
        }, timeRecord.YEAR, timeRecord.MONTH, timeRecord.DAY)

        return datePicker
    }

    private fun setTimePicker(textView: TextView, timeRecord: TimeRecord, tag: Int = 0): TimePickerDialog {
        val timePicker = TimePickerDialog(requireContext(), { _, hour, min ->
            run {
                textView.text = getString(R.string.time_format, hour, min)
                timeRecord.HOUR = hour
                timeRecord.MIN = min
                timeRecord.SEC = 0
            }
        }, timeRecord.HOUR, timeRecord.MIN, true)

        return timePicker
    }

    private fun initDateTimeText() {
        val calendar = Calendar.getInstance()

        binding.apply {
            timeCard.startDate.text = getString(R.string.date_format, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
            timeCard.endDate.text = getString(R.string.date_format, calendar[Calendar.YEAR], calendar[Calendar.MONTH]+1, calendar[Calendar.DAY_OF_MONTH])
            timeCard.startTime.text = getString(R.string.time_format, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE])
            timeCard.endTime.text = getString(R.string.time_format, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]+1)
        }

        SymptomsRecord.startTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], calendar[Calendar.SECOND])
        SymptomsRecord.endTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]+1, calendar[Calendar.SECOND])
    }
}