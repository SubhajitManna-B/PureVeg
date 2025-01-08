package com.example.pureveg.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.example.pureveg.databinding.PopularItemBinding
import com.example.pureveg.ui.DetailsActivity

class PopularAdapter(private val items:List<String>,private val prices:List<String>, private val images:List<Int>, private val requireContext: Context) :
    RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        anim(holder.itemView)
        val item = items[position]
        val price = prices[position]
        val image = images[position]
        holder.bind(item,price,image)

        holder.itemView.setOnClickListener{
            //setonclick listner to open details
            val intent = Intent(requireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage", image)
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

   inner class PopularViewHolder(private val binding : PopularItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String,price: String, image: Int) {
            binding.tvPopularFoodName.text = item
            binding.tvPopularPrice.text = price
            binding.ivPopularFood.setImageResource(image)
        }
    }

    //Animation
    private fun anim(view: View){
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 1000
        view.startAnimation(animation)
    }
}