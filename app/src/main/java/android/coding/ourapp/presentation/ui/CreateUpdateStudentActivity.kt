package android.coding.ourapp.presentation.ui

import android.coding.ourapp.databinding.ActivityCreateUpdateStudentBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CreateUpdateStudentActivity : AppCompatActivity() {
    private var _binding : ActivityCreateUpdateStudentBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}