package com.fyp.smartbus.ui.buses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fyp.smartbus.ui.MainActivity
import com.fyp.smartbus.databinding.FragmentBusListBinding
import com.fyp.smartbus.utils.invisible
import com.fyp.smartbus.viewmodel.BusListViewModel
import com.fyp.smartbus.utils.toast
import com.fyp.smartbus.utils.visible

class BusListFragment : Fragment() {

    private lateinit var vmBusList: BusListViewModel
    private lateinit var binding: FragmentBusListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBusListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vmBusList =
            ViewModelProvider(requireActivity()).get(BusListViewModel::class.java)

        val adapter = GridAdapter(requireContext()) {
            (requireActivity() as MainActivity).showBusOnMap(it)
        }

        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        adapter.setOnDirectionsClickListener { bus ->
            if(bus.currentloc == null && bus.lastloc == null) {
                toast("Directions not available for this bus")
            } else {
                (requireActivity() as MainActivity).directionsOnHome(bus)
            }
        }

        vmBusList.busList.observe(viewLifecycleOwner) { busList ->
            if(busList.isEmpty()) {
                binding.error.visible()
            } else {
                binding.error.invisible()
                adapter.updateList(busList)
            }
        }

        vmBusList.error.observe(viewLifecycleOwner) { e ->
            if(e == null) {
                binding.error.invisible()
            } else {
                binding.error.apply {
                    error = e.message
                    visible()
                }
                e.printStackTrace()
            }
        }


        vmBusList.getAllBuses()
    }
}