package android.coding.ourapp


import android.coding.ourapp.databinding.ActivityMainBinding
import android.coding.ourapp.utils.Utils.language
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        language(this)

    }

    override fun onResume() {
        super.onResume()
        language(this)
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}