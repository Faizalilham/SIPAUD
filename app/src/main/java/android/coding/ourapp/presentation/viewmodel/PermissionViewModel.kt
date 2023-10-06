package android.coding.ourapp.presentation.viewmodel

import android.coding.ourapp.data.datastore.PermissionPreferences
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(@ApplicationContext context : Context): ViewModel() {

    private val permissionPreferences = PermissionPreferences(context)

    fun setPermissionKey(key : Boolean){
        viewModelScope.launch{
            permissionPreferences.setPermission(key)
        }
    }

    fun getPermissionKey() = permissionPreferences.getPermission().asLiveData()
}