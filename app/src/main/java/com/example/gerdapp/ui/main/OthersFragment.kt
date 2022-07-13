package com.example.gerdapp.ui.main

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gerdapp.databinding.FragmentOthersBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class OthersFragment: Fragment() {
    private var _binding: FragmentOthersBinding? = null
    private val binding get() = _binding!!

    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentOthersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = formatDate.format(Date())
            othersTextStartDate.text = currentDate.toString()
            othersTextEndDate.text = currentDate.toString()

            val formatTime = SimpleDateFormat("hh:MM", Locale.getDefault())
            val currentTime = formatTime.format(Date())
            othersTextEndTime.text = currentTime.toString()
            othersTextStartTime.text = currentTime.toString()

            othersTextStartDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = "${setDateFormat(year, month, day)}"
                        othersTextStartDate.text = format
                    }
                }, year, month, day).show()
            }

            othersTextEndDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = "${setDateFormat(year, month, day)}"
                        othersTextEndDate.text = format
                    }
                }, year, month, day).show()
            }

            othersTextStartTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = "${setTimeFormat(hour, min)}"
                        othersTextStartTime.text = format
                    }
                }, hour, min, true).show()
            }

            othersTextEndTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = "${setTimeFormat(hour, min)}"
                        othersTextEndTime.text = format
                    }
                }, hour, min, true).show()
            }
        }

    }
    private fun setDateFormat(year: Int, month: Int, day: Int): String {
        return String.format("%04d-%02d-%02d", year, month+1, day)
    }

    private fun setTimeFormat(hour: Int, min: Int): String {
        return String.format("%02d:%02d", hour, min)
        //"$year-${month + 1}-$day"
    }
}