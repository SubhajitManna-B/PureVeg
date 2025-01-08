package com.example.pureveg.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pureveg.databinding.MenuItemBinding
import com.example.pureveg.ui.DetailsActivity
import com.example.pureveg.ui.model.MenuItemModel

class MenuAdapter(
    private val menuItems : List<MenuItemModel>,
    private val requireContext : Context):
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val itemClickListener : OnClickListener ?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        anim(holder.itemView)
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    openDetailsActivity(position)
                }
            }
        }

        //Open details activity to show the data of item
        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItems[position]

            //Intent to open details activity  and pass the data
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredient", menuItem.foodIngredient)
            }
            //Start the required activity
            requireContext.startActivity(intent)
        }

        //Set data into recycler view item
        fun bind(position: Int) {
            binding.apply {
                tvMenuFoodName.text = menuItems[position].foodName
                val price = "Rs: " +menuItems[position].foodPrice
                tvMenuPrice.text = price
                val uri = Uri.parse(menuItems[position].foodImage)
                Glide.with(requireContext).load(uri).into(ivMenuFood)
            }
        }
    }

    private fun anim(view: View){
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 1000
        view.startAnimation(animation)
    }
    interface OnClickListener {
        fun onItemClick(position: Int){

        }
    }
}

