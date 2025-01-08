package com.example.pureveg.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.pureveg.databinding.RecentBuyItemBinding

class RecentBuyAdapter(
    private var context: Context,
    private var itemNames: ArrayList<String>,
    private var itemImages: ArrayList<String>,
    private var itemPrices: ArrayList<String>,
    private var itemQuantities: ArrayList<Int>
): RecyclerView.Adapter<RecentBuyAdapter.RecentBuyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentBuyViewHolder {
        val binding = RecentBuyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentBuyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentBuyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return itemNames.size
    }

    inner class RecentBuyViewHolder(private val binding: RecentBuyItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvRecentFoodName.text = itemNames[position]
            val uri = Uri.parse(itemImages[position])
            Glide.with(context).load(uri).into(binding.ivRecentBuyFood)
            binding.tvRecentBuyItemQuantity.text = "${itemQuantities[position]}"
            val price = "Rs: "+itemPrices[position]
            binding.tvRecentBuyPrice.text = price
        }

    }
}