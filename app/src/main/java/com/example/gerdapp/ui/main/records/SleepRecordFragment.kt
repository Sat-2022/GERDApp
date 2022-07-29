package com.example.gerdapp.ui.main.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.gerdapp.data.Sleep
import com.example.gerdapp.databinding.FragmentSleepRecordBinding
import com.example.gerdapp.viewmodel.SleepViewModel
import com.example.gerdapp.viewmodel.SleepViewModelFactory
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

    lateinit var others: Sleep

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSleepRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateTimePicker()

        binding.apply {
            completeButton.setOnClickListener {
                addNewItem()
                findNavController().navigate(R.id.action_sleepFragment_to_mainFragment)
            }
        }
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
}