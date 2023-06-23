package android.coding.ourapp.presentation.ui

import android.coding.ourapp.data.Resource
import android.coding.ourapp.databinding.ActivityLoginBinding
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
class LoginActivity : AppCompatActivity() {
    private  var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.language(this)
        doLogin()
        moveToRegister()

    }

    private fun doLogin(){
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            Utils.hideKeyboard(binding.btnLogin)

            authViewModel.doLogin(email,password)
            authViewModel.login.observe(this){
                when(it){
                    is Resource.Failure -> {
                        showLoading(false)
                        Toast.makeText(this, it.exception.localizedMessage?.toString(), Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Loading -> {
                        showLoading(true)
                    }

                    is Resource.Success -> {
                        showLoading(false)
                        Toast.makeText(this, "Selamat datang ${it.result.displayName}", Toast.LENGTH_SHORT).show()
                        authViewModel.setToken()
                        startActivity(Intent(this,StudentsActivity::class.java))
                        finish()
                    }

                    else -> {
                        Toast.makeText(this, "There is an error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }
        finish()
    }

    private fun moveToRegister(){
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
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