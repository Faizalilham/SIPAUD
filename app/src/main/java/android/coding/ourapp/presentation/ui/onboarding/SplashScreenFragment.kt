package android.coding.ourapp.presentation.ui.onboarding

import android.annotation.SuppressLint
import android.coding.ourapp.R
import android.coding.ourapp.databinding.FragmentSplashScreenBinding
import android.coding.ourapp.presentation.ui.LoginActivity
import android.coding.ourapp.presentation.ui.StudentsActivity
import android.coding.ourapp.presentation.viewmodel.OnBoardingViewModel
import android.coding.ourapp.presentation.viewmodel.auth.AuthViewModel
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBoarding : OnBoardingViewModel
    private lateinit var authViewModel : AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        onBoarding = ViewModelProvider(requireActivity())[OnBoardingViewModel::class.java]
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logicSplash()

    }

    private fun logicSplash(){
        onBoarding.getBoardingKey().observe(requireActivity()){
            if(it != null){
                Handler().postDelayed({
                    if(it){
                        if (isAdded && activity != null) {
                            authViewModel.getToken().observe(requireActivity()){ isLogin ->
                                if(isLogin){
                                    startActivity(Intent(requireActivity(), StudentsActivity::class.java).also {
                                        requireActivity().finish()
                                    })
                                }else{
                                    startActivity(Intent(requireActivity(),LoginActivity::class.java).also {
                                        requireActivity().finish()
                                    })
                                }
                            }
                        }

                    }else{
                        findNavController().navigate(R.id.action_splashScreenFragment_to_onBoardingFragment)
//                        startActivity(Intent(activity, SplashScreen::class.java).also { activity?.finish() })
                    }
                },2500)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}