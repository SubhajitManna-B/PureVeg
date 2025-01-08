package com.example.pureveg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pureveg.databinding.NotificationItemBinding

class NotificationAdapter(private val notifyMessage:ArrayList<String>, private val notifyImages:ArrayList<Int>):
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.NotificationViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.NotificationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return notifyMessage.size
    }

    inner class NotificationViewHolder(private val binding : NotificationItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvNotificationMsg.text = notifyMessage[position]
                ivNotificationImage.setImageResource(notifyImages[position])
            }
        }

    }
}