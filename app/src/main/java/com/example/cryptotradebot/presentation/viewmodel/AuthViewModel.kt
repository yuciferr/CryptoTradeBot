package com.example.cryptotradebot.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cryptotradebot.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    fun onApiKeyChange(value: String) {
        _state.value = _state.value.copy(apiKey = value)
    }

    fun onApiSecretChange(value: String) {
        _state.value = _state.value.copy(apiSecret = value)
    }

    fun getApiKey(): String {
        return if (_state.value.apiKey.isBlank()) Constants.API_KEY
        else _state.value.apiKey
    }

    fun getApiSecret(): String {
        return if (_state.value.apiSecret.isBlank()) Constants.API_SECRET
        else _state.value.apiSecret
    }

    data class AuthState(
        val apiKey: String = "",
        val apiSecret: String = ""
    )
} 