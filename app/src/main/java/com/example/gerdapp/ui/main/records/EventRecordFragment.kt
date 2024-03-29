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
import com.example.gerdapp.databinding.FragmentEventRecordBinding
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**********************************************
 * Fragments for user to add event record
 **********************************************/
class EventRecordFragment: Fragment() {
    private var _binding: FragmentEventRecordBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE
    private var actionbarTitleEnable = false

    private lateinit var preferences: SharedPreferences

    private var timeRecordValid = true

    private object EventRecord {
        var event: String? = null
        var note: String? = null
        var startTime: TimeRecord = TimeRecord()
        var endTime: TimeRecord = TimeRecord()
    }

    private fun setRecord() {
        EventRecord.event = null
        EventRecord.note = null
        EventRecord.startTime = TimeRecord()
        EventRecord.endTime = TimeRecord()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.menu_settings).isVisible = false
//        setRecord()
    }


    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentEventRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            noteCard.addNote.userInputText.hint = getString(R.string.add_note)
            othersCard.addOthers.userInputText.hint = getString(R.string.event_record_add_others)

            othersCard.addOthers.userInputText.setOnEditorActionListener { textView, actionId, keyEvent ->
                return@setOnEditorActionListener when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if(validateInputText(textView)) {
                            EventRecord.event = textView.text.toString()
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
                            EventRecord.note = textView.text.toString()
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

            othersCard.addOthersButton.setOnClickListener {
                othersCard.addOthers.layout.visibility = View.VISIBLE
                othersCard.addOthersButton.visibility = View.GONE
            }

            noteCard.addNoteButton.setOnClickListener {
                noteCard.addNote.layout.visibility = View.VISIBLE
                noteCard.addNoteButton.visibility = View.GONE
            }

            othersCard.addOthers.cancel.setOnClickListener {
                othersCard.addOthers.layout.visibility = View.GONE
                othersCard.addOthersButton.visibility = View.VISIBLE
                othersCard.addOthers.userInputText.text = null
                othersCard.addOthers.userInputText.error = null
                EventRecord.event = null
            }

            noteCard.addNote.cancel.setOnClickListener {
                noteCard.addNote.layout.visibility = View.GONE
                noteCard.addNoteButton.visibility = View.VISIBLE
                noteCard.addNote.userInputText.text = null
                noteCard.addNote.userInputText.error = null
                EventRecord.note = null
            }
        }

        initDateTimeText()
        setDateTimePicker()
    }

    private fun isRecordEmpty(): Boolean {
        return EventRecord.event.isNullOrBlank() || EventRecord.startTime.isTimeRecordEmpty() || EventRecord.endTime.isTimeRecordEmpty()
    }

    private fun postRecordApi(): Thread {
        return Thread {
            if(isRecordEmpty()){
                activity?.runOnUiThread {
                    binding.apply {
                        othersCard.addOthers.layout.visibility = View.VISIBLE
                        othersCard.addOthersButton.visibility = View.GONE
                        othersCard.addOthers.userInputText.error = getString(R.string.input_null)
                    }
                    Toast.makeText(context, R.string.event_record_added_failed, Toast.LENGTH_SHORT).show()
                }
            } else if(!timeRecordValid) {
                activity?.runOnUiThread {
                    Toast.makeText(context, R.string.error_end_time_is_not_before_start_time, Toast.LENGTH_SHORT).show()
                }
            } else {
                try {
                    val url = URL(getString(R.string.post_event_record_url, getString(R.string.server_url)))
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

//                    Log.e("API Connection", "Connection success")
                } catch (e: Exception) {
//                    Log.e("API Connection", "Service not found")
                }
            }
        }
    }

    private fun saveRecord() {
        binding.apply {
            if (validateInputText(othersCard.addOthers.userInputText)) {
                EventRecord.event = othersCard.addOthers.userInputText.text.toString()
            }

            if (validateInputText(noteCard.addNote.userInputText)) {
                EventRecord.note = noteCard.addNote.userInputText.text.toString()
            }

            validateTimeRecord(timeCard.startDate, timeCard.startTime, timeCard.endDate, timeCard.endTime)
        }
    }

    private fun validateTimeRecord(startDate: TextView, startTime: TextView, endDate: TextView, endTime: TextView) {
        val timeRecord = TimeRecord()
        val start = startDate.text.toString() + " " + startTime.text.toString() + ":00"
        val end = endDate.text.toString() + " " + endTime.text.toString() + ":00"

        if(!timeRecord.isAfter(start, end)) {
            endTime.error = getString(R.string.error_end_time_is_not_before_start_time)
            timeRecordValid = false
        } else {
            timeRecordValid = true
        }
    }

    private fun recordToJson(): ByteArray {
        val caseNumber = preferences.getString("caseNumber", "")
        var recordString = "{"
        recordString += "\"CaseNumber\": \"$caseNumber\", "
        recordString += "\"ActivityItem\": \"${EventRecord.event}\","
        recordString += "\"StartDate\": \"" + getString(R.string.date_time_format, EventRecord.startTime.YEAR, EventRecord.startTime.MONTH+1, EventRecord.startTime.DAY, EventRecord.startTime.HOUR, EventRecord.startTime.MIN, EventRecord.startTime.SEC) + "\", "
        recordString += "\"EndDate\": \"" + getString(R.string.date_time_format, EventRecord.endTime.YEAR, EventRecord.endTime.MONTH+1, EventRecord.endTime.DAY, EventRecord.endTime.HOUR, EventRecord.endTime.MIN, EventRecord.endTime.SEC) + "\", "
        recordString += "\"ActivityNote\": \"${EventRecord.note}\""
        recordString += "}"

        return recordString.encodeToByteArray()
    }

    private fun postUpdateUi(line: String) {
        activity?.runOnUiThread {
            binding.apply {
                if(line == "\"1\"") {
                    Toast.makeText(context, R.string.event_record_added_successfully, Toast.LENGTH_SHORT).show()
                    setRecord()
                    findNavController().navigate(R.id.action_eventFragment_to_mainFragment)
                }else {
                    setRecord()
                    Toast.makeText(context, R.string.event_record_added_failed, Toast.LENGTH_SHORT).show()
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
                setDatePicker(timeCard.startDate, EventRecord.startTime, EventRecord.endTime, 0).show()
            }

            timeCard.endDate.setOnClickListener {
                setDatePicker(timeCard.endDate, EventRecord.startTime, EventRecord.endTime, 1).show()
            }

            timeCard.startTime.setOnClickListener {
                setTimePicker(timeCard.startTime, EventRecord.startTime, 0).show()
            }

            timeCard.endTime.setOnClickListener {
                setTimePicker(timeCard.endTime, EventRecord.endTime, 0).show()
            }
        }
    }

    private fun setDatePicker(textView: TextView, startTimeRecord: TimeRecord, endTimeRecord: TimeRecord, tag: Int = 0): DatePickerDialog {
        if(tag == 0) {
            val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
                run {
                    textView.text = getString(R.string.date_format, year, month+1, day)
                    startTimeRecord.YEAR = year
                    startTimeRecord.MONTH = month
                    startTimeRecord.DAY = day
                }
            }, startTimeRecord.YEAR, startTimeRecord.MONTH, startTimeRecord.DAY)
            val calendar = Calendar.getInstance()
            calendar.set(endTimeRecord.YEAR, endTimeRecord.MONTH, endTimeRecord.DAY, 0, 0, 0)
            datePicker.datePicker.maxDate = calendar.timeInMillis
            return datePicker
        } else {
            val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
                run {
                    textView.text = getString(R.string.date_format, year, month+1, day)
                    endTimeRecord.YEAR = year
                    endTimeRecord.MONTH = month
                    endTimeRecord.DAY = day
                }
            }, endTimeRecord.YEAR, endTimeRecord.MONTH, endTimeRecord.DAY)
            val calendar = Calendar.getInstance()
            calendar.set(startTimeRecord.YEAR, startTimeRecord.MONTH, startTimeRecord.DAY, 0, 0, 0)
            datePicker.datePicker.minDate = calendar.timeInMillis
            return datePicker
        }
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

        EventRecord.startTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], calendar[Calendar.SECOND])
        EventRecord.endTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]+1, calendar[Calendar.SECOND])
    }
}