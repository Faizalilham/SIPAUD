package android.coding.ourapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import android.coding.ourapp.data.datastore.AuthPreferences.Companion.token
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.token : DataStore<Preferences> by preferencesDataStore(name = token)
class AuthPreferences(@ApplicationContext private val context: Context) {

    private val tokenKey = stringPreferencesKey("tokenKey")
    private val nameKey = stringPreferencesKey("nameKey")

    suspend fun setToken(token :String,name:String){
        context.token.edit {
            it[tokenKey] = token
            it[nameKey] = name
        }
    }

    fun getToken(): Flow<String> {
        return context.token.data.map {
            it[tokenKey] ?: "undefined"
        }
    }

    fun getName(): Flow<String> {
        return context.token.data.map {
            it[nameKey] ?: ""
        }
    }

    suspend fun deleteToken(){
        context.token.edit {
            it.clear()
        }
    }

    companion object{
       const val token = "token"
    }
}