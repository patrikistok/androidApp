package eu.mcomputing.mobv.zadanie.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult

    private val _changePasswordResult = MutableLiveData<Pair<String, Boolean>>()
    val changePasswordResult: LiveData<Pair<String, Boolean>> get() = _changePasswordResult

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult


    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeat_password = MutableLiveData<String>()
    val old_password = MutableLiveData<String>()
    val new_password = MutableLiveData<String>()
    val again_new_password = MutableLiveData<String>()

    fun registerUser() {
        viewModelScope.launch {
            val result = dataRepository.apiRegisterUser(
                username.value ?: "",
                email.value ?: "",
                password.value ?: "",
                repeat_password.value ?: ""
            )
            _registrationResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
        clearInputs()
    }

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(username.value ?: "", password.value ?: "")
            _loginResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
        clearInputs()
    }

    fun changePassword() {
        viewModelScope.launch {
            val result = dataRepository.apiChangePassword(old_password.value ?: "", new_password.value ?: "", again_new_password.value ?: "")
            _changePasswordResult.postValue(Pair(result.first ?: "",result.second ?: false))
        }
        clearInputs()
    }

    private fun clearInputs(){
        username.value = ""
        password.value = ""
        email.value = ""
        repeat_password.value = ""
        old_password.value = ""
        new_password.value = ""
        again_new_password.value = ""
    }

    fun logoutUser(){
        viewModelScope.launch {
            _userResult.postValue(null)
        }
        clearInputs()
    }
}