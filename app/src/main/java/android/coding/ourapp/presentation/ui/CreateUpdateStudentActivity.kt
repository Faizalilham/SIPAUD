package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityCreateUpdateStudentBinding
import android.coding.ourapp.helper.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class CreateUpdateStudentActivity : AppCompatActivity() {
    private var _binding: ActivityCreateUpdateStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var studentViewModel: StudentViewModel
    private var isEdit = false
    private var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        btnSave()

        student = intent.getParcelableExtra(EXTRA_STUDENT)
        if (student != null) {
            isEdit = true
        } else {
            student = Student()
        }

        val titlePage: String
        val btntitle: String

        if (isEdit) {
            titlePage = getString(R.string.update_student)
            btntitle = getString(R.string.update_student)
            if (student != null) {
                student?.let { student ->
                    binding?.etNameStudent?.setText(student.nameStudent)
                    binding?.etSchoolName?.setText(student.group)
                    binding?.etGroup?.setText(student.group)
                }
            }
        } else {
            titlePage = getString(R.string.add_student)
            btntitle = getString(R.string.add_student)
        }

        binding?.tittle?.text = titlePage
        binding?.btnSave?.text = btntitle
        binding?.btnSave?.setOnClickListener {
            val nameStudent = binding?.etNameStudent?.text.toString().trim()
            val nameSchool = binding?.etSchoolName?.text.toString().trim()
            val group = binding?.etGroup?.text.toString().trim()
            when {
                nameStudent.isEmpty() -> {
                    binding?.etNameStudent?.error = getString(R.string.hint_student)
                }
                nameSchool.isEmpty() -> {
                    binding?.etSchoolName?.error = getString(R.string.hint_school)
                }
                group.isEmpty() -> {
                    binding?.etGroup?.error = getString(R.string.hint_group)
                }
                else -> {
                    student.let { student ->
                        student?.nameStudent = nameStudent
                        student?.company = nameSchool
                        student?.group = group
                    }
                    if (isEdit) {
                        studentViewModel.updateData(student as Student)
                        Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        studentViewModel.addData(
                            nameStudent, nameSchool, group,
                            onComplete = {
                                Toast.makeText(this, "Berhasil Tambah Data", Toast.LENGTH_SHORT).show()
                                finish()
                            },
                            onFailure = { errorMessage ->
                                Toast.makeText(
                                    this,
                                    "Gagal Tambah Data ${errorMessage}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                    finish()
                }
            }
        }
    }

    private fun initViewModel() {
        firebaseHelper = FirebaseHelper()
        val studentRepository = StudentRepository(firebaseHelper)
        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)
    }

    private fun btnSave() {
        binding.btnSave.setOnClickListener {
            addData()
        }
    }

    private fun addData() {
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

    companion object {
        const val EXTRA_STUDENT = "extra_student"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }
}