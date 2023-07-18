package android.coding.ourapp.data.datastore

import android.coding.ourapp.data.datastore.AchievementPreferences.Companion.token
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.achievement : DataStore<Preferences> by preferencesDataStore(name = token)
class AchievementPreferences(@ApplicationContext private val context: Context) {

    private val listKey = stringSetPreferencesKey("achievementKey")


    suspend fun saveList( list: List<String>) {
        context.achievement.edit { preferences ->
            preferences[listKey] = list.toSet()
        }
    }

    fun getList(): Flow<List<String>> {
        return context.achievement.data.map { preferences ->
            preferences[listKey]?.toList() ?: emptyList()
        }
    }

    suspend fun deleteList(){
        context.achievement.edit {
            it.clear()
        }
    }

    companion object{
        const val token = "achievement"
    }
}