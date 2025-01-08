package com.example.pureveg.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pureveg.R
import com.example.pureveg.databinding.ActivitySignUpBinding
import com.example.pureveg.ui.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {
    private lateinit var userName : String
    private lateinit var userEmail : String
    private lateinit var userPassword : String
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var gsc : GoogleSignInClient


    private val binding : ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Google sign-in options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        //initialize firebase auth
        auth = Firebase.auth
        //initialize firebase database
        database = Firebase.database.reference
        //Initialize google sign in client
        gsc = GoogleSignIn.getClient(this, gso)

        binding.tvLogIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnSignUp.setOnClickListener {
            userName = binding.etSignName.text.toString().trim()
            userEmail = binding.etSignEmail.text.toString().trim()
            userPassword = binding.etSignPassword.text.toString().trim()

            //Check the fields are empty or not
            if (userName.isNotEmpty() && userEmail.isNotEmpty() && userPassword.isNotEmpty()){
                createUser(userName, userEmail, userPassword)
            }else{
                Toast.makeText(this,"Please Fill all the details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignupGoogle.setOnClickListener {
            val signIntent = gsc.signInIntent
            launcher.launch(signIntent)
        }
    }

    //Create new user in database
    private fun createUser(userName: String, userEmail: String, userPassword: String) {
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener{task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Account created successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else{
                Toast.makeText(this,"Account creation failed", Toast.LENGTH_SHORT).show()
                Log.d("Account","Create Account: Failure", task.exception)
            }
        }
    }

    //Create a launcher for launch google sign in
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener{authTask ->
                    if(authTask.isSuccessful){
                        Toast.makeText(this,"Successfully sign-in with Google", Toast.LENGTH_SHORT).show()
                        updateUi(authTask.result?.user)
                    }else{
                        Toast.makeText(this,"Google sign-in failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Save user data in database
    private fun saveUserData() {
        //get text from edit text
        userName = binding.etSignName.text.toString().trim()
        userEmail = binding.etSignEmail.text.toString().trim()
        userPassword = binding.etSignPassword.text.toString().trim()
        val user = UserModel(userName, userEmail, userPassword)
        val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).child("user_profile").setValue(user)  //Save data in firebase database
    }

    //Change the activity to login activity after sign up operation
    private fun updateUi(user: FirebaseUser?){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}