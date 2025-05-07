 package com.faixeda.barbereasy.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.ActivityHomeBinding
import com.faixeda.barbereasy.home.models.User
import com.faixeda.barbereasy.home.viewmodels.SharedUserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

 class HomeActivity : AppCompatActivity() {
     private lateinit var navController: NavController
     private val sharedUserVm: SharedUserViewModel by viewModels()

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation) as NavHostFragment
        navController = navHostFragment.navController

        binding.btnGoHome.setOnClickListener {
            navController.navigate(R.id.select_ciudad)
        }

         binding.btnGoProfile.setOnClickListener {
            navController.navigate(R.id.perfil)
        }

         val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
         FirebaseFirestore.getInstance().collection("users")
             .document(uid)
             .get()
             .addOnSuccessListener { doc ->
                 val user = doc.toObject(User::class.java)
                 if (user != null) {
                     sharedUserVm.setUserId(uid)
                     sharedUserVm.setUser(user)
                 }
             }
    }
}