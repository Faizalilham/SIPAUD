package android.coding.ourapp.presentation.ui

import android.app.Activity
import android.coding.ourapp.R
import android.coding.ourapp.adapter.AchievementActivityAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.databinding.ActivityCreateUpdateReportBinding
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.leo.searchablespinner.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateUpdateReportActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var _binding : ActivityCreateUpdateReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchableSpinnerFrom : SearchableSpinner
    private lateinit var adapterAchievementActivityAdapter: AchievementActivityAdapter
    private val listAchievementActivity = arrayListOf<String>()
    private val listAchievement = arrayListOf<String>("Menulis","Menggambar")
    private val listImages = arrayListOf<String>()
    private val listImageBitmap = arrayListOf<Bitmap>()
    private val reportViewModel by viewModels<ReportViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchableSpinnerFrom = SearchableSpinner(this)
        chooseDate()
        selectSpinner()
        setupDropDown()
        createReport()
        openGallery()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun chooseDate(){
        binding.tvDate.text = Utils.getCurrentDate()
        binding.linearDate.setOnClickListener { Utils.datePicker(this,binding.tvDate) }
    }

    private fun dropDownMenu(arr : Array<String>, @LayoutRes layout : Int, tv : AutoCompleteTextView){
        val adapter = ArrayAdapter(this, layout,arr)
        with(tv){
            setAdapter(adapter)
            onItemClickListener = this@CreateUpdateReportActivity
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        if (parent == binding.tvWeek) {
            binding.tvWeek.setText(parent.getItemAtPosition(position).toString())
        } else if (parent == binding.tvReport) {
            binding.tvReport.setText(parent.getItemAtPosition(position).toString())
        }
    }

    private fun setupDropDown(){
        val week = resources.getStringArray(R.array.week)
        val report = resources.getStringArray(R.array.report)
        dropDownMenu(week,R.layout.dropdown_item,binding.tvWeek)
        dropDownMenu(report,R.layout.dropdown_item,binding.tvReport)
    }

    private fun selectSpinner() {
        binding.apply {
            etIndicator.setOnClickListener {
                Utils.spinnerDialog(searchableSpinnerFrom,etIndicator, listAchievement){ achievement ->
                    listAchievementActivity.add(achievement)
                    listAchievement.remove(achievement)
                    showActivityAchievement(listAchievementActivity)
                }
            }
        }
    }

    private fun showActivityAchievement(data : ArrayList<String>){
        adapterAchievementActivityAdapter = AchievementActivityAdapter(data, object : AchievementActivityAdapter.OnClick{
            override fun onDelete(data: Int,text : String) {
                listAchievementActivity.removeAt(data)
                listAchievement.add(text)
                binding.rvAchievementActivity.adapter?.notifyItemRemoved(data)
                binding.rvAchievementActivity.adapter?.notifyItemRangeChanged(data,listAchievementActivity.size)

            }
        })
        binding.rvAchievementActivity.apply {
            adapter = adapterAchievementActivityAdapter
            layoutManager = LinearLayoutManager(this@CreateUpdateReportActivity)
        }
    }

    private fun createReport(){
       binding.apply {
           btnSave.setOnClickListener {
               val tittle = "${tvWeek.text.toString()} ${tvReport.text.toString()}"
               val date = tvDate.text.toString().trim()
               if(tittle.isNotBlank() && date.isNotBlank() && listAchievementActivity.isNotEmpty()){
                   doCreateReport(tittle,date,listAchievementActivity,listImages)
               }
           }
       }
    }

    private fun doCreateReport(
        tittle : String,date : String,
        indicator : MutableList<String>,images : MutableList<String>
    ){
        reportViewModel.createReport(tittle,date, indicator, images)
        reportViewModel.message.observe(this){
            when(it){
                is Resource.Success -> {
                    Toast.makeText(this, "Tambah laporan sukses", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,HomeActivity::class.java))
                    finish()
                }
                is Resource.Loading -> {}

                is Resource.Failure -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uriList = data?.getParcelableArrayListExtra<Uri>("list_uri")
            if(uriList != null){
                listImages.clear()
                Utils.showImageReport(true,null,uriList,binding,this)
                for(i in uriList){
                    listImages.add(Utils.uploadImage(i,this))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openGallery(){
        val intent = Intent(this, ImageFragment::class.java)
        binding.linearImage.setOnClickListener {
            listImageBitmap.clear()
            getResult.launch(intent)
        }
        binding.image.setOnClickListener {
            getResult.launch(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}