package android.coding.ourapp.presentation.ui

import android.app.Activity
import android.coding.ourapp.R
import android.coding.ourapp.adapter.AchievementActivityAdapter
import android.coding.ourapp.adapter.StudentActivityAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.repository.report_month.ReportMonthRepository
import android.coding.ourapp.data.repository.student.StudentRepository
import android.coding.ourapp.databinding.ActivityCreateUpdateAsesmentBinding
import android.coding.ourapp.helper.ViewModelFactory
import android.coding.ourapp.presentation.viewmodel.assessment.AssessmentViewModel
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.leo.searchablespinner.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateUpdateAsesmentActivity : AppCompatActivity() {
    private var _binding : ActivityCreateUpdateAsesmentBinding? = null
    private val binding get() = _binding!!
    private val assessmentViewModel by viewModels<AssessmentViewModel>()
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var adapterAchievementActivityAdapter: AchievementActivityAdapter
    private lateinit var adapterStudentActivityAdapter: StudentActivityAdapter
    private val listAchievementActivity = arrayListOf<String>()
    private val listStudentActivity = arrayListOf<String>()
    private val listStudentSelected = arrayListOf<String>()
    private val listImage = arrayListOf<String>()
    private val listImageBitmap = arrayListOf<Bitmap>()
    private lateinit var searchableSpinnerFrom : SearchableSpinner
    var i : String? = null
    var favorite : Boolean? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateAsesmentBinding.inflate(layoutInflater)
        searchableSpinnerFrom = SearchableSpinner(this)
        setContentView(binding.root)
        Utils.language(this)
        binding.tvDate.text = Utils.getCurrentDate()
        initViewModel()
        imeOptions()
        selectSpinner()
        i = intent.getStringExtra("id")
        favorite = intent.getBooleanExtra("favorite",false)
        openGallery()
        getIntentDataEdit(i)
        postUpdate(i)
        getAllDataStudent()
        moveToHome()
    }

    private fun initViewModel() {
        firebaseHelper = FirebaseHelper()
        val studentRepository = StudentRepository(firebaseHelper)
        val monthRepository = ReportMonthRepository(firebaseHelper)
        val viewModelFactory = ViewModelFactory(studentRepository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory)[StudentViewModel::class.java]
    }

    private fun getIntentDataEdit(i : String?){
        if(i != null){
            assessmentViewModel.getDataById(i).observe(this){
                when(it){
                    is Resource.Success -> {
                        setupViewUpdate(it.result,null)
                        for(image in it.result.image){
                            listImageBitmap.add(Utils.convertStringToBitmap(image))
                        }
//                        Utils.showImageAssessment(true,listImageBitmap,null,binding,this)
                        favorite = it.result.favorite
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
    }

    private fun setupViewUpdate(assessmentRequest: AssessmentRequest,idx : String?){
        binding.apply {
            etTittle.setText(assessmentRequest.tittle)
            etDescription.setText(assessmentRequest.description)
            etFeedback.setText(assessmentRequest.feedback)
            listAchievementActivity.addAll(assessmentRequest.achievementActivity)
            listStudentSelected.addAll(assessmentRequest.students)
            if(idx == null){
                listImage.addAll(assessmentRequest.image)
            }else{
                listImage.clear()
            }
            showStudentsActivity(listStudentSelected)
//            showActivityAchievement(listAchievementActivity)
            btnSave.setText(R.string.update_asesment)
            tittle.setText(R.string.update_asesment)
        }
    }

    private fun getAllDataStudent() {
        studentViewModel.getData().observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    val students = result.result
                    students.forEach {
                        listStudentActivity.add(it.nameStudent.toString())
                    }
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

    private fun selectSpinner() {
        binding.apply {
            etStudents.setOnClickListener {
                Utils.spinnerDialog(searchableSpinnerFrom,etStudents,ArrayList(Utils.unique(listStudentActivity,listStudentSelected).sorted().distinct())){ student ->
                    listStudentActivity.remove(student)
                    listStudentSelected.add(student)
                    showStudentsActivity(listStudentSelected)
                }
            }
        }
    }

//    private fun showActivityAchievement(data : ArrayList<String>){
//        adapterAchievementActivityAdapter = AchievementActivityAdapter(data, object : AchievementActivityAdapter.OnClick{
//            override fun onDelete(data: Int) {
//                listAchievementActivity.removeAt(data)
//                binding.rvAchievementActivity.adapter?.notifyItemRemoved(data)
//                binding.rvAchievementActivity.adapter?.notifyItemRangeChanged(data,listAchievementActivity.size)
//
//            }
//        })
//        binding.rvAchievementActivity.apply {
//            adapter = adapterAchievementActivityAdapter
//            layoutManager = LinearLayoutManager(this@CreateUpdateAsesmentActivity)
//        }
//    }

    private fun showStudentsActivity(datas : ArrayList<String>){
        adapterStudentActivityAdapter = StudentActivityAdapter(datas,object : StudentActivityAdapter.OnClick{
            override fun onDelete(data:Int,student : String) {
                listStudentSelected.removeAt(data)
                listStudentActivity.add(student)
                adapterStudentActivityAdapter.notifyItemRemoved(data)
                adapterStudentActivityAdapter.notifyItemRangeChanged(data,listStudentSelected.size)

            }
        })
        binding.rvStudents.layoutManager =  GridLayoutManager(this,2)
        binding.rvStudents.adapter = adapterStudentActivityAdapter
    }


    private fun postUpdate(id : String?){
        binding.btnSave.setOnClickListener {
            val tittle = binding.etTittle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val date = binding.tvDate.text.toString()
            val feedback = binding.etFeedback.text.toString().trim()

            if(Utils.createUpdateAssessmentCondition(
                    tittle,description,date,listStudentSelected,listAchievementActivity,feedback
            )){

                if(id == null){
                    assessmentViewModel.createAssessment(
                        tittle,description,date,listStudentSelected,listImage,listAchievementActivity,feedback,false
                    )
                    assessmentViewModel.message.observe(this){
                        when(it){
                            is Resource.Success -> {
//                                Utils.showImageAssessment(false,null,null,binding,this)
                                Toast.makeText(this, "Tambah narasi sukses", Toast.LENGTH_SHORT).show()
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
                }else{
                    assessmentViewModel.updateAssessment(
                        AssessmentRequest(tittle,description,date,listStudentSelected,listImage,listAchievementActivity,feedback,favorite,id)
                    )
                    assessmentViewModel.message.observe(this){
                        when(it){
                            is Resource.Success -> {
                                Toast.makeText(this, "Update data berhasil", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,HomeActivity::class.java).also { finish() })
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

            }else{
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun imeOptions(){
        binding.etAchievement.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(binding.etAchievement.text.toString().isNotBlank()){
                    listAchievementActivity.add(binding.etAchievement.text.toString())
//                    showActivityAchievement(listAchievementActivity)
                    binding.etAchievement.text?.clear()
                }else{
                    Toast.makeText(this, resources.getString(R.string.warning_form), Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uriList = data?.getParcelableArrayListExtra<Uri>("list_uri")
            if(uriList != null){
                listImage.clear()
//                Utils.showImageAssessment(true,null,uriList,binding,this)
                for(i in uriList){
                    listImage.add(Utils.uploadImage(i,this))
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

    private fun moveToHome(){
        if(i == null){
            binding.linearLayout4.setOnClickListener {
                startActivity(Intent(this,HomeActivity::class.java).also{
                    finish()
                })
            }
        }else{
            binding.linearLayout4.setOnClickListener {
                finish()
            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(i == null){
            binding.linearLayout4.setOnClickListener {
                startActivity(Intent(this,HomeActivity::class.java).also{
                    finish()
                })
            }
        }else{
            binding.linearLayout4.setOnClickListener {
                finish()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}