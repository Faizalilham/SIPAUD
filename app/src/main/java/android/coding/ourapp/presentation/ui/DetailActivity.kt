package android.coding.ourapp.presentation.ui

import android.coding.ourapp.databinding.ActivityDetailBinding
import android.coding.ourapp.databinding.DetailBottomComponentBinding
import android.coding.ourapp.databinding.ImageBottomSheetBinding
import android.coding.ourapp.utils.Utils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailActivity : AppCompatActivity() {
    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheet()
    }

    private fun bottomSheet(){
        val a = DetailBottomComponentBinding.inflate(layoutInflater)
        a.share.setOnClickListener {
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