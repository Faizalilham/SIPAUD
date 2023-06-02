package android.coding.ourapp.presentation.ui

import android.app.Activity
import android.coding.ourapp.R
import android.coding.ourapp.adapter.AchievementActivityAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Month
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.databinding.ActivityCreateUpdateReportBinding
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.FirebaseDatabase
import com.leo.searchablespinner.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateUpdateReportActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var _binding : ActivityCreateUpdateReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchableSpinnerFrom : SearchableSpinner
    private lateinit var adapterAchievementActivityAdapter: AchievementActivityAdapter
    private var student: Student? = null

    @Inject
    lateinit var firebaseDatabase : FirebaseDatabase

//    private var i : String? = null

    private val listAchievementActivity = arrayListOf<String>()
    private val listAchievement = arrayListOf<String>("Menulis","Menggambar")
    private val listImages = arrayListOf<String>()
    private val listImageBitmap = arrayListOf<Bitmap>()
    private val listReport : MutableList<Report> = mutableListOf()
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
        getAllReport()

        student = intent.getParcelableExtra(EXTRA_NAME)
        binding.tvTittle.setText(student?.nameStudent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllReport(){
        reportViewModel.getAllReport.observe(this){
            when (it) {
                is Resource.Success -> {
                    var idParent = ""
//                    var idChild = ""
                    it.result.forEach { result->
                        idParent = result.id
                        listReport.addAll(result.reports)
//                        result.reports.forEach { reports->  idChild = reports.id}
                    }
                    val isAdded = it.result.any { result ->
                        result.studentName == "faizal"
                    }
                    if(isAdded) updateReport(idParent,listReport) else createReport()
                }

                is Resource.Loading -> {}

                is Resource.Failure -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                }
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createReport(){
       binding.apply {
           btnSave.setOnClickListener {
               val tittle = "${tvWeek.text.toString()}, ${tvReport.text.toString()}"
               val date = tvDate.text.toString().trim()
               if(tittle.isNotBlank() && date.isNotBlank() && listAchievementActivity.isNotEmpty()){
                   doCreateReport("faizal",tittle,date,listAchievementActivity,listImages)
               }
           }
       }
    }

    private fun doCreateReport(
        studentName : String,
        tittle : String,date : String,
        indicator : MutableList<String>,images : MutableList<String>
    ){
        reportViewModel.createReport(studentName,tittle,date, indicator, images)
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

    private fun updateReport(idParent : String,listReport : MutableList<Report>){
        binding.apply {
            btnSave.setOnClickListener {
                val tittle = "${tvWeek.text.toString()}, ${tvReport.text.toString()}"
                val date = tvDate.text.toString().trim()
                val idChild =  firebaseDatabase.reference.push().key.toString()
                listReport.add(
                    Report(id=idChild,reportName = tittle, reportDate = date, month = Utils.getMonthFromStringDate(date),
                    indicator = listAchievementActivity, images = listImages
                ))
                doUpdateReport(
                    idParent = idParent,
                    listReport = listReport,
                    indicator = mutableListOf(),
                    images = mutableListOf(),
                    date = "",
                    idChild = "",
                    tittle = ""
                )
            }
        }
    }

    private fun doUpdateReport(
        idParent : String,idChild: String,
        tittle : String,date : String,
        indicator : MutableList<String>,images : MutableList<String>,listReport : MutableList<Report>
    ){

        reportViewModel.updateReport(idParent,idChild, tittle, date, indicator, images, listReport)
        reportViewModel.message.observe(this){
            when(it){
                is Resource.Success -> {
                    if(listReport.isEmpty()){
                        Toast.makeText(this, "Update laporan sukses", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Tambah laporan sukses", Toast.LENGTH_SHORT).show()
                    }
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

//    private fun setupViewUpdate(i :String){
//       reportViewModel.getDataReportById(i).observe(this){
//           when(it){
//               is Resource.Success -> {
//                   binding.apply {
//                       val reportName = i.reportName.split(",")
//                       tvWeek.setText(reportName[0])
//                       tvReport.setText(reportName[1])
//                       tvDate.text = i.reportDate
//                       listAchievement.addAll(i.indicator)
//                       listImages.addAll(i.images)
//                   }
//               }
//           }
//       }
//    }


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

    companion object{
        const val EXTRA_NAME = "extra_name"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}