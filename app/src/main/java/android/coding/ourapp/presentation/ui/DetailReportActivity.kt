package android.coding.ourapp.presentation.ui

import ReportAdapter
import android.coding.ourapp.adapter.OnTouchHelper
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.databinding.ActivityDetailReportBinding
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        val i = intent.getStringExtra("month")
        Log.d("ID YGY","$i")
        if(i != null){
            reportViewModel.getAllReport.observe(this){
                when (it) {
                    is Resource.Success -> {
                       if(it.result.isNotEmpty()){
                           val listReport = mutableListOf<Report>()
                           it.result.forEach { its -> listReport.addAll(its.reports) }
                           setupRecycler(listReport.filter { report -> report.month == i }.toMutableList())
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

        OnTouchHelper(binding.rvReport).build()
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}