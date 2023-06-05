package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.adapter.StudentAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.data.repository.report_month.ReportMonthRepository
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityStudentsBinding
import android.coding.ourapp.helper.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        Utils.language(this)

        initViewModel()
        setRecyclerView()
        getAllData()
        moveToAddStudent()
        moveToProfile()
        binding.rvStudents.adapter = studentAdapter
    }

    private fun initViewModel() {
        firebaseHelper = FirebaseHelper()
        val monthRepository = ReportMonthRepository(firebaseHelper)
        val studentRepository = StudentRepository(firebaseHelper)
        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)
    }

    private fun setRecyclerView() {
        studentAdapter = StudentAdapter(studentViewModel)
        binding.rvStudents.setHasFixedSize(true)
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = studentAdapter
    }

    private fun moveToAddStudent(){
        binding.btnAddStudnet.setOnClickListener {
            startActivity(Intent(this, CreateUpdateStudentActivity::class.java))
        }
    }

    private fun moveToProfile(){
        binding.imageProfile.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun getAllData() {
        studentViewModel.getData().observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    val students = result.result
                    studentAdapter.setListStudent(students)
//                    Toast.makeText(this, "Sukses Get", Toast.LENGTH_SHORT).show()
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

        private fun showAlertDelete(selectedItems: Set<Int>) {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_component, null)
            val alertDialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
            val alertDialog = alertDialogBuilder.create()

            val titleTextView = dialogView.findViewById<TextView>(R.id.tv_tittle)
            val messageTextView = dialogView.findViewById<TextView>(R.id.tv_subTittle)

            titleTextView.text = getString(R.string.alert_confirm)
            messageTextView.text = getString(R.string.alert_delete)

            val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

//            btnYes.setOnClickListener {
//                deleteSelectedItems(selectedItems)
//                alertDialog.dismiss()
//            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

//        private fun deleteSelectedItems(selectedItems: Set<Int>) {
//            val students = studentAdapter.getListStudent()
//            val studentsToDelete = selectedItems.mapNotNull { position ->
//                if (position >= 0 && position < students.size) {
//                    students[position]
//                } else {
//                    null
//                }
//            }
//            for (student in studentsToDelete) {
//                studentViewModel.deleteData(student)
//            }
//        }

//        override fun onDeleteClick(student: Student) {
//            studentViewModel.deleteData(student)
//        }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }
}

