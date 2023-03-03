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
import com.example.gerdapp.*
import com.example.gerdapp.data.TimeRecord
import com.example.gerdapp.databinding.FragmentFoodRecordBinding
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FoodRecordFragment: Fragment() {
    private var _binding: FragmentFoodRecordBinding? = null
    private val binding get() = _binding!!
    
    private var bottomNavigationViewVisibility = View.GONE
    private var actionbarTitleEnable = false

    private lateinit var preferences: SharedPreferences

    private var timeRecordValid = true

    private object FoodRecord {
        var food: String? = null
        var note: String? = null
        var startTime: TimeRecord = TimeRecord()
        var endTime: TimeRecord = TimeRecord()
    }

    private fun setRecord() {
        FoodRecord.food = null
        FoodRecord.note = null
        FoodRecord.startTime = TimeRecord()
        FoodRecord.endTime = TimeRecord()
    }

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
        mainActivity.setActionBarExpanded(false)
        mainActivity.setActionBarTitleEnable(actionbarTitleEnable)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        setBottomNavigationVisibility()
        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        setRecord()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
//        setRecord()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.menu_settings).isVisible = false
//        menu.setGroupVisible(R.id.menu_group, false)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentFoodRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            noteCard.addNote.userInputText.hint = getString(R.string.add_note)
            foodCard.addFood.userInputText.hint = getString(R.string.food_record_add_food)

            foodCard.addFood.userInputText.setOnEditorActionListener { textView, actionId, keyEvent ->
                return@setOnEditorActionListener when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if(validateInputText(textView)) {
                            FoodRecord.food = textView.text.toString()
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
                            FoodRecord.note = textView.text.toString()
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

            foodCard.addFoodButton.setOnClickListener {
                foodCard.addFood.layout.visibility = View.VISIBLE
                foodCard.addFoodButton.visibility = View.GONE
            }

            noteCard.addNoteButton.setOnClickListener {
                noteCard.addNote.layout.visibility = View.VISIBLE
                noteCard.addNoteButton.visibility = View.GONE
            }

            foodCard.addFood.cancel.setOnClickListener {
                foodCard.addFood.layout.visibility = View.GONE
                foodCard.addFoodButton.visibility = View.VISIBLE
                foodCard.addFood.userInputText.text = null
                foodCard.addFood.userInputText.error = null
                FoodRecord.food = null
            }

            noteCard.addNote.cancel.setOnClickListener {
                noteCard.addNote.layout.visibility = View.GONE
                noteCard.addNoteButton.visibility = View.VISIBLE
                noteCard.addNote.userInputText.text = null
                noteCard.addNote.userInputText.error = null
                FoodRecord.note = null
            }
        }

        initDateTimeText()
        setDateTimePicker()
    }

    private fun isRecordEmpty(): Boolean {
        return FoodRecord.food.isNullOrBlank() || FoodRecord.startTime.isTimeRecordEmpty() || FoodRecord.endTime.isTimeRecordEmpty()
    }

    private fun postRecordApi(): Thread {
        return Thread {
            if(isRecordEmpty()){
                activity?.runOnUiThread {
                    binding.apply {
                        foodCard.addFood.layout.visibility = View.VISIBLE
                        foodCard.addFoodButton.visibility = View.GONE
                        foodCard.addFood.userInputText.error = getString(R.string.input_null)
                    }
                    Toast.makeText(context, R.string.food_record_added_failed, Toast.LENGTH_SHORT).show()
                }
            } else if(!timeRecordValid) {
                activity?.runOnUiThread {
                    Toast.makeText(context, R.string.error_end_time_is_not_before_start_time, Toast.LENGTH_SHORT).show()
                }
            } else {
                try {
                    val url = URL(getString(R.string.post_food_record_url, getString(R.string.server_url)))
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
            }
        }
    }

    private fun saveRecord() {
        binding.apply {
            if (validateInputText(foodCard.addFood.userInputText)) {
                FoodRecord.food = foodCard.addFood.userInputText.text.toString()
            }

            if (validateInputText(noteCard.addNote.userInputText)) {
                FoodRecord.note = noteCard.addNote.userInputText.text.toString()
            }

            validateTimeRecord(timeCard.startDate, timeCard.startTime, timeCard.endDate, timeCard.endTime)
        }
    }

    private fun validateTimeRecord(startDate: TextView, startTime: TextView, endDate: TextView, endTime: TextView) {
        val timeRecord = TimeRecord()
        val start = startDate.text.toString() + " " + startTime.text.toString() + ":00"
        val end = endDate.text.toString() + " " + endTime.text.toString() + ":00"

        Log.e("", start)
        Log.e("", end)

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
        recordString += "\"FoodItem\": \"${FoodRecord.food}\","
        recordString += "\"StartDate\": \"" + getString(R.string.date_time_format, FoodRecord.startTime.YEAR, FoodRecord.startTime.MONTH+1, FoodRecord.startTime.DAY, FoodRecord.startTime.HOUR, FoodRecord.startTime.MIN, FoodRecord.startTime.SEC) + "\", "
        recordString += "\"EndDate\": \"" + getString(R.string.date_time_format, FoodRecord.endTime.YEAR, FoodRecord.endTime.MONTH+1, FoodRecord.endTime.DAY, FoodRecord.endTime.HOUR, FoodRecord.endTime.MIN, FoodRecord.endTime.SEC) + "\", "
        recordString += "\"FoodNote\": \"${FoodRecord.note}\""
        recordString += "}"

        return recordString.encodeToByteArray()
    }

    private fun postUpdateUi(line: String) {
        activity?.runOnUiThread {
            binding.apply {
                if(line == "\"1\"") {
                    Toast.makeText(context, R.string.food_record_added_successfully, Toast.LENGTH_SHORT).show()
                    setRecord()
                    findNavController().navigate(R.id.action_foodFragment_to_mainFragment)
                }else {
                    setRecord()
                    Toast.makeText(context, R.string.food_record_added_failed, Toast.LENGTH_SHORT).show()
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
                setDatePicker(timeCard.startDate, FoodRecord.startTime, FoodRecord.endTime, 0).show()
            }

            timeCard.endDate.setOnClickListener {
                setDatePicker(timeCard.endDate, FoodRecord.startTime, FoodRecord.endTime, 1).show()
            }

            timeCard.startTime.setOnClickListener {
                setTimePicker(timeCard.startTime, FoodRecord.startTime).show()
            }

            timeCard.endTime.setOnClickListener {
                setTimePicker(timeCard.endTime, FoodRecord.endTime).show()
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

        FoodRecord.startTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], calendar[Calendar.SECOND])
        FoodRecord.endTime.setTimeRecord(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]+1, calendar[Calendar.SECOND])
    }
}