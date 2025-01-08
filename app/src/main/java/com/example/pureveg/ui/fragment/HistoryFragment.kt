package com.example.pureveg.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorLong
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pureveg.R
import com.example.pureveg.databinding.FragmentHistoryBinding
import com.example.pureveg.ui.RecentBuyActivity
import com.example.pureveg.ui.adapter.PreviousBuyAdapter
import com.example.pureveg.ui.model.OrderDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var preBuyAdapter : PreviousBuyAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderedItem: MutableList<OrderDetailsModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        //Initialize auth and database intense
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //Retrieve and display the user order history
        retrieveBuyHistory()

        //On click of recent buy item Show all the recent buy items
        binding.clRecentBuy.setOnClickListener {
            //Show all the recent buy items
            showItemsRecentBuy()
        }

        //Set the function of receive order button
        binding.btnOrderReceiveStatus.setOnClickListener {
            updateOrderStatus()
        }

        return binding.root
    }

    //Function to show all the recent buy items in recent buy activity
    private fun showItemsRecentBuy() {
        listOfOrderedItem.firstOrNull()?.let {
            val intent = Intent(requireContext(),RecentBuyActivity::class.java)
            intent.putExtra("RecentBuyItems",listOfOrderedItem as ArrayList<OrderDetailsModel>)
            startActivity(intent)
        }
    }

    //Function to retrieve all the data from database
    private fun retrieveBuyHistory() {
        binding.clRecentBuy.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid?:""
        val buyItemReference = database.reference.child("user").child(userId).child("buy_history")
        val sortingQuery = buyItemReference.orderByChild("currentTime")
        sortingQuery.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children){
                    val buyItemHistory = buySnapshot.getValue(OrderDetailsModel::class.java)
                    buyItemHistory?.let {
                        listOfOrderedItem.add(it)
                    }
                }
                listOfOrderedItem.reverse()
                if (listOfOrderedItem.isNotEmpty()){
                    //Display the most recent buy item
                    setDataInRecentBuyItem()
                    //Set recycler view to Display the previous buy items
                    setPreviousBuyItemAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }

        })
    }

    //Function to Display the most recent buy item
    private fun setDataInRecentBuyItem() {
        binding.clRecentBuy.visibility = View.VISIBLE
        val recentBuyItem = listOfOrderedItem.firstOrNull()
        recentBuyItem?.let {
            with(binding){
                tvRecentBuyFoodName.text = it.foodNames?.firstOrNull()?:""
                val totalPrice = "Rs: " + it.totalPrice
                tvRecentBuyFoodTotalPrice.text = totalPrice
                val image = it.foodImages?.firstOrNull()?:""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(ivRecentFoodImage)
                val isOrderAccepted = recentBuyItem.orderAccepted
                if(isOrderAccepted){
                    cvOrderAcceptStatus.background.setTint(Color.GREEN)
                    btnOrderReceiveStatus.visibility = View.VISIBLE
                }
            }
        }
    }

    //Function to Set recycler view to Display the previous buy items
    private fun setPreviousBuyItemAdapter() {
        val preFoodNames = mutableListOf<String>()
        val preFoodTotalPrices = mutableListOf<String>()
        val preFoodImages = mutableListOf<String>()
        for (i in 1 until listOfOrderedItem.size){
            listOfOrderedItem[i].foodNames?.firstOrNull()?.let { preFoodNames.add(it) }
            listOfOrderedItem[i].totalPrice?.let { preFoodTotalPrices.add(it) }
            listOfOrderedItem[i].foodImages?.firstOrNull()?.let { preFoodImages.add(it) }
        }
        //Set the adapter
        preBuyAdapter = PreviousBuyAdapter(preFoodNames, preFoodTotalPrices, preFoodImages, requireContext())
        binding.rvPrevoiusHoistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPrevoiusHoistory.adapter = preBuyAdapter
    }

    //Function to update payment receive on database
    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderedItem[0].itemPushKey
        val orderDetailsRef = database.reference.child("completed_Order").child(itemPushKey!!)
        orderDetailsRef.child("paymentReceived").setValue(true)
    }

    companion object {
    }
}