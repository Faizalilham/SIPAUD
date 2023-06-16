package android.coding.ourapp.presentation.ui

import ReportAdapter
import android.coding.ourapp.adapter.AdapterMonthReport
import android.coding.ourapp.adapter.OnTouchHelper
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.databinding.ActivityDetailReportBinding
import android.coding.ourapp.presentation.ui.ReportActivity.Companion.ID_STUDENT
import android.coding.ourapp.presentation.ui.ReportActivity.Companion.NAME_STUDENT
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Key.Companion.ID_CHILD
import android.coding.ourapp.utils.Key.Companion.ID_PARENT
import android.coding.ourapp.utils.Key.Companion.MONTH
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailReportActivity : AppCompatActivity() {
    private var _binding: ActivityDetailReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var adapterMonthReport: AdapterMonthReport
    private val reportViewModel by viewModels<ReportViewModel>()
    private var idParent: String? = null
    private var idChild: String? = null
    private var nameStudent: String? = null
    private var idStudent: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllReport()
        back()
        idParent = intent.getStringExtra(ID_PARENT)
        idChild = intent.getStringExtra(ID_CHILD)
        nameStudent = intent.getStringExtra(NAME_STUDENT)
        idStudent = intent.getStringExtra(ID_STUDENT)
        val month = intent.getStringExtra(MONTH)
        binding.pageTittle.text = month
        binding.tvName.text = nameStudent
        Log.d("CEK INTENT", "\"$idParent $idChild $nameStudent \"")
    }


    private fun getAllReport() {
        val i = intent.getStringExtra(MONTH)
        if (i != null) {
            reportViewModel.getAllReport.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (it.result.isNotEmpty()) {
                            val listReport = mutableListOf<Report>()
                            val dataReport = it.result.filter { dt ->
                                dt.idStudent == idStudent
                            }
                            dataReport.forEach { its -> listReport.addAll(its.reports) }
                            val data = listReport.filter { report ->
                                report?.month == i
                            }.toMutableList()

                            if (data.isNotEmpty()) {
                                binding.tvNotFound.visibility = View.GONE
                                binding.rvReport.visibility = View.VISIBLE
                                setupRecycler(data)
                            } else {
                                binding.rvReport.visibility = View.GONE
                                binding.tvNotFound.visibility = View.VISIBLE
                            }

                        }
                    }

                    is Resource.Loading -> {
                        setupRecycler(arrayListOf())
                    }

                    is Resource.Failure -> {
                        Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupRecycler(data: MutableList<Report>) {
        reportAdapter = ReportAdapter(this, mutableListOf(), object : ReportAdapter.OnClick {
            override fun onDelete(id: String) {
                reportViewModel.deleteAssessment(idParent!!, id)
                reportViewModel.message.observe(this@DetailReportActivity) {
                    when (it) {
                        is Resource.Success -> {
                            Toast.makeText(
                                this@DetailReportActivity,
                                "Hapus data berhasil",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                        is Resource.Loading -> {}

                        is Resource.Failure -> {
                            Toast.makeText(
                                this@DetailReportActivity,
                                it.exception.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Toast.makeText(this@DetailReportActivity, "", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onUpdate(id: String) {
                startActivity(
                    Intent(
                        this@DetailReportActivity,
                        CreateUpdateReportActivity::class.java
                    ).also {
                        it.putExtra(ID_PARENT, idParent)
                        it.putExtra(ID_CHILD, id)
                        it.putExtra(NAME_STUDENT, nameStudent)
                        it.putExtra(MONTH, intent.getStringExtra(MONTH))
                    })
            }

        })
        reportAdapter.updateData(data)

        binding.rvReport.apply {
            adapter = reportAdapter
            layoutManager = LinearLayoutManager(this@DetailReportActivity)
        }

        OnTouchHelper(binding.rvReport).build()
    }

    private fun back() {
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}