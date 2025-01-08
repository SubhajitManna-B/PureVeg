package com.example.pureveg.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pureveg.databinding.FragmentMenuBottomSheetBinding
import com.example.pureveg.ui.adapter.MenuAdapter
import com.example.pureveg.ui.model.MenuItemModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentMenuBottomSheetBinding
    private lateinit var database : FirebaseDatabase
    private var menuItems : MutableList<MenuItemModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener{
            dismiss()
        }
        setAdapter()
        //Retrieve all the data from the database
        retrieveAllData()
        return binding.root
    }

    //Retrieve all the data from the database
    private fun retrieveAllData() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("admin").child("menu")
        //Fetch data from database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Clear existing data before adding new data
                menuItems.clear()
                //Iterate through the data and add it to the menuItems list
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItemModel::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }
        })
    }

    private fun setAdapter() {
        val adapter = MenuAdapter(menuItems, requireContext())
        binding.rvMenuItem.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenuItem.adapter = adapter
    }
}