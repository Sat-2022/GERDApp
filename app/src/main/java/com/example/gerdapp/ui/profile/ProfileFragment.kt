package com.example.gerdapp.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gerdapp.TestUser
import com.example.gerdapp.User
import com.example.gerdapp.databinding.FragmentProfileBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    var testUsers: List<TestUser>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        testApi().start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
        }
    }

    private fun testApi(): Thread {
        return Thread {
            val url = URL("http://120.126.40.203/GERD_API/api/test/R092&20220801")
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<TestUser>>() {}.type
                testUsers = Gson().fromJson(inputStreamReader, type)
                UpdateUI()
                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "$testUsers")
            } else
                Log.e("API Connection", "failed ${connection.responseMessage}")
        }
    }

    private fun UpdateUI() {
        activity?.runOnUiThread {
            binding.apply {
                testApi.text = testUsers.toString()
            }
        }
    }
}