package com.example.burcycle.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    var email = MutableLiveData<String>()
    var contrasena = MutableLiveData<String>()
    var contrasenaRepetida = MutableLiveData<String>()

    fun onCreate() {

    }

}