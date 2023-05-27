package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.databinding.ActivityProfileBinding
import android.coding.ourapp.databinding.InformationBottomSheetBinding
import android.coding.ourapp.databinding.LanguageBottomSheetBinding
import android.coding.ourapp.presentation.viewmodel.auth.AuthViewModel
import android.coding.ourapp.utils.LanguageManager
import android.coding.ourapp.utils.Utils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
            showAlertLogout()
        }
    }

    private fun moveToHome(){
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
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
                val languageCode = "id"
                LanguageManager.setLanguage(this, languageCode)
                recreate()
                bottomSheet.dismiss()
            }
            view.buttonEnglish.setOnClickListener {
                val languageCode = "en"
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
    private fun showAlertLogout() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_component, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
        val alertDialog = alertDialogBuilder.create()

        val titleTextView = dialogView.findViewById<TextView>(R.id.tv_tittle)
        val messageTextView = dialogView.findViewById<TextView>(R.id.tv_subTittle)

        titleTextView.text = getString(R.string.alert_confirm)
        messageTextView.text = getString(R.string.alert_logout)

        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

        btnYes.setOnClickListener {
            authViewModel.deleteToken()
            finish()
            alertDialog.dismiss()
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}