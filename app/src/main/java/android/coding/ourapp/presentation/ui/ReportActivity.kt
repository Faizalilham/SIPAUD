package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.adapter.AdapterMonthReport
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Month
import android.coding.ourapp.databinding.ActivityReportBinding
import android.coding.ourapp.presentation.ui.ReportMonthActivity.Companion.EXTRA_DATA
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Key.Companion.ID_PARENT
import android.coding.ourapp.utils.Key.Companion.MONTH
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private val listBackground : MutableList<Int> = mutableListOf(R.raw.bg_januari,
        R.raw.bg_februari,R.raw.bg_maret,R.raw.bg_april,R.raw.bg_mei,R.raw.bg_juni,R.raw.bg_juli,R.raw.bg_agustus,R.raw.bg_september, R.raw.bg_oktober,R.raw.bg_oktober,R.raw.bg_oktober)

    private var nameStudent : String? = null
    private var idParent : String = ""
    private var idStudent : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllReport()
        back()
        nameStudent = intent.getStringExtra(NAME_STUDENT)
        idStudent = intent.getStringExtra(ID_STUDENT)
        binding.tvName.text = nameStudent
        Log.d("CEK UYE","\"$idStudent \"")
    }

    private fun getAllReport(){
        reportViewModel.getAllReport.observe(this){ its ->
            when (its) {
                is Resource.Success -> {
                    val listMonthString = mutableListOf<String>()
                    val listMonth = mutableListOf<Month>()
                    if(its.result.isNotEmpty()){
                        val a = its.result.filter { its -> its.idStudent == idStudent }
                        a.forEach { reportResult ->
                            idParent = reportResult.id
                            reportResult.reports.forEach { its ->
                                listMonthString.add(its.month)
                            }
                        }
                        Log.d("CEK INTENT","\"$idParent $nameStudent \" $listMonthString")
                        if(listMonthString.distinct().size <= listBackground.size){
                            val groupedData = listMonthString.groupBy { it }
                            val countMap = groupedData.mapValues { it.value.size }
                            var counts = 0
                            countMap.forEach { (value, count) ->
                                listMonth.add(Month(value,count,listBackground[counts]))
                                counts++
                            }
                        }
                    }

                    Log.d("MONTH","$listMonth")
                    if(listMonth.isNotEmpty()){
                        binding.ivNotData.visibility = View.GONE
                        binding.rvMonth.visibility = View.VISIBLE
                        setupRecycler(listMonth)

                    }else{
                        binding.rvMonth.visibility = View.GONE
                        binding.ivNotData.visibility = View.VISIBLE
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
        adapterMonth = AdapterMonthReport(uniqueMonths.toMutableList(),this)
        adapterMonth.setItemClickListener { name ->
            startActivity(Intent(this@ReportActivity,DetailReportActivity::class.java).also{
                it.putExtra(MONTH,name)
                it.putExtra(ID_PARENT,idParent)
                it.putExtra(ID_STUDENT,idStudent)
                it.putExtra(NAME_STUDENT,nameStudent)
            })
        }

        adapterMonth.setItemClickedListener { name ->
            startActivity(Intent(this@ReportActivity,ReportMonthActivity::class.java).also{
                it.putExtra(EXTRA_DATA,nameStudent)
                it.putExtra(MONTH, name)
                it.putExtra(ID_PARENT, idParent)
                it.putExtra(ID_STUDENT, idStudent)
            })
        }

        binding.rvMonth.apply {
            adapter = adapterMonth
            layoutManager = GridLayoutManager(this@ReportActivity,2)
        }
    }

    companion object{
        const val NAME_STUDENT = "name_student"
        const val ID_STUDENT = "id_student"
    }

    private fun back(){
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}