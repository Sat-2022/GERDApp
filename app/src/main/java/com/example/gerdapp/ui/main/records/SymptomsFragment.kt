package com.example.gerdapp.ui.main.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentSymptomsBinding
import java.text.SimpleDateFormat
import java.util.*

class SymptomsFragment: Fragment() {
    private var _binding: FragmentSymptomsBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE

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
            startDate.text = currentDate.toString()
            endDate.text = currentDate.toString()

            val formatTime = SimpleDateFormat(getString(R.string.simple_time_format), Locale.getDefault())
            val currentTime = formatTime.format(current)
            startTime.text = currentTime.toString()
            endTime.text = currentTime.toString()

            startDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = getString(R.string.date_format, year, month+1, day)
                        startDate.text = format
                    }
                }, year, month, day).show()
            }

            endDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = getString(R.string.date_format, year, month+1, day)
                        endDate.text = format
                    }
                }, year, month, day).show()
            }

            startTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        startTime.text = format
                    }
                }, hour, min, true).show()
            }

            endTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = getString(R.string.time_format, hour, min)
                        endTime.text = format
                    }
                }, hour, min, true).show()
            }

            completeButton.setOnClickListener {
                findNavController().navigate(R.id.action_symptomsFragment_to_mainFragment)
            }
        }

    }
}