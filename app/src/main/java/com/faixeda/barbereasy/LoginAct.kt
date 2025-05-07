package com.faixeda.barbereasy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.faixeda.barbereasy.home.HomeActivity
import com.faixeda.barbereasy.databinding.ActivityLoginBinding
import com.faixeda.barbereasy.home.HomeActivityBarber
import com.faixeda.barbereasy.home.viewmodels.SharedUserViewModel
import com.faixeda.barbereasy.home.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginAct : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val auth = FirebaseAuth.getInstance()
    private val userViewModel : UserViewModel by viewModels()
    private val sharedUserViewModel : SharedUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    userViewModel.getUser().observe(this) { result ->
                        result.fold(
                            onSuccess = { user ->
                                if (user.barber){
                                    startActivity(
                                        Intent(this, HomeActivityBarber::class.java)
                                    )
                                    finish()
                                } else {
                                    startActivity(
                                        Intent(this, HomeActivity::class.java)
                                    )
                                    finish()
                                }
                            },
                            onFailure = { e ->
                                Toast.makeText(this, "Error leyendo usuario: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error autenticación: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterAct::class.java))
            finish()
        }
    }
}
