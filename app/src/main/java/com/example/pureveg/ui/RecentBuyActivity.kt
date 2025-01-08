package com.example.pureveg.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pureveg.databinding.ActivityRecentBuyBinding
import com.example.pureveg.ui.adapter.RecentBuyAdapter
import com.example.pureveg.ui.model.OrderDetailsModel

class RecentBuyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentBuyBinding
    private lateinit var foodNameList: ArrayList<String>
    private lateinit var foodImageList: ArrayList<String>
    private lateinit var foodPriceList: ArrayList<String>
    private lateinit var foodQuantityList: ArrayList<Int>
    private lateinit var recentAdapter: RecentBuyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecentBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRecentBuyBack.setOnClickListener {
            finish()
        }

        //Get the intent data from the fragment history
        val recentOrderItems = intent.getSerializableExtra("RecentBuyItems") as ArrayList<OrderDetailsModel>
        recentOrderItems.let {orderItem ->
            if (orderItem.isNotEmpty()){
                val recentItem = orderItem[0]
                foodNameList = recentItem.foodNames as ArrayList<String>
                foodImageList = recentItem.foodImages as ArrayList<String>
                foodPriceList = recentItem.foodPrices as ArrayList<String>
                foodQuantityList = recentItem.foodQuantities as ArrayList<Int>
            }
        }
        //set data in the adapterp
        setAdapter()
    }

    //Set adapter to receive the data and show it on recycler view
    private fun setAdapter() {
        recentAdapter = RecentBuyAdapter(this, foodNameList, foodImageList, foodPriceList, foodQuantityList)
        binding.rvRecentBuy.layoutManager = LinearLayoutManager(this)
        binding.rvRecentBuy.adapter = recentAdapter
    }
}