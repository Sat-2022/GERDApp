package com.example.gerdapp.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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


        val adapter = CardItemAdapter { cardItem ->
            val action = when(cardItem.stringResourceId){
                R.string.questionnaire -> MainFragmentDirections.actionMainFragmentToCalendarFragment()
                R.string.symptoms -> MainFragmentDirections.actionMainFragmentToSymptomsFragment()
                R.string.food -> MainFragmentDirections.actionMainFragmentToFoodFragment()
                R.string.sleep -> MainFragmentDirections.actionMainFragmentToSleepFragment()
                R.string.others -> MainFragmentDirections.actionMainFragmentToOthersFragment()
                R.string.chart -> MainFragmentDirections.actionMainFragmentToChartFragment()
                else -> MainFragmentDirections.actionMainFragmentSelf()
            }
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter
    }
}
