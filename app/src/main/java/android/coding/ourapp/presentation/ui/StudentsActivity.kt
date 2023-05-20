package android.coding.ourapp.presentation.ui

import android.coding.ourapp.adapter.StudentAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityStudentsBinding
import android.coding.ourapp.di.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

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


        firebaseHelper = FirebaseHelper()
        val studentRepository = StudentRepository(firebaseHelper)

        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, CreateUpdateStudentActivity::class.java))
        }
        studentAdapter = StudentAdapter(
            onDeleteClick = { data ->
                // Tampilkan dialog konfirmasi delete dan hapus data jika dikonfirmasi
                AlertDialog.Builder(this)
                    .setTitle("Yakin")
                    .setMessage("Apakah kamu yakin ingin menghapus data?")
                    .setPositiveButton("Hapus") { _, _ ->
                        studentViewModel.deleteData(data)
                        Toast.makeText(this, "Data Terhapus", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Tidak", null)
                    .show()
            },
        )



        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = studentAdapter

        studentViewModel.getData().observe(
            this
        ) {
            when (it) {
                is Resource.Success -> {
                    val dataList = it.result
                    studentAdapter.updateData(dataList)
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


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}