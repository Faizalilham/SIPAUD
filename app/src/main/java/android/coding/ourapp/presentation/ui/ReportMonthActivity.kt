package android.coding.ourapp.presentation.ui

import android.coding.ourapp.databinding.ActivityReportMonthBinding
import android.coding.ourapp.presentation.ui.AddReportMonthActivity.Companion.DATA_KU
import android.coding.ourapp.utils.Key.Companion.ID_PARENT
import android.coding.ourapp.utils.Key.Companion.MONTH
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ReportMonthActivity : AppCompatActivity() {

    private var _binding: ActivityReportMonthBinding? = null
    private val binding get() = _binding!!
    private var nameStudent: String? = null
    private var nameMonth: String? = null
    private var idParent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReportMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nameStudent = intent.getStringExtra(EXTRA_DATA)
        nameMonth = intent.getStringExtra(MONTH)
        idParent = intent.getStringExtra(ID_PARENT)

        binding.tvNameee.text = nameStudent
        binding.tvMonthg.text = nameMonth
        moveToAdd()
    }

    private fun moveToAdd() {
        binding.btnAddReporttMonth.setOnClickListener {
            startActivity(Intent(this, AddReportMonthActivity::class.java).also {
                it.putExtra(DATA_KU, nameStudent)
                it.putExtra(MONTH, nameMonth)
                it.putExtra(ID_PARENT, idParent)
            })
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}