package com.example.pureveg.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pureveg.R
import com.example.pureveg.databinding.FragmentNotificationBottomSheetBinding
import com.example.pureveg.ui.adapter.NotificationAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NotificationBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBottomSheetBinding.inflate(layoutInflater, container, false)

        val notifications = listOf("Your Order has been Canceled Successfully","Order has been taken by the Driver","Congrats your Order Placed")
        val notifyImages = listOf(R.drawable.icon_cancel,R.drawable.icon_car,R.drawable.icon_done)
        val adapter = NotificationAdapter(ArrayList(notifications), ArrayList(notifyImages))
        binding.rvNotificationList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotificationList.adapter = adapter

        return binding.root
    }

    companion object {

    }
}