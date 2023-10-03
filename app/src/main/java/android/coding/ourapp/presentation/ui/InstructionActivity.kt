package android.coding.ourapp.presentation.ui

import android.coding.ourapp.databinding.ActivityInstructionBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class InstructionActivity : AppCompatActivity() {
    private var _binding : ActivityInstructionBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInstructionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}