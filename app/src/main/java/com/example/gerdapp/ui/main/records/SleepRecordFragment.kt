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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentSleepRecordBinding
import com.example.gerdapp.ui.Time
import com.example.gerdapp.ui.TimeRecord
import com.example.gerdapp.ui.initTime
import com.example.gerdapp.ui.resetTime
import com.example.gerdapp.viewmodel.SleepViewModel
import com.example.gerdapp.viewmodel.SleepViewModelFactory
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class SleepRecordFragment: Fragment() {
    private var _binding: FragmentSleepRecordBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE

    private val viewModel: SleepViewModel by activityViewModels {
        SleepViewModelFactory(
            (activity?.application as BasicApplication).sleepDatabase.sleepDao()
        )
    }

    private object SleepRecord {
        var note: String? = null
        var startTime: TimeRecord = TimeRecord()
        var endTime: TimeRecord = TimeRecord()
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.timeCard.startDate.text.toString()+" "+binding.timeCard.startTime.text.toString(),
            binding.timeCard.endDate.text.toString()+" "+binding.timeCard.endTime.text.toString()
        )
    }

    private fun addNewItem(){
        if(isEntryValid()) {
            viewModel.addSleepRecord(
                binding.timeCard.startDate.text.toString()+" "+binding.timeCard.startTime.text.toString(),
                binding.timeCard.endDate.text.toString()+" "+binding.timeCard.endTime.text.toString()
            )
            Toast.makeText(context, R.string.sleep_record_added_successfully, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, R.string.symptoms_added_failed, Toast.LENGTH_SHORT).show()
        }
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
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
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
                // addNewItem()
                if(isEntryValid()){
                    postRecordApi().start()
                    resetTime()
                    findNavController().navigate(R.id.action_sleepFragment_to_mainFragment)
                }
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

    private fun postRecordApi(): Thread {
        return Thread {
            try {
                val url = URL(getString(R.string.post_sleep_record_url, getString(R.string.server_url)))
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true
                connection.doInput = true

                val outputSystem = connection.outputStream
                val outputStream = DataOutputStream(outputSystem)

                val data: ByteArray = ("{\n" +
                        "    \"CaseNumber\": \"T010\",\n" +
                        "     \"StartDate\": \"2022-08-24T12:43\",\n" +
                        "     \"EndDate\": \"2022-08-24T12:44\",\n" +
                        "     \"SleepNote\": \"備註\"\n" +
                        "}\n").encodeToByteArray()

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
            }catch (e: FileNotFoundException) {
                Log.e("API Connection", "Service not found at ${e.message}")
                Log.e("API Connection", e.toString())
            }
        }
    }

    private fun recordToJson(): String {
        var jsonString = ""



        return jsonString
    }

    private fun postUpdateUi(line: String) {
        activity?.runOnUiThread {
            binding.apply {
                if(line == "1") {
                    Toast.makeText(context, R.string.sleep_record_added_successfully, Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(context, R.string.symptoms_added_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputText(textView: TextView): Boolean {
        if(textView.text.length > 20) {
            textView.error = "超過字數限制"
            return false
        }
        return true
    }

    private fun setDateTimePicker() {
        binding.apply {
            timeCard.startDate.setOnClickListener {
                DatePickerDialog(requireContext(), { _, year, month, date ->
                    run {
                        val format = getString(R.string.date_format, year, month+1, date)
                        timeCard.startDate.text = format
                        Time.year = year
                        Time.month = month
                        Time.date = date
                    }
                }, Time.year, Time.month, Time.date).show()
            }

            timeCard.endDate.setOnClickListener {
                DatePickerDialog(requireContext(), { _, year, month, date ->
                    run {
                        val format = getString(R.string.date_format, year, month+1, date)
                        timeCard.endDate.text = format
                        Time.year = year
                        Time.month = month
                        Time.date = date
                    }
                }, Time.year, Time.month, Time.date).show()
            }

            timeCard.startTime.setOnClickListener {
                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        timeCard.startTime.text = format
                        Time.hour = hour
                        Time.min = min
                        Time.sec = 0
                    }
                }, Time.hour, Time.min, true).show()
            }

            timeCard.endTime.setOnClickListener {
                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        timeCard.endTime.text = format
                        Time.hour = hour
                        Time.min = min
                        Time.sec = 0
                    }
                }, Time.hour, Time.min, true).show()
            }
        }
    }

    private fun initDateTimeText() {
        val calendar = Calendar.getInstance()
        val current = calendar.time // TODO: Check if the time match the device time zone

        val formatDate = SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
        val currentDate = formatDate.format(current)
        binding.apply {
            timeCard.startDate.text = currentDate.toString()
            timeCard.endDate.text = currentDate.toString()
        }

        val formatTime = SimpleDateFormat(getString(R.string.simple_time_format), Locale.getDefault())
        val currentTime = formatTime.format(current)
        binding.apply {
            timeCard.startTime.text = currentTime.toString()
            timeCard.endTime.text = currentTime.toString()
        }

        initTime(calendar)
    }
}