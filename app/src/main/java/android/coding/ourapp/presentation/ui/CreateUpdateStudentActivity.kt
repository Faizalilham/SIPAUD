package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.data.repository.report_month.ReportMonthRepository
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityCreateUpdateStudentBinding
import android.coding.ourapp.helper.ViewModelFactory
import android.coding.ourapp.presentation.ui.component.CustomAlertDialog
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.content.Context
import android.coding.ourapp.utils.Utils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider

class CreateUpdateStudentActivity : AppCompatActivity() {
    private var _binding: ActivityCreateUpdateStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var studentViewModel: StudentViewModel
    private var isEdit = false
    private var student: Student? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.language(this)
        back()
        initViewModel()

        student = intent.getParcelableExtra(EXTRA_STUDENT)
        if (student != null) {
            isEdit = true
        } else {
            student = Student()
        }

        val titlePage: String
        val btnTitle: String

        if (isEdit) {
            titlePage = getString(R.string.update_student)
            btnTitle = getString(R.string.update_student)
            if (student != null) {
                student?.let { student ->
                    binding.etNameStudent.setText(student.nameStudent)
                    binding.etSchoolName.setText(student.company)
                    binding.etGroup.setText(student.group)
                }
            }
        } else {
            titlePage = getString(R.string.add_student)
            btnTitle = getString(R.string.add_student)
        }

        binding.tittle.text = titlePage
        binding.btnSave.text = btnTitle
        binding.btnSave.setOnClickListener {
            val nameStudent = binding.etNameStudent.text.toString().trim()
            val nameSchool = binding.etSchoolName.text.toString().trim()
            val group = binding.etGroup.text.toString().trim()
            when {
                nameStudent.isEmpty() -> {
                    binding.etNameStudent.error = getString(R.string.hint_student)
                }
                nameSchool.isEmpty() -> {
                    binding.etSchoolName.error = getString(R.string.hint_school)
                }
                group.isEmpty() -> {
                    binding.etGroup.error = getString(R.string.hint_group)
                }
                else -> {
                    student?.nameStudent = nameStudent
                    student?.company = nameSchool
                    student?.group = group

                    if (isEdit) {
                        showConfirmationDialog()
                    } else {
                        studentViewModel.addData(
                            nameStudent, nameSchool, group,
                            onComplete = {
                                Toast.makeText(this, "Berhasil Tambah Data", Toast.LENGTH_SHORT)
                                    .show()
                                finish()
                            },
                            onFailure = { errorMessage ->
                                Toast.makeText(
                                    this,
                                    "Gagal Tambah Data $errorMessage",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_component, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        val titleTextView = dialogView.findViewById<TextView>(R.id.tv_tittle)
        val messageTextView = dialogView.findViewById<TextView>(R.id.tv_subTittle)

        titleTextView.text = getString(R.string.alert_confirm)
        messageTextView.text = getString(R.string.alert_update)

        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_yes)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

        btnConfirm.setOnClickListener {
            alertDialog.dismiss()
            updateStudentData()
        }

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun updateStudentData() {
        student?.let { student ->
            student.nameStudent = binding.etNameStudent.text.toString().trim()
            student.company = binding.etSchoolName.text.toString().trim()
            student.group = binding.etGroup.text.toString().trim()
        }
        studentViewModel.updateData(student as Student)
        Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun initViewModel() {
        firebaseHelper = FirebaseHelper()
        val studentRepository = StudentRepository(firebaseHelper)
        val monthRepository= ReportMonthRepository(firebaseHelper)
        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)
    }


    private fun back() {
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        alertDialog?.dismiss()
    }

    companion object {
        const val EXTRA_STUDENT = "extra_student"
    }
}