package com.example.pureveg.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pureveg.databinding.FragmentSearchBinding
import com.example.pureveg.ui.adapter.MenuAdapter
import com.example.pureveg.ui.model.MenuItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding
    private lateinit var searchAdapter : MenuAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var originalMenuItems: MutableList<MenuItemModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        //Initialize auth and database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //Retrieve menu items from database
        retrieveData()

        //Setup search view
        setSearchView()

        return binding.root
    }

    private fun retrieveData() {
        val userId = auth.currentUser?.uid?:""
        val menuRef = database.reference.child("admin").child("menu")
        menuRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItemModel::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                //Show all the menu item from the database
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    //Function to show all the menu items
    private fun showAllMenu() {
        val unFilterMenuItem = ArrayList(originalMenuItems)

        //Set data on adapter
        setAdapter(unFilterMenuItem)
    }

    //Function for set the menu adapter for search item recycler view
    private fun setAdapter(menuItem: List<MenuItemModel>) {
        searchAdapter = MenuAdapter(menuItem, requireContext())
        binding.rvSearchMenuList.layoutManager = LinearLayoutManager(context)
        binding.rvSearchMenuList.adapter = searchAdapter
    }

    //Function to show Menu items on search
    private fun setSearchView() {
        binding.svSearchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    //Function to filter menu items on search
    private fun filterMenuItems(query: String) {
        val filteredMenuItems = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }
        //Set adapter
        setAdapter(filteredMenuItems)
    }

    companion object {

    }
}