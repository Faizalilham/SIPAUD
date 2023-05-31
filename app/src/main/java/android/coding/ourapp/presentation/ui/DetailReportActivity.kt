package android.coding.ourapp.presentation.ui

import android.coding.ourapp.adapter.ReportAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.databinding.ActivityDetailReportBinding
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailReportActivity : AppCompatActivity() {
    private var _binding : ActivityDetailReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var reportAdapter : ReportAdapter
    private val reportViewModel by viewModels<ReportViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllReport()
    }

    private fun getAllReport(){
        val i = intent.getStringExtra("id")
        if(i != null){
            reportViewModel.getDataReportById(i).observe(this){
                when (it) {
                    is Resource.Success -> {
                        if(it.result.report.isEmpty()){
                            setupRecycler(it.result.report)
                        }
                    }

                    is Resource.Loading -> {
                        setupRecycler(arrayListOf())
                    }

                    is Resource.Failure -> {
                        Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupRecycler(data : MutableList<Report>){
        reportAdapter = ReportAdapter(this, mutableListOf())
        reportAdapter.updateData(data)

        binding.rvReport.apply {
            adapter = reportAdapter
            layoutManager = LinearLayoutManager(this@DetailReportActivity)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}