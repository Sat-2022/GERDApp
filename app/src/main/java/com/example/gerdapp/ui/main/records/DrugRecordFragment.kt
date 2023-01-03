package com.example.gerdapp.ui.main.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.data.TimeRecord
import com.example.gerdapp.databinding.FragmentDrugRecordBinding
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class DrugRecordFragment: Fragment() {
    private var _binding: FragmentDrugRecordBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE
    private var actionbarTitleEnable = false

    private lateinit var preferences: SharedPreferences

    private object DrugRecord {
        var drug: String? = null
        var note: String? = null
        var startTime: TimeRecord = TimeRecord()
        var endTime: TimeRecord = TimeRecord()
    }

    private fun setRecord() {
        DrugRecord.drug = null
        DrugRecord.note = null
        DrugRecord.startTime = TimeRecord()
        DrugRecord.endTime = TimeRecord()
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
        setRecord()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
//        setRecord()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDrugRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.menu_settings).isVisible = false
//        menu.setGroupVisible(R.id.menu_group, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            timeCard.endLayout.visibility = View.GONE

            noteCard.addNote.userInputText.hint = getString(R.string.add_note)
            drugCard.addDrug.userInputText.hint = getString(R.string.drug_record_add_drug)

            drugCard.addDrug.userInputText.setOnEditorActionListener { textView, actionId, keyEvent ->
                return@setOnEditorActionListener when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if(validateInputText(textView)) {
                            DrugRecord.drug = textView.text.toString()
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
                            DrugRecord.note = textView.text.toString()
                        }
                        false
                    }
                    else -> false
                }
            }

            completeButton.setOnClickListener {
                saveRecord()
                postRecordApi().start()
            }

            drugCard.addDrugButton.setOnClickListener {
                drugCard.addDrug.layout.visibility = View.VISIBLE
                drugCard.addDrugButton.visibility = View.GONE
            }

            noteCard.addNoteButton.setOnClickListener {
                noteCard.addNote.layout.visibility = View.VISIBLE
                noteCard.addNoteButton.visibility = View.GONE
            }

            drugCard.addDrug.cancel.setOnClickListener {
                drugCard.addDrug.layout.visibility = View.GONE
                drugCard.addDrugButton.visibility = View.VISIBLE
                drugCard.addDrug.userInputText.text = null
                drugCard.addDrug.userInputText.error = null
                DrugRecord.drug = null
            }

            noteCard.addNote.cancel.setOnClickListener {
                noteCard.addNote.layout.visibility = View.GONE
                noteCard.addNoteButton.visibility = View.VISIBLE
                noteCard.addNote.userInputText.text = null
                noteCard.addNote.userInputText.error = null
                DrugRecord.note = null
            }


            timeCard.cardTitle.text = "服用時間"
            timeCard.startTimeTag.text = "時間"
        }

        initDateTimeText()
        setDateTimePicker()
    }

    private fun isRecordEmpty(): Boolean {
        return DrugRecord.drug.isNullOrBlank() || DrugRecord.startTime.isTimeRecordEmpty() || DrugRecord.endTime.isTimeRecordEmpty()
    }

    private fun postRecordApi(): Thread {
        return Thread {
            if(!isRecordEmpty()){
                try {
                    val url = URL(getString(R.string.post_drug_record_url, getString(R.string.server_url)))
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

                    Log.e("API Connection", "Connection success")
                } catch (e: Exception) {
                    Log.e("API Connection", "Service not found")
                }
            } else {
                activity?.runOnUiThread {
                    binding.apply {
                        drugCard.addDrug.layout.visibility = View.VISIBLE
                        drugCard.addDrugButton.visibility = View.GONE
                        drugCard.addDrug.userInputText.error = getString(R.string.input_null)
                    }
                }
            }
        }
    }

    private fun saveRecord() {
        binding.apply {
            if (validateInputText(drugCard.addDrug.userInputText)) {
                DrugRecord.drug = drugCard.addDrug.userInputText.text.toString()
            }

            if (validateInputText(noteCard.addNote.userInputText)) {
                DrugRecord.note = noteCard.addNote.userInputText.text.toString()
            }
        }
    }

    private fun recordToJson(): ByteArray {
        val caseNumber = preferences.getString("caseNumber", "")
        var recordString = "{"
        recordString += "\"CaseNumber\": \"$caseNumber\", "
        recordString += "\"DrugItem\": \"${DrugRecord.drug}\","
        recordString += "\"MedicationTime\": \"" + getString(R.string.date_time_format, DrugRecord.startTime.YEAR, DrugRecord.startTime.MONTH+1, DrugRecord.startTime.DAY, DrugRecord.startTime.HOUR, DrugRecord.startTime.MIN, DrugRecord.startTime.SEC) + "\", "
        recordString += "\"DrugNote\": \"${DrugRecord.note}\""
        recordString += "}"

        return recordString.encodeToByteArray()
    }

    private fun postUpdateUi(line: String) {
        activity?.runOnUiThread {
            binding.apply {
                if(line == "\"1\"") {
                    Toast.makeText(context, R.string.drug_record_added_successfully, Toast.LENGTH_SHORT).show()
                    setRecord()
                    findNavController().navigate(R.id.action_drugFragment_to_mainFragment)
                }else {
                    setRecord()
                    Toast.makeText(context, R.string.drug_record_added_failed, Toast.LENGTH_SHORT).show()
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
                setDatePicker(timeCard.startDate, DrugRecord.startTime, 0).show()
            }

            timeCard.endDate.setOnClickListener {
                setDatePicker(timeCard.startDate, DrugRecord.endTime, 0).show()
            }

            timeCard.startTime.setOnClickListener {
                setTimePicker(timeCard.startTime, DrugRecord.startTime, 0).show()
            }

            timeCard.endTime.setOnClickListener {
                setTimePicker(timeCard.startTime, DrugRecord.endTime, 0).show()
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
        val timePicker = TimePickerDialog(requireContext(), TimePickerDialog.THEME_HOLO_LIGHT, { _, hour, min ->
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

        DrugRecord.startTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], calendar[Calendar.SECOND])
        DrugRecord.endTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]+1, calendar[Calendar.SECOND])
    }
}