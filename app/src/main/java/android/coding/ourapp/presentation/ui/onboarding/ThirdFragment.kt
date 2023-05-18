package android.coding.ourapp.presentation.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.coding.ourapp.R
import android.coding.ourapp.databinding.FragmentThirdBinding
import android.coding.ourapp.presentation.ui.LoginActivity
import android.coding.ourapp.presentation.viewmodel.OnBoardingViewModel
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ThirdFragment : Fragment() {

    private var _binding : FragmentThirdBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBoarding : OnBoardingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdBinding.inflate(layoutInflater)
        onBoarding = ViewModelProvider(requireActivity())[OnBoardingViewModel::class.java]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        finish()
        back()
    }

    private fun back(){
        val viewPager =  activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.back.setOnClickListener {
            viewPager?.currentItem = 1
        }
    }

    private fun finish(){
        binding.finish.setOnClickListener {
            startActivity(Intent(requireActivity(),LoginActivity::class.java).also{
                onBoarding.setBoardingKey(true)
                activity?.finish()
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}