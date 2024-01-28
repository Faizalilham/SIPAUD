package android.coding.ourapp.presentation.ui


import android.coding.ourapp.R
import android.coding.ourapp.adapter.AssessmentAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.databinding.ActivityHomeBinding
import android.coding.ourapp.databinding.BottomSheetFilterBinding
import android.coding.ourapp.presentation.viewmodel.assessment.AssessmentViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var assessmentAdapter: AssessmentAdapter
    private val assessmentViewModel by viewModels<AssessmentViewModel>()
    private var isFilterByNewest = false
    private var isFilterByOldest = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheet(arrayListOf())
//        getAllAssessment()
//        searchAssessment()

        binding.swipeRefreshLayout.setOnRefreshListener { getAllAssessment() }

    }

    private fun getAllAssessment() {
        assessmentViewModel.getAssessment.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.result.assessment != null) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.empty.visibility = View.GONE
                        binding.rvAssessment.visibility = View.VISIBLE
                        setupRecycler(it.result.assessment)
                        applyFilter(it.result.assessment)
                        bottomSheet(it.result.assessment)
                    }
                }

                is Resource.Loading -> {
                    setupRecycler(arrayListOf())
                }

                is Resource.Failure -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun searchAssessment() {
//        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                var result = false
//                if (binding.etSearch.text.toString().isNotBlank()) {
//                    assessmentViewModel.searchAssessment(binding.etSearch.text.toString())
//                        .observe(this) {
//                            when (it) {
//                                is Resource.Success -> {
//                                    if (it.result.assessment != null) {
//                                        result = true
//                                        if (it.result.assessment.size == 0 && result) {
//                                            binding.empty.visibility = View.VISIBLE
//                                            applyFilter(it.result.assessment)
//                                            binding.rvAssessment.visibility = View.GONE
//                                        } else {
//                                            binding.empty.visibility = View.GONE
//                                            binding.rvAssessment.visibility = View.VISIBLE
//                                            setupRecycler(it.result.assessment)
//                                        }
//                                    }
//                                }
//
//                                is Resource.Loading -> {
//                                    if (result) setupRecycler(arrayListOf())
//                                }
//
//                                is Resource.Failure -> {
//                                    Toast.makeText(
//                                        this,
//                                        it.exception.message.toString(),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//
//                                else -> {
//                                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        }
//
//                } else {
//                    Toast.makeText(
//                        this,
//                        resources.getString(R.string.warning_form),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                true
//            } else {
//                false
//            }
//        }
//    }


    private fun setupRecycler(data: ArrayList<AssessmentRequest>) {
        assessmentAdapter = AssessmentAdapter(data)
        assessmentAdapter.updateData(data)
        assessmentAdapter.setItemClickListener { assessment ->
            startActivity(Intent(this, DetailActivity::class.java).also {
                it.putExtra("id", assessment.id)
            })
        }
        binding.rvAssessment.apply {
            adapter = assessmentAdapter
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        }
    }

    private fun bottomSheet(data: ArrayList<AssessmentRequest>) {
        binding.btnFilter.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val view = BottomSheetFilterBinding.inflate(layoutInflater)
            bottomSheet.apply {
                view.apply {
                    val radioGroup = view.rgFilter
                    radioGroup.setOnCheckedChangeListener { _, checkedId ->
                        val selectedRadioButton = findViewById<RadioButton>(checkedId)
                        when (selectedRadioButton?.id) {
                            R.id.rb_favorite -> {
                                setupRecycler(Utils.filter(data, 3))
                            }
                            R.id.rb_news -> {
                                isFilterByNewest = true
                                isFilterByOldest = false
                                applyFilter(data)
                            }
                            R.id.rb_last -> {
                                isFilterByNewest = false
                                isFilterByOldest = true
                                applyFilter(data)
                            }
                            else -> {
                                isFilterByNewest = false
                                isFilterByOldest = false
                                setupRecycler(data)
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
    private fun applyFilter(data: List<AssessmentRequest>) {
        val filteredData = when {
            isFilterByNewest -> data.sortedByDescending { it.date }
            isFilterByOldest -> data.sortedBy { it.date }
            else -> data
        }
        setupRecycler(ArrayList(filteredData))
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Utils.language(this)
    }

}