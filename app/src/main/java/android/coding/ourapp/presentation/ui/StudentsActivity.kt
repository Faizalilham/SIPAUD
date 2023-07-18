package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.adapter.StudentAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityStudentsBinding
import android.coding.ourapp.databinding.BottomSheetFilterBinding
import android.coding.ourapp.helper.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.AchievementViewModel
import android.coding.ourapp.presentation.viewmodel.assessment.AssessmentViewModel
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog


class StudentsActivity : AppCompatActivity() {
    private var _binding: ActivityStudentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var studentViewModel: StudentViewModel
    private var originalStudents: List<Student> = listOf()
    private var isFilterByNameAscending = false
    private var isFilterByNameDescending = false

    private val achievementViewModel by viewModels<AchievementViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.language(this)

        initViewModel()
        getAllData()
        moveToAddStudent()
        moveToProfile()
        search()
        bottomSheet()
        binding.swipeRefreshLayout.setOnRefreshListener { getAllData() }
        setRecyclerView()
        binding.rvStudents.adapter = studentAdapter
    }

    private fun initViewModel() {
        firebaseHelper = FirebaseHelper()
        val studentRepository = StudentRepository(firebaseHelper)
        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory)[StudentViewModel::class.java]
    }

    private fun startShimmer() {
        binding.loadingStudent.startShimmer()
    }

    private fun stopShimmer() {
        binding.loadingStudent.stopShimmer()
        binding.loadingStudent.visibility = View.GONE
    }

    private fun setRecyclerView() {
        studentAdapter = StudentAdapter(this,studentViewModel,achievementViewModel)
        binding.rvStudents.setHasFixedSize(true)
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = studentAdapter
    }

    private fun moveToAddStudent() {
        binding.btnAddStudnet.setOnClickListener {
            startActivity(Intent(this, CreateUpdateStudentActivity::class.java))
        }
    }

    private fun moveToProfile() {
        binding.imageProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun getAllData() {
        studentViewModel.getData().observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    val students = result.result
                    originalStudents = students
                    binding.ivNotResult.visibility = View.GONE
                    binding.rvStudents.visibility = View.VISIBLE
                    studentAdapter.setListStudent(students)
                    binding.swipeRefreshLayout.isRefreshing = false
                    stopShimmer()
                }
                is Resource.Loading -> {
                    startShimmer()
                }
                is Resource.Failure -> {
                    Toast.makeText(this, "There is an error", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "There is an error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun search() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var result = false
                if (binding.etSearch.text.toString().isNotBlank()) {
                    studentViewModel.searchData(binding.etSearch.text.toString())
                        .observe(this) {
                            when (it) {
                                is Resource.Success -> {
                                    if (it.result.isEmpty()) {
                                        binding.ivNotResult.visibility = View.VISIBLE
                                        binding.rvStudents.visibility = View.GONE
                                    } else {
                                        result = true
                                        binding.ivNotResult.visibility = View.GONE
                                        binding.rvStudents.visibility = View.VISIBLE
                                        studentAdapter.setListStudent(it.result)
                                    }
                                }

                                is Resource.Loading -> {
                                    if (result) studentAdapter.setListStudent(arrayListOf())
                                }

                                is Resource.Failure -> {
                                    Toast.makeText(
                                        this,
                                        it.exception.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.warning_form),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            } else {
                false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun bottomSheet() {
        binding.btnFilter.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val view = BottomSheetFilterBinding.inflate(layoutInflater)
            bottomSheet.apply {
                view.apply {
                    val radioGroup = view.rgFilter
                    radioGroup.setOnCheckedChangeListener { _, checkedId ->
                        val selectedRadioButton = findViewById<RadioButton>(checkedId)
                        when (selectedRadioButton?.id) {
                            R.id.rb_name_asc -> {
                                isFilterByNameAscending = true
                                isFilterByNameDescending = false
                                applyFilter()
                            }
                            R.id.rb_name_desc -> {
                                isFilterByNameAscending = false
                                isFilterByNameDescending = true
                                applyFilter()
                            }
                            else -> {
                                isFilterByNameAscending = false
                                isFilterByNameDescending = false
                                setRecyclerView()
                            }
                        }
                        bottomSheet.dismiss()
                    }
                    setContentView(root)
                    show()
                }
            }
        }
    }

    private fun applyFilter() {
        val filteredData = when {
            isFilterByNameAscending -> originalStudents.sortedBy { it.nameStudent }
            isFilterByNameDescending -> originalStudents.sortedByDescending { it.nameStudent }
            else -> originalStudents
        }
        studentAdapter.setListStudent(filteredData)
    }
}

