package com.example.pureveg.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pureveg.databinding.ActivityDetailsBinding
import com.example.pureveg.ui.model.CartItemModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodName : String? = null
    private var foodImage : String? = null
    private var foodPrice : String? = null
    private var foodDescription : String? = null
    private var foodIngredient : String? = null
    private var foodQuantity : Int? = 1
    private lateinit var database: DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize firebase auth
        auth = Firebase.auth
        //initialize firebase database
        database = Firebase.database.reference

        //Intent data
        foodName = intent.getStringExtra("MenuItemName")
        foodImage = intent.getStringExtra("MenuItemImage")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredient = intent.getStringExtra("MenuItemIngredient")
        foodPrice = intent.getStringExtra("MenuItemPrice")

        //Show the data in activity
        binding.tvDetailsFoodName.text = foodName
        binding.tvDetailsDescription.text = foodDescription
        binding.tvDetailsIngredients.text = foodIngredient
        Glide.with(this).load(Uri.parse(foodImage)).into(binding.ivDetailsImage)

        //Set add to cart button
        binding.btnAddToCart.setOnClickListener {
            //Call add to cart function
            addToCart()
        }

        binding.btnDetailsBack.setOnClickListener{
            finish()
        }
    }

    private fun addToCart() {
        val userId = auth.currentUser?.uid?:""
        //Create a cart items object
        val cartItem = CartItemModel(foodName.toString(), foodPrice.toString(), foodImage.toString(), foodQuantity)
        database.child("user").child(userId).child("cart_items").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Item added to cart successfully😊",Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Item not added to cart🥲", Toast.LENGTH_SHORT).show()
        }
    }
}