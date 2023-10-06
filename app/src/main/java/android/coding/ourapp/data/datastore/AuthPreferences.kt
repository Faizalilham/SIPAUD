package android.coding.ourapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import android.coding.ourapp.data.datastore.AuthPreferences.Companion.token
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.token : DataStore<Preferences> by preferencesDataStore(name = token)
class AuthPreferences(@ApplicationContext private val context: Context) {

    private val tokenKey = booleanPreferencesKey("tokenKey")

    suspend fun setToken(){
        context.token.edit {
            it[tokenKey] = true
        }
    }

    fun getToken(): Flow<Boolean> {
        return context.token.data.map {
            it[tokenKey] ?: false
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