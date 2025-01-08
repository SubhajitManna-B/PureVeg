package com.example.pureveg.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pureveg.databinding.PreviousBuyItemBinding

class PreviousBuyAdapter(
    private val preBuyFoodNName: MutableList<String>,
    private val preBuyFoodPrice: MutableList<String> ,
    private val preBuyFoodImage: MutableList<String>,
    private var requireContext: Context
): RecyclerView.Adapter<PreviousBuyAdapter.PreviousBuyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousBuyViewHolder {
        val binding = PreviousBuyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PreviousBuyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreviousBuyViewHolder, position: Int) {
        holder.bind(
            preBuyFoodNName[position],
            preBuyFoodPrice[position],
            preBuyFoodImage[position]
        )
    }

    override fun getItemCount(): Int {
        return preBuyFoodNName.size
    }

    inner class PreviousBuyViewHolder(private val binding: PreviousBuyItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(foodName: String, foodPrice: String, foodImage: String) {
            binding.apply {
                tvPreBuyFoodName.text = foodName
                val price = "Rs: $foodPrice"
                tvPreBuyFoodPrice.text = price
                val uri = Uri.parse(foodImage)
                Glide.with(requireContext).load(uri).into(ivPreBuyFoodImage)
            }
        }

    }
}