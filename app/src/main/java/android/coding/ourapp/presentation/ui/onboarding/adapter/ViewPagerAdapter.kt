package android.coding.ourapp.presentation.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(data : ArrayList<Fragment>, fm : FragmentManager, lifecycle: Lifecycle):  FragmentStateAdapter(fm,lifecycle) {
    val listData = data
    override fun getItemCount(): Int  = listData.size

    override fun createFragment(position: Int): Fragment = listData[position]
}