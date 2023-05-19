package android.coding.ourapp.presentation.viewmodel.auth

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datastore.AuthPreferences
import android.coding.ourapp.data.repository.auth.AuthRepositoryImpl
import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepositoryImpl: AuthRepositoryImpl,
    @ApplicationContext context : Context
):ViewModel() {

    private val authPreferences = AuthPreferences(context)

    private val _register : MutableLiveData<Resource<FirebaseUser>?> = MutableLiveData()
    val register : LiveData<Resource<FirebaseUser>?> = _register

    private val _login : MutableLiveData<Resource<FirebaseUser>?> = MutableLiveData()
    val login : LiveData<Resource<FirebaseUser>?> = _login


    fun setToken(){
        viewModelScope.launch {
            authPreferences.setToken()
        }
    }

    fun getToken () = authPreferences.getToken().asLiveData()

    fun deleteToken(){
        viewModelScope.launch {
            authPreferences.deleteToken()
        }
    }


    val currentUser: FirebaseUser?
        get() = authRepositoryImpl.currentUser

   fun doLogin(email : String,password : String) = viewModelScope.launch{
       _login.value = Resource.Loading
       val result = authRepositoryImpl.login(email, password)
       _login.value = result
   }

    fun doRegister(school: String, email: String, password: String) = viewModelScope.launch {
        _register.value = Resource.Loading
        val result = authRepositoryImpl.register(school, email, password)
        _register.value = result
    }

    fun logout() {
         authRepositoryImpl.logout()
        _register.value = null
    }



}