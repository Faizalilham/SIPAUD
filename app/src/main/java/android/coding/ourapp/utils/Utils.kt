package android.coding.ourapp.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate():String{
        val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
        val current = LocalDateTime.now().format(formatter)
        return current.toString()
    }

    fun language(context: Context){
        val languageCode = LanguageManager.loadLanguagePreference(context)
        LanguageManager.setLanguage(context, languageCode)
    }
}
object LanguageManager {
    private const val PREFERENCE_LANGUAGE = "language"
    private const val DEFAULT_LANGUAGE = "id"

    fun setLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        saveLanguagePreference(context, languageCode)
    }

    fun loadLanguagePreference(context: Context): String {
        val preferences = context.getSharedPreferences("BaseApplication", Context.MODE_PRIVATE)
        return preferences.getString(PREFERENCE_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val preferences = context.getSharedPreferences("BaseApplication", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(PREFERENCE_LANGUAGE, languageCode)
        editor.apply()
    }
}