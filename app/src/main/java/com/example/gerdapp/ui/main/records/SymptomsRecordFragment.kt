package com.example.gerdapp.ui.main.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
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

    private object SymptomsScore {
        var coughScore: Int = 0
        var heartBurnScore: Int = 0
        var acidRefluxScore: Int = 0
        var chestPainScore: Int = 0
        var sourMouthScore: Int = 0
        var hoarsenessScore: Int = 0
        var appetiteLossScore: Int = 0
        var stomachGasScore: Int = 0
        var othersSymptoms: String? = null
    }

    private val viewModel: RecordViewModel by activityViewModels {
        RecordViewModelFactory(
            (activity?.application as BasicApplication).recordDatabase.recordDao()
        )
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.timeCard.startDate.text.toString()+" "+binding.timeCard.startTime.text.toString()
        ) && !symptomsScoreIsEmpty()
    }

    private fun symptomsScoreIsEmpty(): Boolean {
        return SymptomsScore.coughScore==0 && SymptomsScore.heartBurnScore==0 && SymptomsScore.acidRefluxScore==0 && SymptomsScore.chestPainScore==0
                && SymptomsScore.sourMouthScore==0 && SymptomsScore.hoarsenessScore==0 && SymptomsScore.appetiteLossScore==0 && SymptomsScore.stomachGasScore==0
                && SymptomsScore.othersSymptoms.isNullOrBlank()
    }

    private fun addNewItem() = if(isEntryValid()) {
        SymptomsScore.othersSymptoms = binding.symptomsCard.addOtherSymptoms.userInputText.text.toString()
        viewModel.addSymptomRecord(
            binding.timeCard.startDate.text.toString()+" "+binding.timeCard.startTime.text.toString(),
            SymptomsScore.coughScore, SymptomsScore.heartBurnScore, SymptomsScore.acidRefluxScore, SymptomsScore.chestPainScore,
            SymptomsScore.sourMouthScore, SymptomsScore.hoarsenessScore, SymptomsScore.appetiteLossScore, SymptomsScore.stomachGasScore,
            SymptomsScore.othersSymptoms
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            noteCard.addNote.userInputText.hint = getString(R.string.add_note)
            symptomsCard.addOtherSymptoms.userInputText.hint = getString(R.string.symptoms_record_add_other_symptom)

            symptomsCard.addOtherSymptoms.userInputText.setOnEditorActionListener { textView, actionId, keyEvent ->
                return@setOnEditorActionListener when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        SymptomsScore.othersSymptoms = symptomsCard.addOtherSymptoms.userInputText.text.toString()
                        false
                    }
                    else -> false
                }
            }

            noteCard.addNote.userInputText.setOnEditorActionListener { textView, actionId, keyEvent ->
                return@setOnEditorActionListener when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        // SymptomsScore.othersSymptoms = symptomsCard.addOtherSymptoms.userInputText.text.toString()
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
            }

            noteCard.addNote.cancel.setOnClickListener {
                noteCard.addNote.layout.visibility = View.GONE
                noteCard.addNoteButton.visibility = View.VISIBLE
                noteCard.addNote.userInputText.text = null
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
                if(SymptomsScore.coughScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.coughScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.coughScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsHeartBurn.setOnClickListener{
                if(SymptomsScore.heartBurnScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.heartBurnScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.heartBurnScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsAcidReflux.setOnClickListener {
                if(SymptomsScore.acidRefluxScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.acidRefluxScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.acidRefluxScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsChestPain.setOnClickListener{
                if(SymptomsScore.chestPainScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.chestPainScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.chestPainScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsSourMouth.setOnClickListener {
                if(SymptomsScore.sourMouthScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.sourMouthScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.sourMouthScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsHoarseness.setOnClickListener{
                if(SymptomsScore.hoarsenessScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.hoarsenessScore = 1
                } else {
                it.setBackgroundColor(Color.TRANSPARENT)
                SymptomsScore.hoarsenessScore = 0
            }
            }
            symptomsCard.symptomsButtons.symptomsAppetiteLoss.setOnClickListener {
                if(SymptomsScore.appetiteLossScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.appetiteLossScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.appetiteLossScore = 0
                }
            }
            symptomsCard.symptomsButtons.symptomsStomachGas.setOnClickListener{
                if(SymptomsScore.stomachGasScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.stomachGasScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.stomachGasScore = 0
                }
            }
        }
    }
}