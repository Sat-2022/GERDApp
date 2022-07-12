package com.example.gerdapp.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.databinding.FragmentMainBinding
import javax.sql.DataSource

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
                R.string.symptoms -> MainFragmentDirections.actionMainFragmentToSymptomsFragment()
                R.string.food -> MainFragmentDirections.actionMainFragmentToFoodFragment()
                R.string.sleep -> MainFragmentDirections.actionMainFragmentToSleepFragment()
                else -> MainFragmentDirections.actionMainFragmentSelf()
            }
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter
    }
}