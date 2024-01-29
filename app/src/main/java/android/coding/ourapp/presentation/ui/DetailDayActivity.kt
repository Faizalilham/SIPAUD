package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.databinding.ActivityDetailDayBinding
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Key
import android.coding.ourapp.utils.Key.Companion.ID_CHILD
import android.coding.ourapp.utils.Key.Companion.ID_PARENT
import android.coding.ourapp.utils.Key.Companion.MONTH
import android.coding.ourapp.utils.Utils
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Suppress("RemoveToStringInStringTemplate", "RemoveToStringInStringTemplate",
    "RemoveToStringInStringTemplate", "RemoveToStringInStringTemplate",
    "RemoveToStringInStringTemplate", "RemoveToStringInStringTemplate", "NAME_SHADOWING",
    "DEPRECATION"
)
@AndroidEntryPoint
class DetailDayActivity : AppCompatActivity() {
    private  var _binding: ActivityDetailDayBinding? = null
    private val binding get()= _binding!!
    private var nameStudent: String? = null
    private var nameMonth: String? = null
    private var idStudnet: String? = null
    private var idStudent : String? = null
    private var idParent : String? = null
    private var listImages = arrayListOf<String>()
    private val listImageBitmap = arrayListOf<Bitmap>()
    private var idChild : String? = null
    private val listReport : MutableList<Report> = mutableListOf()
    private val reportViewModel by viewModels<ReportViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailDayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameStudent = intent.getStringExtra(EXTRA_DATA)
        nameMonth = intent.getStringExtra(MONTH)
        idStudnet = intent.getStringExtra(ID_STUDENTS)
        idParent = intent.getStringExtra(ID_PARENT)
        idChild = intent.getStringExtra(ID_CHILD)
        Log.d("AA KASIAN AA student, ", "${idStudent}")
        Log.d("AA KASIAN AA parent, ", "${idParent}")
        Log.d("AA KASIAN AA child ", "${idChild}")
        getDetailDayActivity()
        getData(idParent!!,idChild!!)
        back()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData(i :String,idChild : String){
        reportViewModel.getDataReportById(i).observe(this){ it ->
            when(it){
                is Resource.Success -> {
                    binding.apply {
                        val dataDetail = it.result.reports.filterNotNull().filter {  it.month == intent.getStringExtra("month") }
                        val dataNar = it.result.narratives
                        val dataDetailResult = dataDetail.filter { it.id == idChild }
                        Log.d("DATA DETAIL UPDATE1","$dataDetail")
                        Log.d("DATA DETAIL UPDATE2","$dataDetailResult")
                        Log.d("DATA DETAIL UPDATE3","${it.result.reports}")
                        dataDetailResult.forEach { report ->
                            val listAchievement = mutableListOf<String>()
                            listAchievement.addAll(report.indicatorMoral)
                            listAchievement.addAll(report.indicatorAgama)
                            listAchievement.addAll(report.indicatorPekerti)
                            tvDetailDate.text = report.reportDate
                            val formattedText = StringBuilder()
                            for ((index, value) in listAchievement.withIndex()) {
                                formattedText.append("${index + 1}. $value\n")
                            }

                            tvDetailActivity.text = formattedText.toString()

                            listImages.addAll(report.images)
                            for(i in listImages){
                                listImageBitmap.add(Utils.convertStringToBitmap(i))
                            }
                            if(report.images.isEmpty()){
                                tvTitlePhoto.text = "Dokumentasi : Tidak ada gambar pada catatan ini"
                            }
                            Utils.showImageReportDetail(true,listImageBitmap,null,this,this@DetailDayActivity)
                            btnEksporPdf.setOnClickListener {
                                showLoading(true)
                                if(report.images.isNotEmpty()){
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            exportPdf(nameStudent!!,etDesc.text.toString(),Utils.category(1),Utils.category(1),Utils.category(1),listOf(report),report.month ?: "")
                                        }else{
                                            Toast.makeText(this@DetailDayActivity, "Tidak mendukung untuk eksport pdf", Toast.LENGTH_SHORT).show()
                                        }
                                        showLoading(false)
                                    }
                                }else{
                                    showLoading(false)
                                    Toast.makeText(this@DetailDayActivity, "Minimal ada satu gambar untuk membuat laporan bulanan", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    Log.d("ERROR KU","${it.exception}")
                }
                else -> {
                    Log.d("ERROR","There is an error")
                }

            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.apply {
            if(isLoading )frameLoading.visibility = View.VISIBLE else frameLoading.visibility = View.GONE
        }
    }

    private fun getDetailDayActivity(){
        binding.tvDetailNameStudent.text = nameStudent
        binding.tvDetailDate.text = nameMonth

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun exportPdf(name : String, summary : String, categoryAgama : String, categoryMoral : String, categoryPekerti : String, reports: List<Report>, month : String,){
        if(Utils.checkStoragePermission(this,this)){
            val job = lifecycleScope.async(Dispatchers.Default) {
                Utils.exportToPdf("LAPORAN HARIAN",name,summary,categoryAgama,categoryMoral,categoryPekerti,reports,month, applicationContext)
            }
            job.await()
            Toast.makeText(this@DetailDayActivity, "Sukses export pdf", Toast.LENGTH_SHORT).show()
        }
    }

    private fun back() {
        binding.imageBack.setOnClickListener {
            finish()
        }
    }


    companion object {
        const val EXTRA_DATA = "extra_data"
        const val ID_STUDENTS = "id_student"
    }
}