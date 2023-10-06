package android.coding.ourapp.data.datastore

import android.coding.ourapp.data.datastore.AchievementPreferences.Companion.token
import android.content.Context
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

private val Context.achievement : DataStore<Preferences> by preferencesDataStore(name = token)
class AchievementPreferences(@ApplicationContext private val context: Context) {

    private val listKey = stringSetPreferencesKey("achievementKeys")
    private val lastSavedDateKey = stringPreferencesKey("lastSavedDates")


    suspend fun saveList( list: List<String>) {
        context.achievement.edit { preferences ->
            preferences[listKey] = list.toSet()
            preferences[lastSavedDateKey] = System.currentTimeMillis().toString()
//            preferences[lastSavedDateKey] = Date().time.toString()
        }
    }

    fun getList(): Flow<List<String>> {
        return context.achievement.data.map { preferences ->
            Toast.makeText(context, " s ${preferences[listKey]} ", Toast.LENGTH_SHORT).show()
            preferences[listKey]?.toList() ?: emptyList()
        }
    }

    suspend fun deleteListIfNeeded() {
        val lastSavedDateStr = context.achievement.data.map { preferences ->
            preferences[lastSavedDateKey] ?: "0"
        }.first()

        val lastSavedDate = lastSavedDateStr.toLongOrNull() ?: 0L
        val timeDifferenceInDays = getTimeDifferenceInDays(lastSavedDate)

        if (timeDifferenceInDays >= 30) {
            context.achievement.edit { preferences ->
                preferences.remove(listKey)
                preferences.remove(lastSavedDateKey)
                preferences.clear()
            }
        }

        context.achievement.edit { preferences ->
            preferences.remove(listKey)
            preferences.remove(lastSavedDateKey)
            preferences.clear()
        }
//        if(isDataExpired()){
//            context.achievement.edit {
//                it.remove(listKey)
//                it.remove(lastSavedDateKey)
//            }
//        }
    }

    private fun getTimeDifferenceInDays(lastSavedDate: Long): Int {
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - lastSavedDate
        return (difference / (1000 * 60 * 60 * 24)).toInt()
    }


    // PERCOBAAN 5 JAM TOKEN TERISET (BERHASIL)
    suspend fun isDataExpired(): Boolean {
        val preferences = context.achievement.data.first()
        val lastAccessedTime = preferences[lastSavedDateKey]?.firstOrNull()?.toLong() ?: return true

        // Get current time
        val currentTime = Date().time

        // Calculate the difference in milliseconds
        val timeDifference = currentTime - lastAccessedTime

        // Check if it has been more than 5 hours since last access
        return timeDifference >= FIVE_HOURS_IN_MILLIS
    }

    companion object{
        const val token = "achievements"
        const val FIVE_HOURS_IN_MILLIS = 5 * 60 * 60 * 1000L // 5 hours in milliseconds
    }
}