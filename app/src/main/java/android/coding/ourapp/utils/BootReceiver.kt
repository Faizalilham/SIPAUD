package android.coding.ourapp.utils

import android.coding.ourapp.data.datastore.AchievementPreferences
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val achievementPreferences = AchievementPreferences(context.applicationContext)
        val lifecycleScope = CoroutineScope(Dispatchers.Main)

        lifecycleScope.launch {
            achievementPreferences.deleteListIfNeeded()
        }
    }
}
