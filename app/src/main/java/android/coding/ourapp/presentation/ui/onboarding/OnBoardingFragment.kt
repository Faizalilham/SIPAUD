package android.coding.ourapp.presentation.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.coding.ourapp.databinding.FragmentOnBoardingBinding
import android.coding.ourapp.presentation.ui.onboarding.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class OnBoardingFragment : Fragment() {


    private var _binding : FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
    }

    private fun setViewPager(){
        val fragmentList = arrayListOf(
            FirstFragment(),
            SecondFragment(),
            ThirdFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter
        binding.pageIndicator.attachTo(binding.viewPager)
//        TabLayoutMediator(binding.pageIndicator, binding.viewPager) { _, _ -> }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}