package android.coding.ourapp.presentation.ui

import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityCreateUpdateStudentBinding
import android.coding.ourapp.di.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class CreateUpdateStudentActivity : AppCompatActivity() {
    private var _binding : ActivityCreateUpdateStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseHelper = FirebaseHelper()
        val studentRepository = StudentRepository(firebaseHelper)

        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel = ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)

        binding.btnSave.setOnClickListener {
            addData()
        }

    }

    private fun addData(){
        val etName = binding.etNameStudent.text.toString()
        val etTk = binding.etSchoolName.text.toString()
        val etKelompok = binding.etSchoolName.text.toString()

        if (etName.isNotEmpty() && etTk.isNotEmpty() && etKelompok.isNotEmpty()) {
            studentViewModel.addData(etName, etTk, etKelompok,
                onComplete = {
                    Toast.makeText(this, "Berhasil Tambah Data", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, StudentsActivity::class.java))
                },
                onFailure = { errorMessage ->
                    Toast.makeText(
                        this,
                        "Gagal Tambah Data ${errorMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        } else {
            Toast.makeText(this, "Isi Semua Form", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}