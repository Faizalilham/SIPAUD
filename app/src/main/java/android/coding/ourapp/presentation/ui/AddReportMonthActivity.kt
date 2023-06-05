package android.coding.ourapp.presentation.ui

import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.DataReport
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.data.repository.report_month.ReportMonthRepository
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityAddReportMonthBinding
import android.coding.ourapp.helper.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.report_month.ReportMonthViewModel
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.coding.ourapp.utils.Key.Companion.MONTH
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class AddReportMonthActivity : AppCompatActivity() {
    private var _binding : ActivityAddReportMonthBinding? = null
    private val binding get() = _binding!!
    private var nameStudent : String? = null
    private var nameMonth : String? = null
    private var point = "30"
    private var category = "Berkembang"
    private var report: DataReport? = null
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var reportMonthViewModel: ReportMonthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddReportMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        initViewModel()

        nameStudent = intent.getStringExtra(DATA_KU)
        nameMonth = intent.getStringExtra(MONTH)

        binding.btnSaveReport.setOnClickListener {
        }

        binding.tvDetailName.text = nameStudent
        binding.tvDetailMonth.text = nameMonth


    }



//    private fun initViewModel() {
//        firebaseHelper = FirebaseHelper()
//        val studentRepository = StudentRepository(firebaseHelper)
//        val monthRepository= ReportMonthRepository(firebaseHelper)
//        val viewModelFactory = ViewModelFactory(studentRepository, monthRepository)
//        reportMonthViewModel =
//            ViewModelProvider(this, viewModelFactory).get(ReportMonthViewModel::class.java)
//    }

    companion object{
        const val DATA_KU = "data_ku"
    }
}