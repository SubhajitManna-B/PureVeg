package com.example.pureveg.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pureveg.databinding.FragmentProfileBinding
import com.example.pureveg.ui.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        //Initialize firebase auth
        auth = FirebaseAuth.getInstance()
        //Initialize database reference
        database = FirebaseDatabase.getInstance().reference

        //set user data
        setUserData()

        //set edit profile button
        binding.apply {
            etProfileName.isEnabled = false
            etProfilePhone.isEnabled = false
            etProfileMail.isEnabled = false
            etProfileAdderess.isEnabled = false
            btnSaveProfileInfo.isEnabled = false
            btnEditControl.setOnClickListener {
                etProfileName.isEnabled = !etProfileName.isEnabled
                etProfilePhone.isEnabled = !etProfilePhone.isEnabled
                etProfileMail.isEnabled = !etProfileMail.isEnabled
                etProfileAdderess.isEnabled = !etProfileAdderess.isEnabled
                btnSaveProfileInfo.isEnabled = !btnSaveProfileInfo.isEnabled
            }
        }

        //set save information button
        binding.btnSaveProfileInfo.setOnClickListener {
            val name = binding.etProfileName.text.toString().trim()
            val address = binding.etProfileAdderess.text.toString().trim()
            val email = binding.etProfileMail.text.toString().trim()
            val phone = binding.etProfilePhone.text.toString().trim()
            //update user data on database
            updateUserData(name, address, email, phone)
        }

        return binding.root
    }

    //Function to set user profile data on edit text field
    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if(userId != null){
            val userReference = database.child("user").child(userId).child("user_profile")

            userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userprofile = snapshot.getValue(UserModel::class.java)
                        if (userprofile != null){
                            binding.etProfileName.setText(userprofile.userName)
                            binding.etProfileAdderess.setText(userprofile.userAddress)
                            binding.etProfileMail.setText(userprofile.userEmail)
                            binding.etProfilePhone.setText(userprofile.userPhone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    //Function to update user data on database
    private fun updateUserData(name: String, address: String, email: String, phone: String) {
        val userId = auth.currentUser?.uid?:""
        val userProfile = hashMapOf("userName" to name, "userAddress" to address, "userEmail" to email, "userPhone" to phone)
        database.child("user").child(userId).child("user_profile").setValue(userProfile).addOnSuccessListener {
            Toast.makeText(requireContext(),"Information updated successfully😊",Toast.LENGTH_SHORT).show()
            //Update the email for firebase authentication
            auth.currentUser?.updateEmail(email)
        }.addOnFailureListener{
            Toast.makeText(requireContext(),"Information update failed😢",Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
    }
}