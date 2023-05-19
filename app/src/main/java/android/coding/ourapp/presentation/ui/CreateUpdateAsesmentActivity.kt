package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.databinding.ActivityCreateUpdateAsesmentBinding
import android.coding.ourapp.databinding.ImageBottomSheetBinding
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialog

class CreateUpdateAsesmentActivity : AppCompatActivity() {
    private var _binding : ActivityCreateUpdateAsesmentBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateAsesmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvDate.text = Utils.getCurrentDate()
        bottomSheet()
        moveToHome()
    }

    private fun moveToHome(){
        binding.imageBack.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java).also{ finish() })
        }
    }

    private fun bottomSheet(){
        binding.image.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val view = ImageBottomSheetBinding.inflate(layoutInflater)
            bottomSheet.apply {
                view.apply {
                    setContentView(root)
                    show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}