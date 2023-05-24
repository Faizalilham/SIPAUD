package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.adapter.ImageSliderAdapter
import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.databinding.ActivityDetailBinding
import android.coding.ourapp.databinding.AlertComponentBinding
import android.coding.ourapp.databinding.ImageBottomSheetBinding
import android.coding.ourapp.presentation.viewmodel.assessment.AssessmentViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {


    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private val assessmentViewModel by viewModels<AssessmentViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIntentData(){
        val i = intent.getStringExtra("id")
        if(i != null){
            assessmentViewModel.getDataById(i).observe(this){
                when(it){
                    is Resource.Success -> {
                        setupView(it.result)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupView(i : AssessmentRequest){
        binding.apply {

            tvTittle.text = i.tittle
            tvDescription.text = i.description
            tvFeedback.text = i.feedback
            val date ="Dibuat pada ${i.date}"
            tvDate?.text = date
            tvName.text = Utils.convertListToString(i.students)
            tvActivity.text = Utils.convertListToString(i.achievementActivity)

            val listImageBitmap = arrayListOf<Bitmap>()
            for(i in i.image){
                listImageBitmap.add(Utils.convertStringToBitmap(i))
            }

            val adapter = ImageSliderAdapter(this@DetailActivity,listImageBitmap)
            image?.adapter = adapter

            val listImageUri = arrayListOf<Uri>()
            for(i in i.image){
                val uri = Utils.base64ToUri(i)
                if(uri != null){
                    listImageUri.add(uri)
                }
            }

            checkIsFavorite(i.favorite!!)
            updateAssessment(AssessmentRequest(i.tittle,i.description,Utils.getCurrentDate(),i.students,i.image,i.achievementActivity,i.feedback,!i.favorite,i.id))
            doDelete(i.id!!)
            updateDataToEdit(i.id,i.favorite)

            val message = """
                ${i.tittle}
                ${i.date}
                ${i.description}
                ${Utils.convertListToString(i.students)}
                ${Utils.convertListToString(i.achievementActivity)}
                ${i.feedback}
            """.trimIndent()

            shareToWhatsApp(
                listImageBitmap,message.trim()
            )

            exportPdf(listImageBitmap, listOf("${i.tittle}","${i.date}","${i.description}",Utils.convertListToString(i.students),Utils.convertListToString(i.achievementActivity),"${i.feedback}"))
        }
    }

    private fun deleteAssessment(id : String){
        assessmentViewModel.deleteAssessment(id)
        assessmentViewModel.message.observe(this){
            when(it){
                is Resource.Success -> {
                    Toast.makeText(this, "Hapus data berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,HomeActivity::class.java))
                    finish()
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

    private fun doDelete(id : String){
        binding.delete?.setOnClickListener {
            val alert = AlertDialog.Builder(this).create()
            val view = AlertComponentBinding.inflate(layoutInflater)
            alert.setView(view.root)
            view.apply {
                tvTittle.text = getString(R.string.alert_tittle)
                tvSubTittle.text = getString(R.string.alert_subtile_delete)
                btnYes.setOnClickListener {
                    deleteAssessment(id)
                }
                btnCancel.setOnClickListener {
                    alert.dismiss()
                }
            }
            alert.show()
        }
    }

    private fun updateAssessment(assessmentRequest: AssessmentRequest){
        binding.favorite?.setOnClickListener {
            assessmentViewModel.updateAssessment(assessmentRequest)
            assessmentViewModel.message.observe(this){
                when(it){
                    is Resource.Success -> {
                        checkIsFavorite(assessmentRequest.favorite!!)
                        Toast.makeText(this, "Berhasil ditambahkan ke favorite", Toast.LENGTH_SHORT).show()
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

    private fun shareToWhatsApp(
        imageUris : ArrayList<Bitmap>, message : String
    ){
        binding.share?.setOnClickListener {
            Utils.checkStoragePermission(imageUris,message,this,this)
            Utils.sendMultipleImagesAndText(imageUris,message,this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 104) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION_WHATSAPP","PERMISSION_ACTIVE")
            } else {
                Log.d("PERMISSION_WHATSAPP","PERMISSION_DENIED")
            }
        }
    }

    private fun exportPdf(bitmaps: List<Bitmap>, texts: List<String>){
        binding.export?.setOnClickListener {
            Utils.exportToPdf(bitmaps,texts,this)
            Toast.makeText(this, "Sukses export pdf", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIsFavorite(isFavorite : Boolean){
        if(isFavorite){
            binding.favorite?.setImageDrawable(
                ContextCompat.getDrawable(this,
                    R.drawable.ic_favorite_active))
        }else{
            binding.favorite?.setImageDrawable(
                ContextCompat.getDrawable(this,
                    R.drawable.ic_favorite_inactive))
        }
    }

    private fun updateDataToEdit(id : String,favorite : Boolean){
        binding.update?.setOnClickListener {
            startActivity(Intent(this,CreateUpdateAsesmentActivity::class.java).also{
                it.putExtra("id",id)
                it.putExtra("favorite",favorite)
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}