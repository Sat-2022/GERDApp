package com.example.gerdapp.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.R
import com.example.gerdapp.data.Others
import com.example.gerdapp.databinding.FragmentSleepBinding
import com.example.gerdapp.viewmodel.SleepViewModel
import com.example.gerdapp.viewmodel.SleepViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class SleepFragment: Fragment() {
    private var _binding: FragmentSleepBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SleepViewModel by activityViewModels {
        SleepViewModelFactory(
            (activity?.application as BasicApplication).sleepDatabase.sleepDao()
        )
    }

    lateinit var others: Others

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.sleepTextStartDate.text.toString()+" "+binding.sleepTextStartTime.text.toString(),
            binding.sleepTextEndDate.text.toString()+" "+binding.sleepTextEndTime.text.toString()
        )
    }

    private fun addNewItem(){
        if(isEntryValid()) {
            viewModel.addSleepRecord(
                binding.sleepTextStartDate.text.toString()+" "+binding.sleepTextStartTime.text.toString(),
                binding.sleepTextEndDate.text.toString()+" "+binding.sleepTextEndTime.text.toString()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSleepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val calendar = Calendar.getInstance()
            val current = calendar.time // TODO: Check if the time match the device time zone

            val formatDate = SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
            val currentDate = formatDate.format(current)
            sleepTextStartDate.text = currentDate.toString()
            sleepTextEndDate.text = currentDate.toString()

            val formatTime = SimpleDateFormat(getString(R.string.simple_time_format), Locale.getDefault())
            val currentTime = formatTime.format(current)
            sleepTextEndTime.text = currentTime.toString()
            sleepTextStartTime.text = currentTime.toString()

            sleepTextStartDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = "${getString(R.string.date_format, year, month, day)}"
                        sleepTextStartDate.text = format
                    }
                }, year, month, day).show()
            }

            sleepTextEndDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = "${getString(R.string.date_format, year, month, day)}"
                        sleepTextEndDate.text = format
                    }
                }, year, month, day).show()
            }

            sleepTextStartTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = "${getString(R.string.time_format, hour, min)}"
                        sleepTextStartTime.text = format
                    }
                }, hour, min, true).show()
            }

            sleepTextEndTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = "${getString(R.string.time_format, hour, min)}"
                        sleepTextEndTime.text = format
                    }
                }, hour, min, true).show()
            }

            sleepButtonCancel.setOnClickListener {
                addNewItem()
                findNavController().navigate(R.id.action_sleepFragment_to_mainFragment)
            }

            sleepButtonDone.setOnClickListener {
                findNavController().navigate(R.id.action_sleepFragment_to_mainFragment)
            }
        }

    }
}