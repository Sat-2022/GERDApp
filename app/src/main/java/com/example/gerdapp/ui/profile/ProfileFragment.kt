package com.example.gerdapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gerdapp.MainActivity
import com.example.gerdapp.User
import com.example.gerdapp.databinding.FragmentProfileBinding
import com.google.gson.Gson
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val okHttp = com.example.gerdapp.OkHttp(requireContext())
//        binding.apply {
//            testApi.setOnClickListener {
//
//                testApi.text = okHttp.getData()
//            }
//        }
        binding.apply {
            //testApi.text = MainActivity.user
        }
    }
}