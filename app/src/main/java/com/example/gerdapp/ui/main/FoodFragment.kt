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
import com.example.gerdapp.data.Food
import com.example.gerdapp.data.Others
import com.example.gerdapp.databinding.FragmentFoodBinding
import com.example.gerdapp.viewmodel.FoodViewModel
import com.example.gerdapp.viewmodel.FoodViewModelFactory
import com.example.gerdapp.viewmodel.OthersViewModel
import com.example.gerdapp.viewmodel.OthersViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class FoodFragment: Fragment() {
    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodViewModel by activityViewModels {
        FoodViewModelFactory(
            (activity?.application as BasicApplication).foodDatabase.foodDao()
        )
    }

    lateinit var food: Food

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.foodTextStartDate.text.toString()+" "+binding.foodTextStartTime.text.toString(),
            binding.foodTextEndDate.text.toString()+" "+binding.foodTextEndTime.text.toString(),
            binding.foodRecordInput.text.toString()
        )
    }

    private fun addNewItem(){
        if(isEntryValid()) {
            viewModel.addFoodRecord(
                binding.foodTextStartDate.text.toString()+" "+binding.foodTextStartTime.text.toString(),
                binding.foodTextEndDate.text.toString()+" "+binding.foodTextEndTime.text.toString(),
                binding.foodRecordInput.text.toString()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val calendar = Calendar.getInstance()
            val current = calendar.time // TODO: Check if the time match the device time zone

            val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = formatDate.format(current)
            foodTextStartDate.text = currentDate.toString()
            foodTextEndDate.text = currentDate.toString()

            val formatTime = SimpleDateFormat("hh:mm", Locale.getDefault())
            val currentTime = formatTime.format(current)
            foodTextEndTime.text = currentTime.toString()
            foodTextStartTime.text = currentTime.toString()

            foodTextStartDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = "${setDateFormat(year, month, day)}"
                        foodTextStartDate.text = format
                    }
                }, year, month, day).show()
            }

            foodTextEndDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_MONTH]
                val month = calendar[Calendar.MONTH]
                val year = calendar[Calendar.YEAR]

                DatePickerDialog(requireContext(), { _, year, month, day ->
                    run {
                        val format = "${setDateFormat(year, month, day)}"
                        foodTextEndDate.text = format
                    }
                }, year, month, day).show()
            }

            foodTextStartTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = "${setTimeFormat(hour, min)}"
                        foodTextStartTime.text = format
                    }
                }, hour, min, true).show()
            }

            foodTextEndTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]

                TimePickerDialog(requireContext(), { _, hour, min ->
                    run {
                        val format = "${setTimeFormat(hour, min)}"
                        foodTextEndTime.text = format
                    }
                }, hour, min, true).show()
            }

            foodButtonCancel.setOnClickListener {
                addNewItem()
                findNavController().navigate(R.id.action_foodFragment_to_mainFragment)
            }

            foodButtonDone.setOnClickListener {
                findNavController().navigate(R.id.action_foodFragment_to_mainFragment)
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