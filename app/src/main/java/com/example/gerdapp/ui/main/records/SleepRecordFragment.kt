package com.example.gerdapp.ui.main.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentSleepRecordBinding
import com.example.gerdapp.data.model.TimeRecord
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class SleepRecordFragment: Fragment() {
    private var _binding: FragmentSleepRecordBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE

    private object SleepRecord {
        var note: String? = null
        var startTime: TimeRecord = TimeRecord()
        var endTime: TimeRecord = TimeRecord()
    }

    private fun setRecord() {
        SleepRecord.note = null
        SleepRecord.startTime = TimeRecord()
        SleepRecord.endTime = TimeRecord()
    }

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
        mainActivity.setActionBarExpanded(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setBottomNavigationVisibility()
//        setRecord()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
//        setRecord()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSleepRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            noteCard.addNote.userInputText.hint = getString(R.string.add_note)

            noteCard.addNote.userInputText.setOnEditorActionListener { textView, actionId, keyEvent ->
                return@setOnEditorActionListener when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if(validateInputText(textView)) {
                            SleepRecord.note = textView.text.toString()
                        }
                        false
                    }
                    else -> false
                }
            }

            completeButton.setOnClickListener {
                postRecordApi().start()
            }

            noteCard.addNoteButton.setOnClickListener {
                noteCard.addNote.layout.visibility = View.VISIBLE
                noteCard.addNoteButton.visibility = View.GONE
            }

            noteCard.addNote.cancel.setOnClickListener {
                noteCard.addNote.layout.visibility = View.GONE
                noteCard.addNoteButton.visibility = View.VISIBLE
                noteCard.addNote.userInputText.text = null
                noteCard.addNote.userInputText.error = null
                SleepRecord.note = null
            }
        }

        initDateTimeText()
        setDateTimePicker()
    }

    private fun isRecordEmpty(): Boolean {
        return SleepRecord.startTime.isTimeRecordEmpty() || SleepRecord.endTime.isTimeRecordEmpty()
    }

    private fun postRecordApi(): Thread {
        return Thread {
            if(!isRecordEmpty()){
                try {
                    val url = URL(
                        getString(
                            R.string.post_sleep_record_url,
                            getString(R.string.server_url)
                        )
                    )
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
            }
        }
    }

    private fun recordToJson(): ByteArray {
        var recordString = "{"
        recordString += "\"CaseNumber\": \"T010\", "
        recordString += "\"StartDate\": \"" + getString(R.string.date_time_format, SleepRecord.startTime.YEAR, SleepRecord.startTime.MONTH+1, SleepRecord.startTime.DAY, SleepRecord.startTime.HOUR, SleepRecord.startTime.MIN, SleepRecord.startTime.SEC) + "\", "
        recordString += "\"EndDate\": \"" + getString(R.string.date_time_format, SleepRecord.endTime.YEAR, SleepRecord.endTime.MONTH+1, SleepRecord.endTime.DAY, SleepRecord.endTime.HOUR, SleepRecord.endTime.MIN, SleepRecord.endTime.SEC) + "\", "
        recordString += "\"SleepNote\": \"${SleepRecord.note}\""
        recordString += "}"

        return recordString.encodeToByteArray()
    }

    private fun postUpdateUi(line: String) {
        activity?.runOnUiThread {
            binding.apply {
                if(line == "\"1\"") {
                    setRecord()
                    Toast.makeText(context, R.string.symptoms_added_successfully, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_sleepFragment_to_mainFragment)
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
                setDatePicker(timeCard.startDate, SleepRecord.startTime, 0).show()
            }

            timeCard.endDate.setOnClickListener {
                setDatePicker(timeCard.endDate, SleepRecord.endTime).show()
            }

            timeCard.startTime.setOnClickListener {
                setTimePicker(timeCard.startTime, SleepRecord.startTime).show()
            }

            timeCard.endTime.setOnClickListener {
                setTimePicker(timeCard.endTime, SleepRecord.endTime).show()
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

        SleepRecord.startTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], calendar[Calendar.SECOND])
        SleepRecord.endTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]+1, calendar[Calendar.SECOND])
    }
}