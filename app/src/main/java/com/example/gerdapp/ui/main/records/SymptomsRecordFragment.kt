package com.example.gerdapp.ui.main.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentSymptomsRecordBinding
import com.example.gerdapp.viewmodel.RecordViewModel
import com.example.gerdapp.viewmodel.RecordViewModelFactory
import com.example.gerdapp.viewmodel.SleepViewModel
import com.example.gerdapp.viewmodel.SleepViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class SymptomsRecordFragment: Fragment() {
    private var _binding: FragmentSymptomsRecordBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE

    object SymptomsScore {
        var coughScore: Int = 0
        var heartBurnScore: Int = 0
        var acidRefluxScore: Int = 0
        var chestPainScore: Int = 0
        var sourMouthScore: Int = 0
        var hoarsenessScore: Int = 0
        var appetiteLossScore: Int = 0
        var stomachGasScore: Int = 0
    }

    private val viewModel: RecordViewModel by activityViewModels {
        RecordViewModelFactory(
            (activity?.application as BasicApplication).recordDatabase.recordDao()
        )
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.timeCard.startDate.text.toString()+" "+binding.timeCard.startTime.text.toString()
        )
    }

    private fun addNewItem(){
        if(isEntryValid()) {
            viewModel.addSymptomRecord(
                binding.timeCard.startDate.text.toString()+" "+binding.timeCard.startTime.text.toString(),
                SymptomsScore.coughScore, SymptomsScore.heartBurnScore, SymptomsScore.acidRefluxScore, SymptomsScore.chestPainScore,
                SymptomsScore.sourMouthScore, SymptomsScore.hoarsenessScore, SymptomsScore.appetiteLossScore, SymptomsScore.stomachGasScore
            )
            Toast.makeText(context, "sleep record added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "invalid input", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setBottomNavigationVisibility() {
        var mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
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
            completeButton.setOnClickListener {
                addNewItem()
                findNavController().navigate(R.id.action_symptomsFragment_to_mainFragment)
            }
        }

        dateTimePicker()
        setSymptomsCard()
    }

    fun dateTimePicker() {
        binding.apply {
            val calendar = Calendar.getInstance()
            val current = calendar.time // TODO: Check if the time match the device time zone

            val formatDate = SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
            val currentDate = formatDate.format(current)
            timeCard.startDate.text = currentDate.toString()
            timeCard.endDate.text = currentDate.toString()

            val formatTime = SimpleDateFormat(getString(R.string.simple_time_format), Locale.getDefault())
            val currentTime = formatTime.format(current)
            timeCard.startTime.text = currentTime.toString()
            timeCard.endTime.text = currentTime.toString()

            timeCard.startDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = getString(R.string.date_format, year, month+1, day)
                        timeCard.startDate.text = format
                    }
                }, year, month, day).show()
            }

            timeCard.endDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = getString(R.string.date_format, year, month+1, day)
                        timeCard.endDate.text = format
                    }
                }, year, month, day).show()
            }

            timeCard.startTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        timeCard.startTime.text = format
                    }
                }, hour, min, true).show()
            }

            timeCard.endTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        timeCard.endTime.text = format
                    }
                }, hour, min, true).show()
            }

        }
    }

    fun setSymptomsCard() {
        binding.apply {
            symptomsCard.symptomsCough.setOnClickListener {
                if(SymptomsScore.coughScore==0){
                    it.setBackgroundColor(Color.RED)
                    SymptomsScore.coughScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.coughScore = 0
                }
            }
            symptomsCard.symptomsHeartBurn.setOnClickListener{
                if(SymptomsScore.heartBurnScore==0){
                    it.setBackgroundColor(Color.RED)
                    SymptomsScore.heartBurnScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.heartBurnScore = 0
                }
            }
            symptomsCard.symptomsAcidReflux.setOnClickListener {
                if(SymptomsScore.acidRefluxScore==0){
                    it.setBackgroundColor(Color.RED)
                    SymptomsScore.acidRefluxScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.acidRefluxScore = 0
                }
            }
            symptomsCard.symptomsChestPain.setOnClickListener{
                if(SymptomsScore.chestPainScore==0){
                    it.setBackgroundColor(Color.RED)
                    SymptomsScore.chestPainScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.chestPainScore = 0
                }
            }
            symptomsCard.symptomsSourMouth.setOnClickListener {
                if(SymptomsScore.sourMouthScore==0){
                    it.setBackgroundColor(Color.RED)
                    SymptomsScore.sourMouthScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.sourMouthScore = 0
                }
            }
            symptomsCard.symptomsHoarseness.setOnClickListener{
                if(SymptomsScore.hoarsenessScore==0){
                    it.setBackgroundColor(Color.RED)
                    SymptomsScore.hoarsenessScore = 1
                } else {
                it.setBackgroundColor(Color.TRANSPARENT)
                SymptomsScore.hoarsenessScore = 0
            }
            }
            symptomsCard.symptomsAppetiteLoss.setOnClickListener {
                if(SymptomsScore.appetiteLossScore==0){
                    it.setBackgroundColor(Color.RED)
                    SymptomsScore.appetiteLossScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.appetiteLossScore = 0
                }
            }
            symptomsCard.symptomsStomachGas.setOnClickListener{
                if(SymptomsScore.stomachGasScore==0){
                    it.setBackgroundColor(Color.RED)
                    SymptomsScore.stomachGasScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.stomachGasScore = 0
                }
            }
        }
    }

}