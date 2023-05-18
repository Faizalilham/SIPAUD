package android.coding.ourapp.presentation.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.coding.ourapp.R
import android.coding.ourapp.databinding.FragmentSplashScreenBinding
import android.coding.ourapp.presentation.ui.HomeActivity
import android.coding.ourapp.presentation.ui.LoginActivity
import android.coding.ourapp.presentation.viewmodel.OnBoardingViewModel
import android.coding.ourapp.presentation.viewmodel.auth.AuthViewModel
import android.content.Intent
import android.os.Handler
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBoarding : OnBoardingViewModel
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        onBoarding = ViewModelProvider(requireActivity())[OnBoardingViewModel::class.java]
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
                       authViewModel.getToken().observe(requireActivity()){ isLogin ->
                           if(isLogin){
                               startActivity(Intent(requireActivity(), HomeActivity::class.java).also {
                                   requireActivity().finish()
                               })
                           }else{
                               if (isAdded && activity != null) {
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
                },2000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}