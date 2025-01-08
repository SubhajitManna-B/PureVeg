package com.example.pureveg.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.pureveg.R
import com.example.pureveg.databinding.FragmentHomeBinding
import com.example.pureveg.ui.adapter.MenuAdapter
import com.example.pureveg.ui.model.MenuItemModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private var popularItems : MutableList<MenuItemModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //View Menu
        binding.tvBtnViewMenu.setOnClickListener{
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }
        val adapter = MenuAdapter(popularItems, requireContext())
        binding.rvPopularFood.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPopularFood.adapter = adapter
        //Retrieve data from database and display in popular item list
        retrieveDataAndDisplay()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(
            object : ItemClickListener{
                override fun doubleClick(position: Int) {
                    Toast.makeText(requireContext(), "Selected image $position", Toast.LENGTH_SHORT).show()
                }

                override fun onItemSelected(position: Int) {
                    val itemPosition = imageList[position]
                    val itemMessage = "Selected Image $itemPosition"
                    Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    //Retrieve data from database and display in popular item list
    private fun retrieveDataAndDisplay() {
        database = FirebaseDatabase.getInstance()
        val foodReference : DatabaseReference = database.reference.child("admin").child("menu")

        //Retrieve popular items from the database
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val popularItem = foodSnapshot.getValue(MenuItemModel::class.java)
                    popularItem?.let {
                        popularItems.add(it)
                    }
                }
                //Display Random popular items
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }

        })
    }

    private fun randomPopularItems() {
        //Create a shuffled list of items
        val index = popularItems.indices.toList().shuffled()
        val numOfItemToShow = 6;
        val subsetPopularItem = index.take(numOfItemToShow).map { popularItems[it] }
        //Set popular adapter
        setAdapter(subsetPopularItem)
    }

    private fun setAdapter(subsetPopularItem: List<MenuItemModel>) {
        val adapter = MenuAdapter(subsetPopularItem, requireContext())
        binding.rvPopularFood.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPopularFood.adapter = adapter
    }
}