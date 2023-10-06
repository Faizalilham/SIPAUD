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

//@HiltViewModel
//class AchievementViewModel @Inject constructor(@ApplicationContext context : Context) : ViewModel() {
//
//    private val achievementPreferences = AchievementPreferences(context)
//
//    fun setAchievementKey(list : List<String>){
//        viewModelScope.launch{
//            achievementPreferences.saveList(list)
//        }
//    }
//
//    fun getAchievementKey() = achievementPreferences.getList().asLiveData()
//
//    fun deleteAchievement() {
//        viewModelScope.launch { achievementPreferences.deleteListIfNeeded() }
//    }
//
//}

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AchievementViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
    private val myListKey = "my_list_key"

    private val _myList = MutableLiveData<List<String>>()
    val myList: LiveData<List<String>> = _myList

    init {
        loadListFromSharedPreferences()
    }

    fun setAchievementKey(list: List<String>) {
        val editor = sharedPreferences.edit()
        editor.putStringSet(myListKey, list.toSet())
        editor.apply()
        _myList.value = list
    }

    private fun loadListFromSharedPreferences() {
        val savedSet = sharedPreferences.getStringSet(myListKey, setOf())
        val savedList = savedSet?.toList() ?: emptyList()
        _myList.value = savedList
    }

   fun deleteListFromSharedPreferences(){
        val editor = sharedPreferences.edit()
        editor.remove(myListKey)
        editor.apply()
        _myList.value = emptyList()
    }
}
