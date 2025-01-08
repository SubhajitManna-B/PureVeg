package com.example.pureveg.ui.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pureveg.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context : Context,
    private val cartItemNames: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private val cartItemImages: MutableList<String>,
    private var cartItemQuantity: MutableList<Int>
): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    //Instance of Firebase
    private val auth = FirebaseAuth.getInstance()
    init {
        //Initialization of firebase
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid?:""
        val cartItemNumber = cartItemNames.size
        itemQuantities = IntArray(cartItemNumber){1}
        cartItemsReference = database.reference.child("user").child(userId).child("cart_items")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartItemNames.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        anim(holder.itemView)
        holder.bind(position)
    }

    //get updated quantity
    fun getUpdatedItemQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartItemQuantity)
        return itemQuantity
    }

    inner class CartViewHolder (private val binding:CartItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                tvCartFoodName.text = cartItemNames[position]
                val price = "Rs: "+cartItemPrices[position]
                tvCartFoodPrice.text = price
                val uriString = cartItemImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(ivCartFoodImage)
                tvQuantity.text = "$quantity"

                btnMinus.setOnClickListener{
                    decreaseQuantity(position)
                }

                btnPlus.setOnClickListener{
                    increaseQuantity(position)
                }

                btnDelete.setOnClickListener{
                    deleteItem(position)
                }
            }
        }

        //Decrease item quantity
        private fun decreaseQuantity(position: Int){
            if(itemQuantities[position] > 1){
                itemQuantities[position]--
                cartItemQuantity[position] = itemQuantities[position]
                binding.tvQuantity.text = "${itemQuantities[position]}"
            }
        }

        //Increase item quantity
        private fun increaseQuantity(position: Int){
            if(itemQuantities[position] < 10){
                itemQuantities[position]++
                cartItemQuantity[position] = itemQuantities[position]
                binding.tvQuantity.text = "${itemQuantities[position]}"
            }
        }

        //Delete item from cart
        private fun deleteItem(position: Int){
            val retrievePosition = position
            //Find the unique of the item
            getUniqueKeyAtPosition(retrievePosition){uniqueKey ->
                if (uniqueKey != null){
                    removeItem(position, uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                cartItemNames.removeAt(position)
                cartItemPrices.removeAt(position)
                cartItemQuantity.removeAt(position)
                cartItemImages.removeAt(position)
                Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                //Update item quantities
                itemQuantities = itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItemNames.size)
            }.addOnFailureListener {
                Toast.makeText(context, "Item deletion failed", Toast.LENGTH_SHORT).show()
            }
        }

        private fun getUniqueKeyAtPosition(retrievePosition: Int, onComplete:(String?) -> Unit ) {
            cartItemsReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if(index == retrievePosition){
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DatabaseError","Error : ${error.message}")
                }

            })
        }
    }

    //Animation
    private fun anim(view: View){
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 1000
        view.startAnimation(animation)
    }

    companion object{
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }
}