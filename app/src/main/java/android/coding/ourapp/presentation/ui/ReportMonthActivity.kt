package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.adapter.MonthAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.databinding.ActivityReportMonthBinding
import android.coding.ourapp.presentation.ui.AddReportMonthActivity.Companion.DATA_KU
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Key.Companion.ID_PARENT
import android.coding.ourapp.utils.Key.Companion.MONTH
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class ReportMonthActivity : AppCompatActivity() {
    private var _binding: ActivityReportMonthBinding? = null
    private val binding get() = _binding!!
    private val reportViewModel by viewModels<ReportViewModel>()
    private lateinit var monthAdapter: MonthAdapter
    private var nameStudent: String? = null
    private var nameMonth: String? = null
    private var idParent: String? = null
    private var idStudent: String? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReportMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(true)
        nameStudent = intent.getStringExtra(EXTRA_DATA)
        nameMonth = intent.getStringExtra(MONTH)
        idParent = intent.getStringExtra(ID_PARENT)
        idStudent = intent.getStringExtra(ID_STUDENT)
        Utils.language(this)
        binding.tvDetailNameStudent.text = nameStudent
        binding.tvMonth.text = nameMonth
        moveToAdd()
        back()
        getReport()
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun getReport() {
        val currentMonth = intent.getStringExtra(MONTH)
        if (currentMonth != null) {
            reportViewModel.getAllReport.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        showLoading(false)
                        val narrative = mutableListOf<Narrative>()
                        val data = it.result.filter { uye ->
                            uye.idStudent == idStudent
                        }
                        data.forEach { report ->
                            narrative.addAll(report.narratives)
                        }
                        val dataKu = narrative.filter { report ->
                            report.month == currentMonth
                        }
                        val listReport = mutableListOf<Report>()
                        val dataReport = it.result.filter { dt ->
                            dt.idStudent == idStudent
                        }
                        dataReport.forEach { its -> listReport.addAll(its.reports) }
                        val datak = listReport.filter { report ->
                            report.month == currentMonth
                        }.toMutableList()

                        if (dataKu.isNotEmpty()) {
                            binding.apply {
                                tvDetailNarasi.text = dataKu[0].summary
                                tvDetailPointAgama.text = dataKu[0].totalIndicatorAgama.toString()
                                tvDetailPointMoral.text = dataKu[0].totalIndicatorMoral.toString()
                                tvDetailPointPekerti.text = dataKu[0].totalIndicatorPekerti.toString()
                                tvDetailCategoryAgama.text = Utils.category(dataKu[0].totalIndicatorAgama)
                                tvDetailKategoriMoral.text = Utils.category(dataKu[0].totalIndicatorMoral)
                                tvDetailKategoriPekerti.text = Utils.category(dataKu[0].totalIndicatorPekerti)
                                btnAddReporttMonth.text = getString(R.string.ekspor_pdf)
                            }

                            binding.btnAddReporttMonth.setOnClickListener {
                                showLoading(true)
                                lifecycleScope.launch(Dispatchers.Main) {
                                    exportPdf(nameStudent!!,dataKu[0].summary,Utils.category(dataKu[0].totalIndicatorAgama),Utils.category(dataKu[0].totalIndicatorMoral),Utils.category(dataKu[0].totalIndicatorPekerti),datak,dataKu[0].month ?: "")
                                    showLoading(false)
                                }
                            }
                            setupRecycler(datak)
                            showLoading(false)
                           binding.apply {
                               emptyReport.visibility = View.GONE
                               tvDetailNarasi.visibility = View.VISIBLE
                               tvDetailPointMoral.visibility = View.VISIBLE
                               tvDetailPointPekerti.visibility = View.VISIBLE
                               tvMonthg.visibility = View.VISIBLE
                               tvNameee.visibility = View.VISIBLE
                               tvDetailNameStudent.visibility = View.VISIBLE
                               tvDetailPointAgama.visibility = View.VISIBLE
                               tvDetailCategoryAgama.visibility = View.VISIBLE
                               tvDetailKategoriMoral.visibility = View.VISIBLE
                               tvDetailKategoriPekerti.visibility = View.VISIBLE
                               leftBox.visibility = View.VISIBLE
                               leftBox1.visibility = View.VISIBLE
                               leftBox2.visibility = View.VISIBLE
                               loadings.cancelAnimation()
                           }
                        } else {
                           binding.apply{
                               emptyReport.visibility = View.VISIBLE
                               tvDetailNarasi.visibility = View.GONE
                               tvDetailPointMoral.visibility = View.GONE
                               tvDetailPointPekerti.visibility = View.GONE
                               tvMonthg.visibility = View.GONE
                               tvNameee.visibility = View.GONE
                               tvDetailNameStudent.visibility = View.GONE
                               tvDetailPointAgama.visibility = View.GONE
                               tvDetailCategoryAgama.visibility = View.GONE
                               tvDetailKategoriMoral.visibility = View.GONE
                               tvDetailKategoriPekerti.visibility = View.GONE
                               leftBox.visibility = View.GONE
                               leftBox1.visibility = View.GONE
                               leftBox2.visibility = View.GONE
                           }
                        }
                    }
                    is Resource.Loading -> {
                        binding.loadings.playAnimation()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(
                            this,
                            it.exception.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupRecycler(data: MutableList<Report>) {
        monthAdapter = MonthAdapter(this, data)
        binding.rvMonthhh.apply {
            adapter = monthAdapter
            layoutManager = LinearLayoutManager(this@ReportMonthActivity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun exportPdf(name : String, summary : String, categoryAgama : String, categoryMoral : String, categoryPekerti : String, reports: List<Report>, month : String,){
        if(Utils.checkStoragePermission(this,this)){
            val job = lifecycleScope.async(Dispatchers.Default) {
                Utils.exportToPdf(name,summary,categoryAgama,categoryMoral,categoryPekerti,reports,month, applicationContext)
            }
            job.await()
            Toast.makeText(this@ReportMonthActivity, "Sukses export pdf", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.apply {
            if(isLoading )frameLoading.visibility = View.VISIBLE else frameLoading.visibility = View.GONE
        }
    }

    private fun moveToAdd() {
        binding.btnAddReporttMonth.setOnClickListener {
            startActivity(Intent(this, AddReportMonthActivity::class.java).also {
                it.putExtra(DATA_KU, nameStudent)
                it.putExtra(MONTH, nameMonth)
                it.putExtra(ID_PARENT, idParent)
            })
        }
    }

    private fun back(){
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val ID_STUDENT = "id_student"
    }
}
