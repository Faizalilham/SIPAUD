package android.coding.ourapp.data.datastore

import android.coding.ourapp.data.datastore.PermissionPreferences.Companion.dataStoreName
import android.coding.ourapp.data.datastore.PermissionPreferences.Companion.permissionKey
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.permission: DataStore<Preferences> by preferencesDataStore(name = dataStoreName)
class PermissionPreferences(@ApplicationContext private val context  : Context) {
    private val permissionKey = booleanPreferencesKey(Companion.permissionKey)

    suspend fun setPermission(key : Boolean){
        context.permission.edit {
            it[permissionKey] = key
        }
    }

    fun getPermission(): Flow<Boolean> {
        return context.permission.data.map {
            it[permissionKey] ?: false
        }
    }


    companion object{
        const val dataStoreName = "DataStorePreferences"
        const val permissionKey = "permissionKey"
    }
}