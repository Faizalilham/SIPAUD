package android.coding.ourapp.presentation.viewmodel

import android.coding.ourapp.data.datastore.OnBoardingPreferences
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(@ApplicationContext context : Context): ViewModel() {

    private val onBoardingPreferences = OnBoardingPreferences(context)

    fun setBoardingKey(key : Boolean){
        viewModelScope.launch{
            onBoardingPreferences.setToken(key)
        }
    }

    fun getBoardingKey() = onBoardingPreferences.getToken().asLiveData()
}