package android.coding.ourapp.presentation.ui

import android.annotation.SuppressLint
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.databinding.ActivityAddReportMonthBinding
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Key.Companion.ID_PARENT
import android.coding.ourapp.utils.Key.Companion.MONTH
import android.coding.ourapp.utils.Utils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReportMonthActivity : AppCompatActivity() {
    private var _binding : ActivityAddReportMonthBinding? = null
    private val binding get() = _binding!!
    private var nameStudent : String? = null
    private var nameMonth : String? = null
    private var idParent : String? = null
    private var pointAgama = 0
    private var pointMoral = 0
    private var pointPekerti = 0
    private var isFirst : Boolean = false
    private val reportViewModel by viewModels<ReportViewModel>()
    private val listNarrative : MutableList<Narrative> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddReportMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllData()

        nameStudent = intent.getStringExtra(DATA_KU)
        nameMonth = intent.getStringExtra(MONTH)
        idParent = intent.getStringExtra(ID_PARENT)

        binding.tvDetailName.text = nameStudent
        binding.tvDetailMonth.text = nameMonth
        back()

        if(idParent != null){
            binding.btnSaveReport.setOnClickListener { addNarrative(idParent!!) }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getAllData(){
        reportViewModel.getAllReport.observe(this){
            when (it) {
                is Resource.Success -> {
                   val data = it.result.filter { result ->  result.studentName == nameStudent && result.reports.any { reports -> reports.month == nameMonth }}
                   data.forEach { dataReport ->
                       listNarrative.addAll(dataReport.narratives)
                       dataReport.narratives.forEach { narrative ->
                           if(narrative.month == ""){
                               isFirst = true
                           }
                       }
                       dataReport.reports.forEach { reports ->
                           pointAgama += reports.indicatorAgama.size
                           pointMoral += reports.indicatorMoral.size
                           pointPekerti += reports.indicatorPekerti.size
                       }
                   }
                    binding.apply {
                        tvDetailPointAgama.text = " $pointAgama"
                        tvDetailPointMoral.text = " $pointMoral"
                        tvDetailPointPekerti.text = " $pointPekerti"
                        tvDetailCategoryAgama.text = Utils.category(pointAgama)
                        tvDetailKategoriMoral.text = Utils.category(pointMoral)
                        tvDetailCategoryPekerti.text = Utils.category(pointPekerti)
                    }
                }

                is Resource.Loading -> {}

                is Resource.Failure -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addNarrative(idParent : String){
        val narrative = binding.etDesc.text.toString()
        if(isFirst){
            listNarrative.clear()
            reportViewModel.deleteNarrative(idParent)
        }
        listNarrative.add(Narrative(summary = narrative, month = nameMonth!!, totalIndicatorAgama = pointAgama, totalIndicatorMoral = pointMoral, totalIndicatorPekerti = pointPekerti))

        reportViewModel.
        updateReport(
            idParent,
            idChild = "",
            indicatorAgama = mutableListOf(),
            indicatorMoral = mutableListOf(),
            indicatorPekerti = mutableListOf(),
            images = mutableListOf(),
            listReport = mutableListOf(),
            date = "",
            tittle = "",
            listNarrative = listNarrative
        )
        reportViewModel.message.observe(this){
            when(it){
                is Resource.Success -> {
                    Toast.makeText(this, "Tambah laporan bulanan berhasil", Toast.LENGTH_SHORT).show()
                    finish()
                    finish()
                }
                is Resource.Loading -> {}

                is Resource.Failure -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun back(){
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    companion object{
        const val DATA_KU = "data_ku"
    }
}