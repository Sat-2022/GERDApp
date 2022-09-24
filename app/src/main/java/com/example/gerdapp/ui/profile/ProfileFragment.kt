package com.example.gerdapp.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gerdapp.*
import com.example.gerdapp.databinding.FragmentProfileBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.VISIBLE
    private var actionbarTitleEnable = true

    private lateinit var preferences: SharedPreferences

    object User {
        var caseNumber = ""
        var gender = ""
        var nickname = ""
    }

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)

        mainActivity.setActionBarTitleEnable(actionbarTitleEnable)
        mainActivity.setActionBarTitle()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        User.caseNumber = preferences.getString("caseNumber", "").toString()
        User.gender = preferences.getString("gender", "").toString()
        User.nickname = preferences.getString("nickname", "").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            testButton.setOnClickListener {
                val preferences: SharedPreferences = context?.getSharedPreferences("config", 0)!!
                val editor: SharedPreferences.Editor = preferences.edit()

                editor.putBoolean("loggedIn", false)

                editor.commit()

                val intent = Intent(requireContext(), SplashActivity::class.java)
                startActivity(intent)
            }

            tvUserAcc.text = "\t案號：" + User.caseNumber
            tvUserNickname.text = "\t暱稱：" + User.nickname
            if(User.gender == "1") tvUserGender.text = "\t性別：男"
            else if(User.gender == "2") tvUserGender.text = "\t性別：女"
            else tvUserGender.text = "\t性別："
        }
    }
}