package com.example.gerdapp.ui.main

import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.MainActivity
import com.example.gerdapp.Notification
import com.example.gerdapp.R
import com.example.gerdapp.adapter.CardItemAdapter
import com.example.gerdapp.data.Result
import com.example.gerdapp.databinding.FragmentMainBinding
import com.example.gerdapp.ui.main.records.SymptomsRecordFragment
import com.example.gerdapp.viewmodel.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.VISIBLE

    private lateinit var recyclerView: RecyclerView

    private object SymptomsScore {
        var time: String ?= null
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
            SymptomsScore.time.toString()
        ) && !symptomsScoreIsEmpty()
    }

    private fun symptomsScoreIsEmpty(): Boolean {
        return SymptomsScore.coughScore==0 && SymptomsScore.heartBurnScore==0 && SymptomsScore.acidRefluxScore==0 && SymptomsScore.chestPainScore==0
                && SymptomsScore.sourMouthScore==0 && SymptomsScore.hoarsenessScore==0 && SymptomsScore.appetiteLossScore==0 && SymptomsScore.stomachGasScore==0
    }

    private fun addNewItem() = if(isEntryValid()) {
        viewModel.addSymptomRecord(
            SymptomsScore.time!!,
            SymptomsScore.coughScore, SymptomsScore.heartBurnScore, SymptomsScore.acidRefluxScore, SymptomsScore.chestPainScore,
            SymptomsScore.sourMouthScore, SymptomsScore.hoarsenessScore, SymptomsScore.appetiteLossScore, SymptomsScore.stomachGasScore,
            ""
        )
        Toast.makeText(context, "record added", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "invalid input", Toast.LENGTH_SHORT).show()
    }

    private val sleepViewModel: SleepViewModel by activityViewModels {
        SleepViewModelFactory(
            (activity?.application as BasicApplication).sleepDatabase.sleepDao()
        )
    }

//    private val othersViewModel: OthersViewModel by activityViewModels {
//        OthersViewModelFactory(
//            (activity?.application as BasicApplication).othersDatabase.othersDao()
//        )
//    }
//
//    private val foodViewModel: FoodViewModel by activityViewModels {
//        FoodViewModelFactory(
//            (activity?.application as BasicApplication).foodDatabase.foodDao()
//        )
//    }

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        var sleepRecentData = "sleep data"

//        sleepViewModel.getRecentRecord().observe(this.viewLifecycleOwner) {
//            sleepRecentData = it.startTime
//        }
//        var othersRecentData = "others data"
//        othersViewModel.getRecentRecord().observe(this.viewLifecycleOwner) {
//            othersRecentData = it.startTime
//        }
//        var foodRecentData = ""
//        foodViewModel.getRecentRecord().observe(this.viewLifecycleOwner) {
//            foodRecentData = it.startTime
//        }

        val adapter = CardItemAdapter({ cardItem ->
            val action = when (cardItem.stringResourceId) {
                R.string.symptoms -> MainFragmentDirections.actionMainFragmentToSymptomsFragment()
                R.string.medicine -> MainFragmentDirections.actionMainFragmentToDrugRecordFragment()
                R.string.food -> MainFragmentDirections.actionMainFragmentToFoodFragment()
                R.string.sleep -> MainFragmentDirections.actionMainFragmentToSleepFragment()
                R.string.others -> MainFragmentDirections.actionMainFragmentToOthersFragment()
                else -> MainFragmentDirections.actionMainFragmentSelf()
            }
            findNavController().navigate(action)
        }) { cardItem ->
            val recentRecord = when (cardItem.stringResourceId) {
                R.string.symptoms -> ""
//                R.string.food -> foodRecentData
                R.string.sleep -> {
//                    try {
//                        sleepViewModel.getRecentRecord().value?.startTime
//                    } catch (e: NullPointerException) {
//                        "No Current Data"
//                    }
                    ""
                }
//                R.string.others -> othersRecentData
                else -> ""
            }
//            text = recentRecord.toString()
            recentRecord
        }

        recyclerView.adapter = adapter

        binding.apply {
            val calendar = Calendar.getInstance()
            val current = calendar.time
            val formatDate =
                SimpleDateFormat(getString(R.string.simple_date_format), Locale.getDefault())
            val currentDate = formatDate.format(current)
            testButton.setOnClickListener { view ->
                Snackbar.make(view, "", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            setSymptomsCard()

            if(!Notification.notificationOn) {
                notification.layout.visibility = View.GONE
            } else {
                notification.layout.visibility = View.VISIBLE
            }

            notification.cancelButton.setOnClickListener {
                notification.layout.visibility = View.GONE
                Notification.notificationOn = false
            }
        }
    }

    private fun setSymptomsCard() {
        binding.apply {
            symptomsButtons.symptomsCough.setOnClickListener {
                if(SymptomsScore.coughScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.coughScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.coughScore = 0
                }
            }
            symptomsButtons.symptomsHeartBurn.setOnClickListener{
                if(SymptomsScore.heartBurnScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.heartBurnScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.heartBurnScore = 0
                }
            }
            symptomsButtons.symptomsAcidReflux.setOnClickListener {
                if(SymptomsScore.acidRefluxScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.acidRefluxScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.acidRefluxScore = 0
                }
            }
            symptomsButtons.symptomsChestPain.setOnClickListener{
                if(SymptomsScore.chestPainScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.chestPainScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.chestPainScore = 0
                }
            }
            symptomsButtons.symptomsSourMouth.setOnClickListener {
                if(SymptomsScore.sourMouthScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.sourMouthScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.sourMouthScore = 0
                }
            }
            symptomsButtons.symptomsHoarseness.setOnClickListener{
                if(SymptomsScore.hoarsenessScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.hoarsenessScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.hoarsenessScore = 0
                }
            }
            symptomsButtons.symptomsAppetiteLoss.setOnClickListener {
                if(SymptomsScore.appetiteLossScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.appetiteLossScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.appetiteLossScore = 0
                }
            }
            symptomsButtons.symptomsStomachGas.setOnClickListener{
                if(SymptomsScore.stomachGasScore==0){
                    it.setBackgroundResource(R.drawable.circular)
                    SymptomsScore.stomachGasScore = 1
                } else {
                    it.setBackgroundColor(Color.TRANSPARENT)
                    SymptomsScore.stomachGasScore = 0
                }
            }

            addSymptoms.setOnClickListener {
                val calendar = Calendar.getInstance()
                val current = calendar.time // TODO: Check if the time match the device time zone
                val formatDateTime = SimpleDateFormat(getString(R.string.simple_date_time_format), Locale.getDefault())
                val currentDateTime = formatDateTime.format(current)

                SymptomsScore.time = currentDateTime

                addNewItem()

                SymptomsScore.time = null
                SymptomsScore.coughScore = 0
                SymptomsScore.heartBurnScore = 0
                SymptomsScore.acidRefluxScore = 0
                SymptomsScore.sourMouthScore = 0
                SymptomsScore.hoarsenessScore = 0
                SymptomsScore.appetiteLossScore = 0
                SymptomsScore.stomachGasScore = 0

                symptomsButtons.symptomsCough.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsHeartBurn.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsAcidReflux.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsChestPain.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsSourMouth.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsHoarseness.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsAppetiteLoss.setBackgroundColor(Color.TRANSPARENT)
                symptomsButtons.symptomsStomachGas.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}