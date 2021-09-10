package com.fyp.smartbus.ui.buses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fyp.smartbus.R
import com.fyp.smartbus.api.app.Bus
import com.fyp.smartbus.databinding.RvBusItemBinding

class GridAdapter(private val context: Context, private val onClick: (bus: Bus) -> Unit) :
    RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    private var list: List<Bus>? = null
    private var onDirections: ((bus: Bus) -> Unit)? = null

    inner class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RvBusItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = RvBusItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return GridViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        with(holder) {
            list?.let { items ->
                with(items[position]) {
                    binding.busNo.text = busno
                    binding.driverName.text = username
                    binding.card.setOnClickListener {
                        onClick.invoke(this)
                    }
                    val tint = if(isonline == true) R.color.busOnline else R.color.busOffline
                    binding.ivBus.setColorFilter(ContextCompat.getColor(context, tint))
                    binding.btnDirections.setOnClickListener {
                        onDirections?.invoke(this)
                    }
                }
            }
        }
    }

    fun setOnDirectionsClickListener(onClick: (bus: Bus) -> Unit) {
        onDirections = onClick
    }

    fun updateList(list: List<Bus>) {
        this.list = list
        notifyDataSetChanged()
    }

}
