package android.coding.ourapp.presentation.ui


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
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportMonthActivity : AppCompatActivity() {

    private var _binding: ActivityReportMonthBinding? = null
    private val binding get() = _binding!!
    private val reportViewModel by viewModels<ReportViewModel>()
    private lateinit var monthAdapter: MonthAdapter
    private var nameStudent: String? = null
    private var nameMonth: String? = null
    private var idParent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReportMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nameStudent = intent.getStringExtra(EXTRA_DATA)
        nameMonth = intent.getStringExtra(MONTH)
        idParent = intent.getStringExtra(ID_PARENT)

        binding.tvDetailNameStudent.text = nameStudent
        binding.tvDetailMonths.text = nameMonth
        moveToAdd()
        getReport()
    }


    private fun getReport() {
        val currentMonth = intent.getStringExtra(MONTH)
        if (currentMonth != null) {
            reportViewModel.getAllReport.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        val narrative = mutableListOf<Narrative>()
                        val data = it.result.filter { uye ->
                            uye.studentName == nameStudent
                        }
                        data.forEach { report ->
                            narrative.addAll(report.narratives)
                        }
                        val dataKu = narrative.filter { report ->
                            report.month == currentMonth
                        }

                        val listReport = mutableListOf<Report>()
                        val dataReport = it.result.filter { dt ->
                            dt.studentName == nameStudent
                        }
                        dataReport.forEach { its -> listReport.addAll(its.reports) }
                        val datak = listReport.filter { report ->
                            report?.month == currentMonth
                        }.toMutableList()

                        if (dataKu.isNotEmpty()) {
                            binding.tvDetailNarasi.text = dataKu[0].summary
                            binding.btnAddReporttMonth.visibility = View.GONE
                            setupRecycler(datak)
                        } else {
                            binding.emptyReport.visibility = View.VISIBLE
                            binding.tvDetailNarasi.visibility = View.GONE
                            binding.tvDetailMonths.visibility = View.GONE
                            binding.tvMonthg.visibility = View.GONE
                            binding.tvNameee.visibility = View.GONE
                            binding.tvDetailNameStudent.visibility = View.GONE
                            binding.tvDetailCategory.visibility = View.GONE
                            binding.leftBox.visibility = View.GONE
                            binding.leftBox1.visibility = View.GONE
                        }
                    }
                    is Resource.Loading -> {

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

    private fun exportPdf(bitmaps: List<Bitmap>, texts: List<String>){
        binding.btnAddReporttMonth?.setOnClickListener {
            if(Utils.checkStoragePermission(this,this)){
                Utils.exportToPdf(bitmaps,texts,this)
                Toast.makeText(this, "Sukses export pdf", Toast.LENGTH_SHORT).show()
            }
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

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_SUMMARY = "extra_summary"
    }
}