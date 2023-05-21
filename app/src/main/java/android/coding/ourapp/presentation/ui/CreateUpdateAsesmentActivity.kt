package android.coding.ourapp.presentation.ui


import android.coding.ourapp.R
import android.coding.ourapp.adapter.AchievementActivityAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.databinding.ActivityCreateUpdateAsesmentBinding
import android.coding.ourapp.databinding.FragmentImageBinding
import android.coding.ourapp.databinding.ImageBottomSheetBinding
import android.coding.ourapp.presentation.viewmodel.assessment.AssessmentViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio

@AndroidEntryPoint
class CreateUpdateAsesmentActivity : AppCompatActivity() {
    private var _binding : ActivityCreateUpdateAsesmentBinding? = null
    private val binding get() = _binding!!
    private val assessmentViewModel by viewModels<AssessmentViewModel>()
    private lateinit var adapterAchievementActivityAdapter: AchievementActivityAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUpdateAsesmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.language(this)
        binding.tvDate.text = Utils.getCurrentDate()
        moveToHome()
        openGallery()
        post()

    }

    private fun showActivityAchievement(data : ArrayList<String>){
        adapterAchievementActivityAdapter = AchievementActivityAdapter(data, object : AchievementActivityAdapter.OnClick{
            override fun onDelete(data: String) {

            }
        })

        binding.rvAchievementActivity.apply {
            adapter = adapterAchievementActivityAdapter
            layoutManager = LinearLayoutManager(this@CreateUpdateAsesmentActivity)
        }

    }

    private fun post(){
        binding.btnSave.setOnClickListener {
            val tittle = binding.etTittle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val date = binding.tvDate.text.toString()
            val feedback = binding.etFeedback.text.toString().trim()
            val listImage = arrayListOf<String>("Image1","Image2")
            val listAchievementActivity = arrayListOf<String>("Sudah bisa menulis","Sudah bisa menggambar")
            val listStudent = arrayListOf<String>("Faizal","Faishal","Hildan","Yusron")
            assessmentViewModel.createAssessment(AssessmentRequest(
                tittle,description,date,listStudent,listImage,listAchievementActivity,feedback,false
            ))

            assessmentViewModel.message.observe(this){
                when(it){
                    is Resource.Success -> {
                        showLoading(false)
                        Toast.makeText(this, it.result, Toast.LENGTH_SHORT).show()
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
                }
            }
        }
    }
    private fun imeOptions(){
        binding.etAchievement
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

    private fun openGallery(){
        binding.image.setOnClickListener {
            startActivity(Intent(this, ImageFragment::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}