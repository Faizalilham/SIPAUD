import android.app.Activity
import android.app.Application
import android.coding.ourapp.data.datastore.AchievementPreferences
import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityLifecycleObserver(private val context: Context) : Application.ActivityLifecycleCallbacks {

    private val achievementPreferences: AchievementPreferences by lazy {
        AchievementPreferences(context)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        val lifecycleScope = CoroutineScope(Dispatchers.Main)
        lifecycleScope.launch {
            achievementPreferences.deleteListIfNeeded()
        }
    }
}
