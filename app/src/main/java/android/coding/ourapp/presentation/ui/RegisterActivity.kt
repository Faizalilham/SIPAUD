package android.coding.ourapp.presentation.ui

import android.coding.ourapp.data.Resource
import android.coding.ourapp.databinding.ActivityRegisterBinding
import android.coding.ourapp.presentation.viewmodel.auth.AuthViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private var _binding : ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.language(this)
        doRegister()
        moveToLogin()

    }

    private fun doRegister(){
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val school = binding.etSchool.text.toString().trim()

            authViewModel.doRegister(school,email,password)
            authViewModel.register.observe(this){
                when(it){
                    is Resource.Loading -> {
                        showLoading(true)

                    }
                    is Resource.Failure -> {
                        showLoading(false)
                        Toast.makeText(this, it.exception.localizedMessage?.toString(), Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Success -> {
                        showLoading(false)
                        Toast.makeText(this, "Register Successfully, please check email to verified", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "There is an error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun moveToLogin(){
        binding.tvLogin.setOnClickListener {
          finish()
        }
    }

    private fun showLoading(isLoading : Boolean){
        if(isLoading){
            binding.apply { 
                loadingBg.visibility = View.VISIBLE
                loading.visibility = View.VISIBLE
            }
        }else{
            binding.apply {
                loadingBg.visibility = View.GONE
                loading.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}