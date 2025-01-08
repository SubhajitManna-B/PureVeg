package com.example.pureveg.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pureveg.ui.fragment.SuccessfulOrderBottomSheetFragment
import com.example.pureveg.databinding.ActivityPayOutBinding
import com.example.pureveg.ui.model.OrderDetailsModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodName: ArrayList<String>
    private lateinit var foodPrice: ArrayList<String>
    private lateinit var foodImage: ArrayList<String>
    private lateinit var foodQuantity: ArrayList<Int>
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Initialize firebase auth
        auth = FirebaseAuth.getInstance()
        //Initialize database reference
        database = FirebaseDatabase.getInstance().reference

        //set user data
        setUserData()

        //get user details from intent
        getUserCartDetails()

        binding.btnPayOutBack.setOnClickListener{
            finish()
        }

        binding.btnPlacedOrder.setOnClickListener(){
            name = binding.etPayUserName.text.toString().trim()
            address = binding.etPayUserAddress.text.toString().trim()
            phone = binding.etPayUserPhone.text.toString().trim()
            //Check all the fields are filled or not
            if(name.isEmpty() || address.isEmpty() || phone.isEmpty()){
                Toast.makeText(this,"Please fill all the required fields🙂", Toast.LENGTH_SHORT).show()
            }else{
                placeOrder()
            }
        }
    }

    private fun placeOrder() {
        val userId = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itemPushKey = database.child("order_details").push().key
        val orderDetails = OrderDetailsModel(userId, name, foodName, foodImage, foodPrice, foodQuantity, address, phone, totalAmount, false, false, itemPushKey, time)
        val orderReference = database.child("order_details").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = SuccessfulOrderBottomSheetFragment()
            bottomSheetDialog.show(supportFragmentManager,"Text")
            //remove all the items from the cart and make the cart empty
            removeItemsFromCart()
            addOrderToHistory(orderDetails)
        }.addOnFailureListener {
            Toast.makeText(this,"Order placed failed😢", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetailsModel) {
        database.child("user").child(orderDetails.userId!!).child("buy_history")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails)
    }

    private fun removeItemsFromCart() {
        val userId = auth.currentUser?.uid?:""
        val cartReference = database.child("user").child(userId).child("cart_items")
        cartReference.removeValue()
    }

    private fun getUserCartDetails() {
        foodName = intent.getStringArrayListExtra("CartFoodName") as ArrayList<String>
        foodImage = intent.getStringArrayListExtra("CartFoodImage") as ArrayList<String>
        foodPrice = intent.getStringArrayListExtra("CartFoodPrice") as ArrayList<String>
        foodQuantity = intent.getIntegerArrayListExtra("CartFoodQuantity") as ArrayList<Int>
        //total amount to pay
        totalAmount = calculateTotalAmount()
        binding.tvPayTotalAmount.text = totalAmount
    }

    private fun calculateTotalAmount(): String {
        var totalPrice = 0
        for (i in 0 until foodName.size){
            val price = foodPrice[i].toInt()
            totalPrice += price * foodQuantity[i]
        }
        return totalPrice.toString();
    }

    private fun setUserData() {
        if(auth.currentUser != null){
            val userId = auth.currentUser?.uid?:""
            val userReference = database.child("user").child(userId).child("user_profile")

            userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val name = snapshot.child("userName").getValue(String::class.java)?:""
                        val address = snapshot.child("userAddress").getValue(String::class.java)?:""
                        val phone = snapshot.child("userPhone").getValue(String::class.java)?:""
                        binding.apply {
                            etPayUserName.setText(name)
                            etPayUserAddress.setText(address)
                            etPayUserPhone.setText(phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DatabaseError","Error : ${error.message}")
                }

            })
        }
    }
}