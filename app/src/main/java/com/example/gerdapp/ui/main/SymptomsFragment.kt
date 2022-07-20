package com.example.gerdapp.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentSymptomsBinding
import java.text.SimpleDateFormat
import java.util.*

class SymptomsFragment: Fragment() {
    private var _binding: FragmentSymptomsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSymptomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val calendar = Calendar.getInstance()
            val current = calendar.time // TODO: Check if the time match the device time zone

            val formatDate = SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
            val currentDate = formatDate.format(current)
            symptomsTextStartDate.text = currentDate.toString()
//            symptomsTextEndDate.text = currentDate.toString()

            val formatTime = SimpleDateFormat(getString(R.string.simple_time_format), Locale.getDefault())
            val currentTime = formatTime.format(current)
            symptomsTextStartTime.text = currentTime.toString()
//            symptomsTextEndTime.text = currentTime.toString()

            symptomsTextStartDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = getString(R.string.date_format, year, month+1, day)
                        symptomsTextStartDate.text = format
                    }
                }, year, month, day).show()
            }

//            symptomsTextEndDate.setOnClickListener {
//                val calendar = Calendar.getInstance()
//                val day = calendar[Calendar.DAY_OF_MONTH]
//                val month = calendar[Calendar.MONTH]
//                val year = calendar[Calendar.YEAR]
//
//                DatePickerDialog(requireContext(), { _, year, month, day ->
//                    run {
//                        val format = getString(R.string.date_format, year, month+1, day)
//                        symptomsTextEndDate.text = format
//                    }
//                }, year, month, day).show()
//            }

            symptomsTextStartTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        symptomsTextStartTime.text = format
                    }
                }, hour, min, true).show()
            }

//            symptomsTextEndTime.setOnClickListener {
//                val calendar = Calendar.getInstance()
//                val hour = calendar[Calendar.HOUR_OF_DAY]
//                val min = calendar[Calendar.MINUTE]
//
//                TimePickerDialog(requireContext(), { _, hour, min ->
//                    run {
//                        val format = getString(R.string.time_format, hour, min)
//                        symptomsTextEndTime.text = format
//                    }
//                }, hour, min, true).show()
//            }

            symptomsButtonCancel.setOnClickListener {
                findNavController().navigate(R.id.action_symptomsFragment_to_mainFragment)
            }

            symptomsButtonDone.setOnClickListener {
                findNavController().navigate(R.id.action_symptomsFragment_to_mainFragment)
            }
        }

    }
}