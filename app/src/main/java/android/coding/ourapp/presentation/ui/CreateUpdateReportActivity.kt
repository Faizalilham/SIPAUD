package android.coding.ourapp.presentation.ui
import android.app.Activity
import android.coding.ourapp.R
import android.coding.ourapp.adapter.AchievementActivityAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Month
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.databinding.ActivityCreateUpdateReportBinding
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Key.Companion.ID_CHILD
import android.coding.ourapp.utils.Key.Companion.ID_PARENT
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
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

    @Inject
    lateinit var firebaseDatabase : FirebaseDatabase

    private var nameStudent : String? = null
    private var idStudent : String? = null
    private var idParent : String? = null
    private var idChild : String? = null

    private val listAchievementActivity = arrayListOf<String>()
    private val listAchievementAgama = arrayListOf<String>()
    private val listAchievementMoral = arrayListOf<String>()
    private val listAchievementPekerti = arrayListOf<String>()
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

        nameStudent = intent.getStringExtra(EXTRA_NAME)
        idStudent = intent.getStringExtra(EXTRA_ID)
        idParent = intent.getStringExtra(ID_PARENT)
        idChild = intent.getStringExtra(ID_CHILD)
        binding.tvTittle.text = nameStudent
        chooseDate()
        selectSpinner()
        openGallery()
        back()

        if(idParent != null && idChild != null) {
            Log.d("CEK INTENT","\"$idParent $idChild $nameStudent $idStudent \"")
            updateReport()
        }else{
            Log.d("CEK INTENT 2","\"$idParent $idChild $nameStudent \"")
            listAchievementAgama.addAll(resources.getStringArray(R.array.agama))
            listAchievementMoral.addAll(resources.getStringArray(R.array.moral))
            listAchievementPekerti.addAll(resources.getStringArray(R.array.pekerti))
            getAllReport()
            createReport()
            setupDropDown()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllReport(){
        reportViewModel.getAllReport.observe(this){
            when (it) {
                is Resource.Success -> {
                    if(idStudent != null){
                        var id = ""
                        val dataByName = it.result.filter { result -> result.idStudent == idStudent }
                        dataByName.forEach { result ->
                            listReport.addAll(result.reports)
                        }
                        if(dataByName.isNotEmpty()){
                            id  = dataByName[0].id
                        }
                        val isAdded = it.result.any { result ->
                            result.idStudent == idStudent
                        }

                        if(isAdded) updateReport(id,listReport) else createReport()
                    }

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
            etIndicator.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                }
            }
            etIndicator.setOnClickListener {
                Utils.spinnerDialog(searchableSpinnerFrom,etIndicator, ArrayList(listAchievementAgama.distinct())){ achievement ->
                    binding.rvAchievementActivity.visibility =  View.VISIBLE
                    listAchievementActivity.add(achievement)
                    listAchievementAgama.remove(achievement)
                    showActivityAchievement(listAchievementActivity)
                }
            }
            etSecondIndicator.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                }
            }

            etSecondIndicator.setOnClickListener{
                Utils.spinnerDialog(searchableSpinnerFrom,etSecondIndicator, ArrayList(listAchievementMoral.distinct())){ achievement ->
                    binding.rvAchievementActivity.visibility =  View.VISIBLE
                    listAchievementActivity.add(achievement)
                    listAchievementMoral.remove(achievement)
                    showActivityAchievement(listAchievementActivity)
                }
            }

            etIndicatorThird.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                }
            }
            etIndicatorThird.setOnClickListener{
                Utils.spinnerDialog(searchableSpinnerFrom,etIndicatorThird, ArrayList(listAchievementPekerti.distinct())){ achievement ->
                    binding.rvAchievementActivity.visibility =  View.VISIBLE
                    listAchievementActivity.add(achievement)
                    listAchievementPekerti.remove(achievement)
                    showActivityAchievement(listAchievementActivity)
                }
            }
        }
    }

    private fun showActivityAchievement(datas : ArrayList<String>){
        adapterAchievementActivityAdapter = AchievementActivityAdapter(datas, object : AchievementActivityAdapter.OnClick{
            override fun onDelete(data: Int,text : String) {
                if (data >= 0 && data < datas.size) {
                    datas.removeAt(data)
                    if (resources.getStringArray(R.array.agama).contains(text)) {
                        listAchievementAgama.add(text)
                    } else if(resources.getStringArray(R.array.moral).contains(text)) {
                        listAchievementMoral.add(text)
                    }else{
                        listAchievementPekerti.add(text)
                    }
                }else{
                    binding.rvAchievementActivity.visibility =  View.GONE
                }
                binding.rvAchievementActivity.adapter?.notifyItemRemoved(data)
                binding.rvAchievementActivity.adapter?.notifyItemRangeChanged(data, datas.size)
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
                   doCreateReport(binding.tvTittle.text.toString(),tittle,date,
                       resources.getStringArray(R.array.agama).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                       resources.getStringArray(R.array.moral).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                       resources.getStringArray(R.array.pekerti).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                       listImages)
               }
           }
       }
    }

    private fun doCreateReport(
        studentName : String,
        tittle : String,date : String,
        indicatorAgama : MutableList<String>,
        indicatorMoral : MutableList<String>,
        indicatorPekerti : MutableList<String>,
        images : MutableList<String>
    ){
        if(idStudent != null){
            reportViewModel.createReport(idStudent!!,studentName,tittle,date, indicatorAgama,indicatorMoral,indicatorPekerti, images)
            reportViewModel.message.observe(this){
                when(it){
                    is Resource.Success -> {
                        Toast.makeText(this, "Tambah laporan sukses", Toast.LENGTH_SHORT).show()
                        Utils.hideKeyboard(binding.btnSave)
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateReport(idParent : String, listReport : MutableList<Report>){
        binding.apply {
            btnSave.setOnClickListener {
                val tittle = "${tvWeek.text.toString()}, ${tvReport.text.toString()}"
                val date = tvDate.text.toString().trim()
                val idChild =  firebaseDatabase.reference.push().key.toString()
                listReport.add(
                    Report(id=idChild,reportName = tittle, reportDate = date, month = Utils.getMonthFromStringDate(date),
                    indicatorAgama =  resources.getStringArray(R.array.agama).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                    indicatorMoral =  resources.getStringArray(R.array.moral).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                    indicatorPekerti =  resources.getStringArray(R.array.pekerti).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                        images = listImages
                ))
                doUpdateReport(
                    idParent = idParent,
                    listReport = listReport,
                    indicatorAgama = mutableListOf(),
                    indicatorMoral = mutableListOf(),
                    indicatorPekerti = mutableListOf(),
                    images = mutableListOf(),
                    date = "",
                    idChild = "",
                    tittle = "",
                    listNarrative = mutableListOf()
                )
            }
        }
    }

    private fun doUpdateReport(
        idParent : String,idChild: String,
        tittle : String,date : String,
        indicatorAgama : MutableList<String>,
        indicatorMoral : MutableList<String>,
        indicatorPekerti : MutableList<String>,
        images : MutableList<String>,listReport : MutableList<Report>, listNarrative : MutableList<Narrative>
    ){

        reportViewModel.updateReport(idParent,idChild, tittle, date, indicatorAgama,indicatorMoral,indicatorPekerti, images, listReport,listNarrative)
        reportViewModel.message.observe(this){
            when(it){
                is Resource.Success -> {
                    if(listReport.isEmpty()){
                        Toast.makeText(this, "Update laporan sukses", Toast.LENGTH_SHORT).show()
                        Utils.hideKeyboard(binding.btnSave)
                    }else{
                        Toast.makeText(this, "Tambah laporan sukses", Toast.LENGTH_SHORT).show()
                        Utils.hideKeyboard(binding.btnSave)
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

    private fun updateReport(){
        setupViewUpdate(idParent!!,idChild!!)
        binding.btnSave.setOnClickListener {
            val tittle = "${binding.tvWeek.text.toString()}, ${binding.tvReport.text.toString()}"
            Log.d("CEK 3",tittle)
            doUpdateReport(
                idParent!!,
                idChild!!,
                tittle,
                binding.tvDate.text.toString(),
                resources.getStringArray(R.array.agama).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                resources.getStringArray(R.array.moral).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                resources.getStringArray(R.array.pekerti).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                listImages,
                mutableListOf(),
                mutableListOf()
            )
        }
    }

    private fun setupViewUpdate(i :String,idChild : String){
       reportViewModel.getDataReportById(i).observe(this){
           when(it){
               is Resource.Success -> {
                   binding.apply {
                       val dataDetail = it.result.reports.filter {  it.month == intent.getStringExtra("month") }
                       val dataDetailResult = dataDetail.filter { it.id == idChild }
                       Log.d("DATA DETAIL UPDATE","$dataDetail")
                       dataDetailResult.forEach { report ->
                           val reportNames = report.reportName.split(",")
                           tvWeek.setText(reportNames[0])
                           tvReport.setText(reportNames[1])
                           setupDropDown()
                           tvDate.text = report.reportDate
                           listAchievementAgama.addAll(resources.getStringArray(R.array.agama).subtract(report.indicatorAgama).toTypedArray().toMutableList())
                           listAchievementMoral.addAll(resources.getStringArray(R.array.moral).subtract(report.indicatorAgama).toTypedArray().toMutableList())
                           listAchievementPekerti.addAll(resources.getStringArray(R.array.pekerti).subtract(report.indicatorPekerti).toTypedArray().toMutableList())
                           listAchievementActivity.addAll(report.indicatorAgama + report.indicatorMoral + report.indicatorPekerti)
                           Log.d("LIST","$listAchievementActivity")
                           showActivityAchievement(ArrayList(listAchievementActivity.distinct()))
                           listImages.addAll(report.images)
                           for(i in listImages){
                               listImageBitmap.add(Utils.convertStringToBitmap(i))
                           }
                           Utils.showImageReport(true,listImageBitmap,null,this,this@CreateUpdateReportActivity) }

                   }
               }
               is Resource.Loading -> {}
               is Resource.Failure -> {
                   Log.d("ERROR","${it.exception}")
               }
               else -> {
                   Log.d("ERROR","There is an error")
               }

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

    companion object{
        const val EXTRA_NAME = "name_student"
        const val EXTRA_ID = "id_student"
    }

    private fun back(){
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}