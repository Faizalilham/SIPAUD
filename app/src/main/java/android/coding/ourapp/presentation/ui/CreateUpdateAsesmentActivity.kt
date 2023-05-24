package android.coding.ourapp.presentation.ui


import android.coding.ourapp.R
import android.coding.ourapp.adapter.AchievementActivityAdapter
import android.coding.ourapp.adapter.StudentActivityAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.databinding.ActivityCreateUpdateAsesmentBinding
import android.coding.ourapp.presentation.viewmodel.assessment.AssessmentViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.google.firebase.database.FirebaseDatabase
import com.leo.searchablespinner.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateUpdateAsesmentActivity : AppCompatActivity() {
    private var _binding : ActivityCreateUpdateAsesmentBinding? = null
    private val binding get() = _binding!!
    private val assessmentViewModel by viewModels<AssessmentViewModel>()
    private lateinit var adapterAchievementActivityAdapter: AchievementActivityAdapter
    private lateinit var adapterStudentActivityAdapter: StudentActivityAdapter
    private val listAchievementActivity = arrayListOf<String>()
    private val listStudentActivity = arrayListOf<String>()
    private val listImage = arrayListOf<String>()
    private lateinit var searchableSpinnerFrom : SearchableSpinner
    var i : String? = null
    var ids : String? = null
    var favorite : Boolean? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateAsesmentBinding.inflate(layoutInflater)
        searchableSpinnerFrom = SearchableSpinner(this)
        setContentView(binding.root)
        binding.tvDate.text = Utils.getCurrentDate()
        moveToHome()
        imeOptions()
        selectSpinner()
        getIntentDataImage()
        i = intent.getStringExtra("id")
        ids = intent.getStringExtra("ids")
        openGallery(i)
        favorite = intent.getBooleanExtra("favorite",false)
        getIntentDataEdit(i)
        postUpdate(i)

    }

    private fun getIntentDataImage(){
        val uriList = intent.getSerializableExtra("list_uri")
        if(uriList != null){
            val uriArrayList =  intent.getSerializableExtra("list_uri") as ArrayList<Uri>
            Utils.showImageAssessment(true,null,uriArrayList,binding,this)
            for(i in uriArrayList){
                listImage.add(Utils.uploadImage(i,this))
            }
        }
    }

    private fun getIntentDataEdit(i : String?){
        if(i != null){
            assessmentViewModel.getDataById(i).observe(this){
                when(it){
                    is Resource.Success -> {
                        setupViewUpdate(it.result,null)
                        val listImageBitmap = arrayListOf<Bitmap>()
                        for(i in it.result.image){
                            listImageBitmap.add(Utils.convertStringToBitmap(i))
                        }
                        Utils.showImageAssessment(true,listImageBitmap,null,binding,this)
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
        }else if(ids != null) {
            assessmentViewModel.getDataById(ids!!).observe(this){
                when(it){
                    is Resource.Success -> {
                        setupViewUpdate(it.result,ids)
                        favorite = it.result.favorite
                        getIntentDataImage()
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
            listStudentActivity.addAll(assessmentRequest.students)
            if(idx == null){
                listImage.addAll(assessmentRequest.image)
            }else{
                listImage.clear()
            }
            showStudentsActivity(listStudentActivity)
            showActivityAchievement(listAchievementActivity)
            btnSave.setText(R.string.update_asesment)
        }
    }


    private fun selectSpinner() {
        binding.apply {
            etStudents.setOnClickListener {
                Utils.spinnerDialog(searchableSpinnerFrom,etStudents,ArrayList(listAchievementActivity.sorted().distinct())){ student ->
                    listStudentActivity.add(student)
                    showStudentsActivity(listStudentActivity)
                }
            }
        }
    }

    private fun showActivityAchievement(data : ArrayList<String>){
        adapterAchievementActivityAdapter = AchievementActivityAdapter(data, object : AchievementActivityAdapter.OnClick{
            override fun onDelete(data: Int) {
                listAchievementActivity.removeAt(data)
                binding.rvAchievementActivity.adapter?.notifyItemRemoved(data)
                binding.rvAchievementActivity.adapter?.notifyItemRangeChanged(data,listAchievementActivity.size)

            }
        })

        binding.rvAchievementActivity.apply {
            adapter = adapterAchievementActivityAdapter
            layoutManager = LinearLayoutManager(this@CreateUpdateAsesmentActivity)
        }
    }

    private fun showStudentsActivity(datas : ArrayList<String>){
        adapterStudentActivityAdapter = StudentActivityAdapter(datas,object : StudentActivityAdapter.OnClick{
            override fun onDelete(data:Int) {
                listStudentActivity.removeAt(data)
                adapterStudentActivityAdapter.notifyItemRemoved(data)
                adapterStudentActivityAdapter.notifyItemRangeChanged(data,listStudentActivity.size)

            }

        })
        binding.rvStudents.setHasFixedSize(true)
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
                    tittle,description,date,listStudentActivity,listAchievementActivity,feedback
            )){

                if(id == null  && ids == null){
                    assessmentViewModel.createAssessment(
                        tittle,description,date,listStudentActivity,listImage,listAchievementActivity,feedback,false
                    )
                    assessmentViewModel.message.observe(this){
                        when(it){
                            is Resource.Success -> {
                                showLoading(false)
                                Utils.showImageAssessment(false,null,null,binding,this)
                                Toast.makeText(this, "Tambah narasi sukses", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,HomeActivity::class.java))
                                finish()
                            }
                            is Resource.Loading -> {
                                showLoading(true)
                            }
                            is Resource.Failure -> {
                                showLoading(false)
                                Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }else{
                    assessmentViewModel.updateAssessment(
                        AssessmentRequest(tittle,description,date,listStudentActivity,listImage,listAchievementActivity,feedback,favorite,ids)
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
                    showActivityAchievement(listAchievementActivity)
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

    private fun showLoading(isLoading : Boolean){
        if(isLoading){
            binding.loadingBg.visibility = View.VISIBLE
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loadingBg.visibility = View.GONE
            binding.loading.visibility = View.GONE
        }
    }

    private fun moveToHome(){
        binding.imageBack.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java).also{ finish() })
        }
    }

    private fun openGallery(id : String?){
       if(id == null){
           binding.image.setOnClickListener {
               startActivity(Intent(this, ImageFragment::class.java))
           }
       }else{
           binding.linearImage.setOnClickListener {
               startActivity(Intent(this, ImageFragment::class.java).also{
                   it.putExtra("id",id)
               })
           }
       }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}