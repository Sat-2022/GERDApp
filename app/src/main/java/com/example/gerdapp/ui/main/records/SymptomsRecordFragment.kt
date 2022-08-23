package com.example.gerdapp.ui.main.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
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
import com.example.gerdapp.databinding.FragmentSymptomsRecordBinding
import com.example.gerdapp.ui.Time
import com.example.gerdapp.ui.Time.date
import com.example.gerdapp.ui.Time.hour
import com.example.gerdapp.ui.Time.min
import com.example.gerdapp.ui.Time.month
import com.example.gerdapp.ui.Time.sec
import com.example.gerdapp.ui.Time.year
import com.example.gerdapp.ui.initTime
import com.example.gerdapp.ui.resetTime
import com.example.gerdapp.viewmodel.RecordViewModel
import com.example.gerdapp.viewmodel.RecordViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class SymptomsRecordFragment: Fragment() {
    private var _binding: FragmentSymptomsRecordBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE

    private object SymptomsRecord {
        var coughScore: Int = 0
        var heartBurnScore: Int = 0
        var acidRefluxScore: Int = 0
        var chestPainScore: Int = 0
        var sourMouthScore: Int = 0
        var hoarsenessScore: Int = 0
        var appetiteLossScore: Int = 0
        var stomachGasScore: Int = 0
        var othersSymptoms: String? = null
        var note: String? = null
    }

    private val viewModel: RecordViewModel by activityViewModels {
        RecordViewModelFactory(
            (activity?.application as BasicApplication).recordDatabase.recordDao()
        )
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.timeCard.startDate.text.toString()+" "+binding.timeCard.startTime.text.toString()
        ) && !isRecordEmpty()
    }

    private fun isRecordEmpty(): Boolean {
        return SymptomsRecord.coughScore==0 && SymptomsRecord.heartBurnScore==0 && SymptomsRecord.acidRefluxScore==0 && SymptomsRecord.chestPainScore==0
                && SymptomsRecord.sourMouthScore==0 && SymptomsRecord.hoarsenessScore==0 && SymptomsRecord.appetiteLossScore==0 && SymptomsRecord.stomachGasScore==0
                && SymptomsRecord.othersSymptoms.isNullOrBlank()
    }

    private fun addNewItem() = if(isEntryValid()) {
        SymptomsRecord.othersSymptoms = binding.symptomsCard.addOtherSymptoms.userInputText.text.toString()
        viewModel.addSymptomRecord(
            binding.timeCard.startDate.text.toString()+" "+binding.timeCard.startTime.text.toString(),
            SymptomsRecord.coughScore, SymptomsRecord.heartBurnScore, SymptomsRecord.acidRefluxScore, SymptomsRecord.chestPainScore,
            SymptomsRecord.sourMouthScore, SymptomsRecord.hoarsenessScore, SymptomsRecord.appetiteLossScore, SymptomsRecord.stomachGasScore,
            SymptomsRecord.othersSymptoms
        )
        Toast.makeText(context, R.string.symptoms_added_successfully, Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, R.string.symptoms_added_failed, Toast.LENGTH_SHORT).show()
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
        _binding = FragmentSymptomsRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun validateInputText(textView: TextView): Boolean {
        if(textView.text.length > 20) {
            textView.error = "超過字數限制"
            return false
        }
        return true
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
                addNewItem()
                resetTime()
                findNavController().navigate(R.id.action_symptomsFragment_to_mainFragment)
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
                }, year, month, date).show()
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
                }, year, month, date).show()
            }

            timeCard.startTime.setOnClickListener {
                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        timeCard.startTime.text = format
                        Time.hour = hour
                        Time.min = min
                        sec = 0
                    }
                }, hour, min, true).show()
            }

            timeCard.endTime.setOnClickListener {
                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        timeCard.endTime.text = format
                        Time.hour = hour
                        Time.min = min
                        sec = 0
                    }
                }, hour, min, true).show()
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

    private fun setSymptomsCard() {
        binding.apply {
            symptomsCard.symptomsButtons.symptomsCough.setOnClickListener {
                if(SymptomsRecord.coughScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.coughScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.coughScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsHeartBurn.setOnClickListener{
                if(SymptomsRecord.heartBurnScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.heartBurnScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.heartBurnScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsAcidReflux.setOnClickListener {
                if(SymptomsRecord.acidRefluxScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.acidRefluxScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.acidRefluxScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsChestPain.setOnClickListener{
                if(SymptomsRecord.chestPainScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.chestPainScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.chestPainScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsSourMouth.setOnClickListener {
                if(SymptomsRecord.sourMouthScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.sourMouthScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.sourMouthScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsHoarseness.setOnClickListener{
                if(SymptomsRecord.hoarsenessScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.hoarsenessScore = 1
                } else {
                it.setBackgroundColor(Color.TRANSPARENT)
                SymptomsRecord.hoarsenessScore = 0
            }
            }
            symptomsCard.symptomsButtons.symptomsAppetiteLoss.setOnClickListener {
                if(SymptomsRecord.appetiteLossScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.appetiteLossScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.appetiteLossScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsStomachGas.setOnClickListener{
                if(SymptomsRecord.stomachGasScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsRecord.stomachGasScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsRecord.stomachGasScore = 0
                }
            }
        }
    }
}