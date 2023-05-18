package android.coding.ourapp.presentation.ui

import android.coding.ourapp.databinding.ActivityStudentsBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class StudentsActivity : AppCompatActivity() {
    private var _binding : ActivityStudentsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

//    faishal

//    SIAPAUD

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}