package android.coding.ourapp.presentation.ui

import android.coding.ourapp.databinding.ActivityProfileBinding
import android.coding.ourapp.databinding.InformationBottomSheetBinding
import android.coding.ourapp.databinding.LanguageBottomSheetBinding
import android.coding.ourapp.presentation.viewmodel.auth.AuthViewModel
import android.coding.ourapp.utils.LanguageManager
import android.coding.ourapp.utils.Utils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        Utils.language(this)
        currentUser()
        doLogout()
        moveToHome()
        bottomSheetLanguage()
        bottomSheetInformation()

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
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun bottomSheetLanguage(){
        binding.cardTranslate.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val view = LanguageBottomSheetBinding.inflate(layoutInflater)
            bottomSheet.apply {
                view.apply {
                    setContentView(root)
                    show()
                }
            }
            view.buttonIndonesia.setOnClickListener {
                val languageCode = "id" // Ganti dengan kode bahasa yang sesuai
                LanguageManager.setLanguage(this, languageCode)
                recreate()
                bottomSheet.dismiss()
            }
            view.buttonEnglish.setOnClickListener {
                val languageCode = "en" // Ganti dengan kode bahasa yang sesuai
                LanguageManager.setLanguage(this, languageCode)
                recreate()
                bottomSheet.dismiss()
            }

        }
    }
    private fun bottomSheetInformation() {
        binding.cardInfo.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val view = InformationBottomSheetBinding.inflate(layoutInflater)
            bottomSheet.apply {
                view.apply {
                    setContentView(root)
                    show()
                }
            }
        }
    }
}