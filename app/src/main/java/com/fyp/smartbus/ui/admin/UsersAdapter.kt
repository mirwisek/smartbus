package com.fyp.smartbus.ui.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fyp.smartbus.R
import com.fyp.smartbus.api.app.User
import com.fyp.smartbus.databinding.RvAdminUserBinding
import com.fyp.smartbus.utils.invisible

class UsersAdapter(
    private val context: Context,
    private var users: List<User>? = null,
    private val onAccept: ((User) -> Unit)? = null
) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RvAdminUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvAdminUserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        users?.get(position)?.let { item ->
            holder.binding.apply {
                username.text = item.username
                email.text = item.email

                val icon: Int

                when(item.usertype) {
                    "D" -> {
                        icon = R.drawable.ic_bus_offline
                        busNo.text = item.busno
                    }
                    else -> {
                        icon = R.drawable.ic_person
                        busNo.invisible()
                    }
                }

                ivBus.setImageDrawable(ContextCompat.getDrawable(context, icon))

                btnAccept.setOnClickListener {
                    onAccept?.invoke(item)
                }
            }
        }
    }

    fun updateList(list: List<User>) {
        users = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return users?.size ?: 0
    }
}