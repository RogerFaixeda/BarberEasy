package com.faixeda.barbereasy.home.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.faixeda.barbereasy.home.models.Barber
import com.faixeda.barbereasy.home.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    fun registerUser(
        email: String,
        password: String,
        isBarber: Boolean,
        firstName: String,
        lastName: String
    ): LiveData<Result<Unit>> {
        val resultLd = MutableLiveData<Result<Unit>>()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authRes ->
                val uid = authRes.user?.uid
                if (uid == null) {
                    resultLd.value = Result.failure(Exception("UID nulo"))
                    return@addOnSuccessListener
                }
                // 1) Preparamos los objetos a guardar
                val user = User(email, isBarber, firstName, lastName)
                val userRef = db.collection("users").document(uid)

                // Si es barbero, creamos también el documento en "barberos"
                val batch = db.batch()
                batch.set(userRef, user)

                if (isBarber) {
                    val fullName = "$firstName $lastName"
                    val barber = Barber(fullName = fullName, email = email)
                    val barberRef = db.collection("barberos").document(uid)
                    batch.set(barberRef, barber)
                }

                // 2) Ejecutamos el batch
                batch.commit()
                    .addOnSuccessListener {
                        resultLd.value = Result.success(Unit)
                    }
                    .addOnFailureListener { e ->
                        resultLd.value = Result.failure(e)
                    }
            }
            .addOnFailureListener { e ->
                resultLd.value = Result.failure(e)
            }
        return resultLd
    }


    fun getUser(): LiveData<Result<User>> {
        val resultLd = MutableLiveData<Result<User>>()
        val uid = auth.currentUser?.uid
        if (uid == null) {
            resultLd.value = Result.failure(Exception("Ningún usuario logueado"))
        } else {
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject(User::class.java)
                    if (user != null) {
                        resultLd.value = Result.success(user)
                    } else {
                        resultLd.value = Result.failure(Exception("Documento de usuario vacío"))
                    }
                }
                .addOnFailureListener { e ->
                    resultLd.value = Result.failure(e)
                }
        }
        return resultLd
    }


}