package com.example.pureveg.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pureveg.databinding.FragmentSuccessfulOrderBottomSheetBinding
import com.example.pureveg.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SuccessfulOrderBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentSuccessfulOrderBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSuccessfulOrderBottomSheetBinding.inflate(layoutInflater, container, false)

        binding.btnGoHome.setOnClickListener(){
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            dismiss()
        }

        return binding.root
    }

    companion object {
    }
}