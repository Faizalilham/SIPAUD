@file:Suppress("ConvertArgumentToSet", "ConvertArgumentToSet", "ConvertArgumentToSet")

package android.coding.ourapp.presentation.ui
import android.app.Activity
import android.coding.ourapp.R
import android.coding.ourapp.adapter.AchievementAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.Achievement
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.databinding.ActivityCreateUpdateReportBinding
import android.coding.ourapp.presentation.viewmodel.AchievementViewModel
import android.coding.ourapp.presentation.viewmodel.report.ReportViewModel
import android.coding.ourapp.utils.Key.Companion.ID_CHILD
import android.coding.ourapp.utils.Key.Companion.ID_PARENT
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.leo.searchablespinner.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("RemoveToStringInStringTemplate", "RemoveToStringInStringTemplate",
    "RemoveToStringInStringTemplate", "RemoveToStringInStringTemplate",
    "RemoveToStringInStringTemplate", "RemoveToStringInStringTemplate", "NAME_SHADOWING",
    "DEPRECATION"
)
@AndroidEntryPoint
class CreateUpdateReportActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var _binding : ActivityCreateUpdateReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchableSpinnerFrom : SearchableSpinner
    private lateinit var adapterAchievementAdapter: AchievementAdapter

    @Inject
    lateinit var firebaseDatabase : FirebaseDatabase

    private var nameStudent : String? = null

    // DATA YANG DIPERLUKAN UTK UPDATE, DIDAPAT DARI INTENT
    private var idStudent : String? = null
    private var idParent : String? = null
    private var idChild : String? = null
    private var name : String? = null

    private val listAchievementActivity = arrayListOf<String>()
    private val listAchievementActivityRemote = arrayListOf<String>()
    private var listImages = arrayListOf<String>()
    private val listImageBitmap = arrayListOf<Bitmap>()
    private lateinit var listUri: ArrayList<Uri>
    private val listReport : MutableList<Report> = mutableListOf()
    private val reportViewModel by viewModels<ReportViewModel>()
    private val achievementViewModel by viewModels<AchievementViewModel>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchableSpinnerFrom = SearchableSpinner(this)

        nameStudent = intent.getStringExtra(EXTRA_NAME)
        name = intent.getStringExtra("nama")
        idStudent = intent.getStringExtra(EXTRA_ID)
        idParent = intent.getStringExtra(ID_PARENT)
        idChild = intent.getStringExtra(ID_CHILD)
        listUri =  intent.getParcelableArrayListExtra("list_uri") ?: arrayListOf()


        if(listUri != null){
            Utils.showImageReport(true,null,listUri,binding,this)
            for(i in listUri){
                listImages.add(Utils.uploadImage(i,this))
            }
        }



        binding.tvTittle.text = nameStudent
        chooseDate()
        openGallery()
        back()

        if(idParent != null && idChild != null) {
            Log.d("CEK INTENT","\"$idParent $idChild $nameStudent $idStudent \"")
            updateReport()

            binding.tittle.text = "Update Laporan"
            binding.btnSave.text = "Update Laporan"
        }else{
            Log.d("CEK INTENT 2","\"$idParent $idChild $nameStudent \"")
            getAllReport()
            createReport()
            setupDropDown()
        }
        getAchievement()



    }

    private fun getAchievement(){
        achievementViewModel.myList.observe(this){
            val listDatax = it.distinct()
            if(it != null && it.isNotEmpty()){
                showActivityAchievement(listDatax)
            }
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
                            id  = dataByName[0].ids
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



    private fun showActivityAchievement(datas : List<String>){

        val listData = mutableListOf<Achievement>()
        val listDatas = mutableListOf<String>()

        if(idParent != null && idChild != null){
            achievementViewModel.myList.observe(this){
                if(it != null && it.isNotEmpty()){
                    listDatas.addAll(it.distinct())
                }
            }

            val a = listDatas.intersect(datas).toMutableList()
            val b = listDatas.subtract(datas).toMutableList()

            listAchievementActivity.addAll(a)
            a.forEach {
                listData.add(Achievement(it,true))
            }

            b.forEach {
                listData.add(Achievement(it,false))
            }

        }else{
            datas.forEach {
                listData.add(Achievement(it,false))
            }
        }
        adapterAchievementAdapter = AchievementAdapter(listData,object : AchievementAdapter.OnClick{
            override fun onChecked(name: List<String>,isMuncul : Boolean) {


                if(isMuncul) listAchievementActivity.addAll(name) else listAchievementActivity.removeAll(name)

               Log.d("TAGSS","$listAchievementActivity $name")
            }
        })
        binding.rvAchievementActivity.apply {
            adapter = adapterAchievementAdapter
            layoutManager = LinearLayoutManager(this@CreateUpdateReportActivity)
        }
        adapterAchievementAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createReport(){
        binding.apply {
            btnSave.setOnClickListener {
                val tittle = "${tvWeek.text.toString()}, ${tvReport.text.toString()}"
                val date = tvDate.text.toString().trim()

                if(tittle.isNotBlank() && date.isNotBlank() && listAchievementActivity.isNotEmpty()){
                    doCreateReport(binding.tvTittle.text.toString(),tittle,date,
                        Utils.removeNumbersFromList(resources.getStringArray(R.array.agama).toList()).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                        Utils.removeNumbersFromList(resources.getStringArray(R.array.moral).toList()).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                        Utils.removeNumbersFromList(resources.getStringArray(R.array.pekerti).toList()).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                        listImages)

                }else{
                    Toast.makeText(this@CreateUpdateReportActivity, "Data tidak lengkap", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

                        startActivity(Intent(this, StudentsActivity::class.java).also{finish()})

                        Toast.makeText(this, "Tambah laporan sukses", Toast.LENGTH_SHORT).show()
                        Utils.hideKeyboard(binding.btnSave)
                    }
                    is Resource.Loading -> {
                        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Failure -> {
                        Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                    }
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
                        indicatorAgama =   Utils.removeNumbersFromList(resources.getStringArray(R.array.agama).toList()).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                        indicatorMoral =   Utils.removeNumbersFromList(resources.getStringArray(R.array.moral).toList()).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
                        indicatorPekerti =   Utils.removeNumbersFromList(resources.getStringArray(R.array.pekerti).toList()).intersect(listAchievementActivity.toSet()).toTypedArray().toMutableList(),
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
                        finish()
                    }else{
                        Toast.makeText(this, "Tambah laporan sukses", Toast.LENGTH_SHORT).show()
                        Utils.hideKeyboard(binding.btnSave)
                        startActivity(Intent(this, StudentsActivity::class.java).also{finish()})
                    }

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
            if(listAchievementActivity.isNotEmpty()){
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
            }else{
                Toast.makeText(this, "Minimal terdapat 1 capaian kegiatan untuk sisw", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewUpdate(i :String,idChild : String){
        reportViewModel.getDataReportById(i).observe(this){ it ->
            when(it){
                is Resource.Success -> {
                    binding.apply {
                        val dataDetail = it.result.reports.filterNotNull().filter {  it.month == intent.getStringExtra("month") }
                        val dataDetailResult = dataDetail.filter { it.id == idChild }
                        Log.d("DATA DETAIL UPDATE","$dataDetail")
                        dataDetailResult.forEach { report ->
                            val reportNames = report.reportName.split(",")
                            tvWeek.setText(reportNames[0])
                            tvReport.setText(reportNames[1])
                            setupDropDown()
                            tvDate.text = report.reportDate
//                           listAchievementAgama.addAll(resources.getStringArray(R.array.agama).subtract(report.indicatorAgama).toTypedArray().toMutableList())
//                           listAchievementMoral.addAll(resources.getStringArray(R.array.moral).subtract(report.indicatorAgama).toTypedArray().toMutableList())
//
                            listAchievementActivityRemote.addAll(report.indicatorPekerti + report.indicatorMoral + report.indicatorAgama)
                            Log.d("LIST","$listAchievementActivity")
                            showActivityAchievement(ArrayList(listAchievementActivityRemote.distinct()))
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


//    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data = result.data
//            val uriList = data?.getParcelableArrayListExtra<Uri>("list_uri")
//            if(uriList != null){
//                listImages.clear()
//
//            }
//        }
//    }

    private fun openGallery(){
        val intent = Intent(this, ImageFragment::class.java)
        intent.putExtra(EXTRA_NAME, nameStudent)
        intent.putExtra(EXTRA_ID, idStudent)
        intent.putExtra(EXTRA_ID_CHILD, idChild)
        intent.putExtra(EXTRA_ID_PARENT, idParent)


        binding.linearImage.setOnClickListener {
            listImageBitmap.clear()
            startActivity(intent)
//            getResult.launch(intent)
        }
        binding.image.setOnClickListener {
            startActivity(intent)
//            getResult.launch(intent)
        }
    }

    companion object{
        const val EXTRA_NAME = "name_student"
        const val EXTRA_ID = "id_student"
        const val EXTRA_ID_CHILD = "id_child"
        const val EXTRA_ID_PARENT = "id_parent"
        const val EXTRA_ID_ACHIEVEMENT = "list_data_achievement"
    }

    private fun back(){
        binding.imageBack.setOnClickListener {
            startActivity(Intent(this,StudentsActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}