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
import com.example.gerdapp.databinding.FragmentOthersBinding
import com.example.gerdapp.viewmodel.OthersViewModel
import com.example.gerdapp.viewmodel.OthersViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class OthersFragment: Fragment() {
    private var _binding: FragmentOthersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OthersViewModel by activityViewModels {
        OthersViewModelFactory(
            (activity?.application as BasicApplication).othersDatabase.othersDao()
        )
    }

    lateinit var others: Others

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.othersTextStartDate.text.toString()+" "+binding.othersTextStartTime.text.toString(),
            binding.othersTextEndDate.text.toString()+" "+binding.othersTextEndTime.text.toString(),
            binding.othersRecordInput.text.toString()
        )
    }

    private fun addNewItem(){
        if(isEntryValid()) {
            viewModel.addOthersRecord(
                binding.othersTextStartDate.text.toString()+" "+binding.othersTextStartTime.text.toString(),
                binding.othersTextEndDate.text.toString()+" "+binding.othersTextEndTime.text.toString(),
                binding.othersRecordInput.text.toString()
            )
        }
    }

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

            val calendar = Calendar.getInstance()
            val current = calendar.time // TODO: Check if the time match the device time zone

            val formatDate = SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
            val currentDate = formatDate.format(current)
            othersTextStartDate.text = currentDate.toString()
            othersTextEndDate.text = currentDate.toString()

            val formatTime = SimpleDateFormat(getString(R.string.simple_time_format), Locale.getDefault())
            val currentTime = formatTime.format(current)
            othersTextEndTime.text = currentTime.toString()
            othersTextStartTime.text = currentTime.toString()

            othersTextStartDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = "${getString(R.string.date_format, year, month, day)}"
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
                        val format = "${getString(R.string.date_format, year, month, day)}"
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
                        val format = "${getString(R.string.time_format, hour, min)}"
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
                        val format = "${getString(R.string.time_format, hour, min)}"
                        othersTextEndTime.text = format
                    }
                }, hour, min, true).show()
            }

            othersButtonCancel.setOnClickListener {
                findNavController().navigate(R.id.action_othersFragment_to_mainFragment)
            }

            othersButtonDone.setOnClickListener {
                addNewItem()
                findNavController().navigate(R.id.action_othersFragment_to_mainFragment)
            }
        }

    }
}