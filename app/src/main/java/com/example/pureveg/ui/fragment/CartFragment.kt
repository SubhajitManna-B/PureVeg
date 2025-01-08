package com.example.pureveg.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pureveg.databinding.FragmentCartBinding
import com.example.pureveg.ui.PayOutActivity
import com.example.pureveg.ui.adapter.CartAdapter
import com.example.pureveg.ui.model.CartItemModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {
    private lateinit var binding : FragmentCartBinding
    private var cartItemNames: MutableList<String> = mutableListOf()
    private var cartItemPrices: MutableList<String> = mutableListOf()
    private var cartItemImages: MutableList<String> = mutableListOf()
    private var cartItemQuantity: MutableList<Int> = mutableListOf()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private lateinit var cartAdapter: CartAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater,container,false)
        //initialize firebase auth
        auth = Firebase.auth
        setAdapter()
        //Retrieve Cart items from firebase database
        retrieveCartItems()

        //set the proceed button
        binding.btnProceed.setOnClickListener(){
            //Get order item details before processing to checkout
            getOrderItemDetails()
        }
        return binding.root
    }

    private fun getOrderItemDetails() {
        val orderIdReference: DatabaseReference = database.reference.child("user").child(userId).child("cart_items")
        val foodNames = mutableListOf<String>()
        val foodPrices = mutableListOf<String>()
        val foodImages = mutableListOf<String>()
        //get item quantities
        val foodQuantities = cartAdapter.getUpdatedItemQuantities()

        orderIdReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    //get the cart item to the respected list
                    val orderItem = foodSnapshot.getValue(CartItemModel::class.java)
                    //add item details into the list
                    orderItem?.foodName?.let { foodNames.add(it) }
                    orderItem?.foodPrice?.let { foodPrices.add(it) }
                    orderItem?.foodImage?.let { foodImages.add(it) }
                }
                orderNow(foodNames, foodPrices, foodImages, foodQuantities)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }

        })
    }

    private fun orderNow(
        foodNames: MutableList<String>,
        foodPrices: MutableList<String>,
        foodImages: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
        if(isAdded && context != null){
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("CartFoodName",foodNames as ArrayList<String>)
            intent.putExtra("CartFoodPrice",foodPrices as ArrayList<String>)
            intent.putExtra("CartFoodImage",foodImages as ArrayList<String>)
            intent.putExtra("CartFoodQuantity",foodQuantities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        //database reference
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid?:""
        val foodReference: DatabaseReference = database.reference.child("user").child(userId).child("cart_items")
        //fetch data from database
        foodReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val cartItem = foodSnapshot.getValue(CartItemModel::class.java)
                    cartItem?.foodName?.let { cartItemNames.add(it) }
                    cartItem?.foodPrice?.let { cartItemPrices.add(it) }
                    cartItem?.foodImage?.let { cartItemImages.add(it) }
                    cartItem?.foodQuantity?.let { cartItemQuantity.add(it) }
                }
                //set adapter for show the data
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }

        })
    }

    private fun setAdapter() {
        cartAdapter = CartAdapter(requireContext(), cartItemNames, cartItemPrices, cartItemImages, cartItemQuantity)
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCart.adapter = cartAdapter
    }
}
