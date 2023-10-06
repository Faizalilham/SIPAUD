package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.adapter.InstructionAdapter
import android.coding.ourapp.databinding.ActivityInstructionBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

class InstructionActivity : AppCompatActivity() {
    private var _binding : ActivityInstructionBinding? = null
    private val binding get() = _binding!!

    private lateinit var  instructionAdapter : InstructionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInstructionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val data = resources.getStringArray(R.array.instruction)
        setupRecycler(data)
        binding.imageBack.setOnClickListener { finish() }
    }


    private fun setupRecycler(data: Array<String>){
        instructionAdapter = InstructionAdapter(data)
        binding.rvInstruction.apply {
            adapter = instructionAdapter
            layoutManager = LinearLayoutManager(this@InstructionActivity)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}