package android.coding.ourapp.presentation.viewmodel

import android.coding.ourapp.data.datastore.AchievementPreferences
import android.coding.ourapp.data.datastore.AuthPreferences
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
class AchievementViewModel @Inject constructor(@ApplicationContext context : Context) : ViewModel() {

    private val achievementPreferences = AchievementPreferences(context)

    fun setAchievementKey(list : List<String>){
        viewModelScope.launch{
            achievementPreferences.saveList(list)
        }
    }

    fun getAchievementKey() = achievementPreferences.getList().asLiveData()

}