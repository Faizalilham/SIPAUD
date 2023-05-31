package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.adapter.AdapterMonthReport
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Month
import android.coding.ourapp.databinding.ActivityReportBinding
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportActivity : AppCompatActivity() {
    private var _binding : ActivityReportBinding? = null
    private val binding get() = _binding!!
    private val reportViewModel by viewModels<ReportViewModel>()
    private lateinit var adapterMonth : AdapterMonthReport
    private val listBackground : MutableList<Int> = mutableListOf(R.drawable.background_dashed,R.drawable.background_dashed)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllReport()
    }

    private fun getAllReport(){
        reportViewModel.getAllReport.observe(this){ its ->
            when (its) {
                is Resource.Success -> {

                    val listMonthString = mutableListOf<String>()
                    val listMonth = mutableListOf<Month>()
                    if(its.result.isNotEmpty()){
                        val a = its.result.filter { its -> its.studentName == "xxx" }
                        a.forEach { reportResult ->
                            reportResult.reports.forEach { its ->
                                listMonthString.add(its.month)
                            }
                        }

                        if(listMonthString.distinct().size <= listBackground.size){
                            val groupedData = listMonthString.groupBy { it }
                            val countMap = groupedData.mapValues { it.value.size }

                            countMap.forEach { (value, count) ->
                                listMonth.add(Month(value,count,listBackground[count-1]))
                            }

                        }
                    }

                    Log.d("MONTH","$listMonth")
                    if(listMonth.isNotEmpty()){
                        setupRecycler(listMonth)
                    }
                }

                is Resource.Loading -> {
                    setupRecycler(arrayListOf())
                }

                is Resource.Failure -> {
                    Toast.makeText(this, its.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupRecycler(data : MutableList<Month>){
        val groupedMonths = mutableMapOf<String, Month>()
        for (month in data) {
            val existingMonth = groupedMonths[month.name]
            if (existingMonth != null) {
                val updatedMonth = existingMonth.copy(count = existingMonth.count + month.count)
                groupedMonths[month.name] = updatedMonth
            } else {
                groupedMonths[month.name] = month
            }
        }
        val uniqueMonths = groupedMonths.values.toList()
        adapterMonth = AdapterMonthReport(uniqueMonths.toMutableList())
        adapterMonth.setItemClickListener { name ->
            startActivity(Intent(this@ReportActivity,DetailReportActivity::class.java).also{
                it.putExtra("month",name)
            })
        }

        binding.rvMonth.apply {
            adapter = adapterMonth
            layoutManager = GridLayoutManager(this@ReportActivity,2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}