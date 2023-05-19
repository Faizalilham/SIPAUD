package android.coding.ourapp.presentation.ui

import android.coding.ourapp.databinding.ActivityProfileBinding
import android.coding.ourapp.presentation.viewmodel.auth.AuthViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private var _binding : ActivityProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentUser()
        doLogout()
        moveToHome()

    }

    private fun currentUser(){
        binding.apply {
            tvEmail.text = authViewModel.currentUser?.email
            tvSchool.text = authViewModel.currentUser?.displayName
        }
    }

    private fun doLogout(){
        binding.cardLogout.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java).also{
                authViewModel.deleteToken()
                finish()
            })
            finish()
        }
    }

    private fun moveToHome(){
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}