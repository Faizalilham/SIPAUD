package android.coding.ourapp.presentation.ui

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.Chart
import android.coding.ourapp.data.datasource.model.DataReport
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityDashboardBinding
import android.coding.ourapp.helper.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.time.Month
import java.util.*

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private var _binding : ActivityDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseHelper: FirebaseHelper

    private val reportViewModel by viewModels<ReportViewModel>()
    private lateinit var studentViewModel : StudentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            imageProfile.setOnClickListener { startActivity(Intent(this@DashboardActivity, ProfileActivity::class.java)) }
            cardAmount.setOnClickListener { startActivity(Intent(this@DashboardActivity, StudentsActivity::class.java)) }
            cardAddStudent.setOnClickListener { startActivity(Intent(this@DashboardActivity, CreateUpdateStudentActivity::class.java)) }
        }

        initViewModel()
        getAllData()
        getStudentCount()
    }

    private fun initViewModel() {
        firebaseHelper = FirebaseHelper()
        val studentRepository = StudentRepository(firebaseHelper)
        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory)[StudentViewModel::class.java]
    }

    private fun getStudentCount(){
        studentViewModel.getData().observe(this){
            when(it){
                is Resource.Success -> {
                    binding.tvAmountStudent.text = "${it.result.size} Siswa"
                }
                is Resource.Failure -> {

                }

                is Resource.Loading -> {

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showChart(data: List<Chart>) {
        val descriptions = Description()
        descriptions.text = "Student Report"
        descriptions.setPosition(150f, 15f)
        binding.lineChart.apply {
            description = descriptions
            axisRight.setDrawLabels(false)
        }

        val xAxis = binding.lineChart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(data.map { it.month })
            labelCount = data.size
            granularity = 1f
        }

        val yAxis = binding.lineChart.axisLeft
        yAxis.apply {
            axisMinimum = 0f
            axisMaximum = 100f
            axisLineWidth = 2f
            axisLineColor = Color.BLACK
            labelCount = 20
        }

        val entriesList = mutableListOf<Entry>()
        val dataSetList = mutableListOf<LineDataSet>()


        data.forEach { chart ->
            val entries = Entry(Month.valueOf(chart.month).value.toFloat(), chart.average.toFloat())
            entriesList.add(entries)

            val dataSet = LineDataSet(entriesList, chart.name)
            dataSet.color = getRandomColor()
            dataSetList.add(dataSet)

        }

        dataSetList.forEachIndexed { index, _ ->
            val lineData = LineData(dataSetList[index])

            binding.lineChart.data = lineData
            binding.lineChart.invalidate()
        }


    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    private fun getAllData(){
        reportViewModel.getAllReport.observe(this){ data ->
           when(data){
               is Resource.Success -> {
                   val result = calculateAverageTotalIndicators(data.result)
                   showChart(result)
               }

               is Resource.Failure -> {

               }

               is Resource.Loading -> {

               }
           }
        }
    }

    private fun calculateAverageTotalIndicators(dataReports: List<DataReport>): List<Chart> {
        val result = mutableListOf<Chart>()

        val groupedByMonth = dataReports.flatMap { it.narratives }
            .groupBy { it.month }

        for ((month, narratives) in groupedByMonth) {
            val averageAgama = narratives.map { it.totalIndicatorAgama }.average()
            val averageMoral = narratives.map { it.totalIndicatorMoral }.average()
            val averagePekerti = narratives.map { it.totalIndicatorPekerti }.average()

            val overallAverage = (averageAgama + averageMoral + averagePekerti) / 3.0

            dataReports.firstOrNull()?.let {
                result.add(Chart(it.studentName, month ?: "Unknown", overallAverage))
            }
        }

        return result
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}