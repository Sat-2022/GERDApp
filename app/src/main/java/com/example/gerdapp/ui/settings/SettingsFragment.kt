package com.example.gerdapp.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.MainActivity
import com.example.gerdapp.R
import com.example.gerdapp.adapter.SettingsAdapter
import com.example.gerdapp.databinding.FragmentSettingsBinding

/**********************************************
 * Fragment of setting page
 **********************************************/
class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.GONE
    private var actionbarTitleEnable = false

    private lateinit var recyclerView: RecyclerView

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
        mainActivity.setActionBarExpanded(false)
        mainActivity.setActionBarTitleEnable(actionbarTitleEnable)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.menu_settings).isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.settingsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = SettingsAdapter { item ->
            val action = when (item.id) {

                else -> SettingsFragmentDirections.actionSettingsFragmentSelf()
            }
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter
        binding.apply {
        }
    }
}