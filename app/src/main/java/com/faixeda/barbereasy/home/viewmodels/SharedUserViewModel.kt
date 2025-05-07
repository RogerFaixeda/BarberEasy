package com.faixeda.barbereasy.home.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.faixeda.barbereasy.home.models.User

class SharedUserViewModel : ViewModel() {

    // LiveData interna para el usuario completo
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    // LiveData interna para el uid
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    // Métodos para actualizar los datos
    fun setUser(user: User) {
        _user.value = user
    }

    fun setUserId(uid: String) {
        _userId.value = uid
    }
}