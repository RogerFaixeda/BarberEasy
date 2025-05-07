package com.faixeda.barbereasy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.faixeda.barbereasy.databinding.ActivityRegisterBinding
import com.faixeda.barbereasy.home.HomeActivity
import com.faixeda.barbereasy.home.viewmodels.UserViewModel

class RegisterAct : AppCompatActivity() {
    private var isBarber = false
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding: ActivityRegisterBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_register)

        binding.btnToggleUser.setOnClickListener {
            isBarber = !isBarber
            binding.btnToggleUser.text = if (isBarber) "Barbero" else "Cliente"
        }

        binding.btnRegister.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val apellidos  = binding.etApellidos.text.toString().trim()
            val email     = binding.etEmail.text.toString().trim()
            val password  = binding.etPassword.text.toString().trim()

            if (nombre.isEmpty() || apellidos.isEmpty() ||
                email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llama al ViewModel
            userViewModel.registerUser(
                email, password, isBarber, nombre, apellidos
            ).observe(this, Observer { result ->
                result.fold(
                    onSuccess = {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    },
                    onFailure = { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                )
            })
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginAct::class.java))
            finish()
        }
    }
}
