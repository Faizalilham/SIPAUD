package android.coding.ourapp.presentation.ui

import android.coding.ourapp.adapter.StudentAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityStudentsBinding
import android.coding.ourapp.helper.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller

class StudentsActivity : AppCompatActivity() {
    private var _binding: ActivityStudentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setRecyler()
        getAllData()
        btnMoveToAddPage()
        deleteData()
    }

    private fun initViewModel() {
        firebaseHelper = FirebaseHelper()
        val studentRepository = StudentRepository(firebaseHelper)
        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)
    }

    private fun setRecyler(){
        studentAdapter = StudentAdapter()
        binding.rvStudents.setHasFixedSize(true)
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = studentAdapter
    }

    private fun getAllData(){
        studentViewModel.getData().observe(
            this
        ) {
            when (it) {
                is Resource.Success -> {
                    studentAdapter.setListStudent(it.result)
                    Toast.makeText(this, "Sukses Get ", Toast.LENGTH_SHORT).show()
                }
                is Resource.Failure -> {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteData(){
        studentAdapter.setOnDeleteClickListener(object : StudentAdapter.OnDeleteClickListener {
            override fun onDeleteClick(student: Student) {
                studentViewModel.deleteData(student)
            }
        })
    }

    private fun btnMoveToAddPage(){
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, CreateUpdateStudentActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}