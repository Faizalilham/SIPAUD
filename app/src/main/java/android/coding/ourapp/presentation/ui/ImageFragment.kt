package android.coding.ourapp.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.coding.ourapp.R
import android.coding.ourapp.presentation.viewmodel.PermissionViewModel
import android.coding.ourapp.utils.TAG
import android.coding.ourapp.utils.options
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.pix.helpers.*


@AndroidEntryPoint
class ImageFragment : AppCompatActivity() {

    private val listUri : MutableList<Uri> = mutableListOf()
    private val listPath : MutableList<String> = mutableListOf()
    var i : String? = null
    private val permission by viewModels<PermissionViewModel>()
    private var nameStudent : String? = null
    private var idStudent : String? = null
    private var idParent : String? = null
    private var idChild : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_image)
        setupScreen()
        supportActionBar?.hide()
        showCameraFragment()
        i = intent.getStringExtra("id")
        permission.getPermissionKey().observe(this){
            if(!it){
                checkPermission()
            }
        }

        nameStudent = intent.getStringExtra(EXTRA_NAME)
        idStudent = intent.getStringExtra(EXTRA_ID)

        idParent = intent.getStringExtra(EXTRA_ID_PARENT)
        idChild = intent.getStringExtra(EXTRA_ID_CHILD)
       

    }

    private fun showCameraFragment() {
        addPixToActivity(R.id.container, options) {
            when (it.status) {
                PixEventCallback.Status.SUCCESS -> {
                    it.data.forEach { uri ->
                        Log.d(TAG, "showCameraFragment: ${uri.path}")
                    }
                    listUri.addAll(it.data)
                    Log.d(TAG,"$listPath $listUri uhuy")
                    if(listUri.isNotEmpty() && listUri.size != 4){
                        startActivity(Intent(this, CreateUpdateReportActivity::class.java).also {
                            it.putParcelableArrayListExtra("list_uri", ArrayList(listUri))
                            it.putExtra(EXTRA_NAME, nameStudent)
                            it.putExtra(EXTRA_ID, idStudent)
                            it.putExtra(EXTRA_ID_PARENT, idParent)
                            it.putExtra(EXTRA_ID_CHILD, idChild)
                            finishAffinity()
                            finish()
                        })



                    }else{
                        finish()
                    }
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    supportFragmentManager.popBackStack()
                }
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun requestLocationPermission() {
        requestPermissions(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,), 201)
    }

    @SuppressLint("InlinedApi")
    private fun checkPermission():Boolean{
        val permissionCheck = checkSelfPermission(Manifest.permission.CAMERA)
        val permissionCheck3 = checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        Log.d("PERMISSION","$permissionCheck $permissionCheck3" )
        return if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestLocationPermission()
            permission.getPermissionKey().observe(this){
                if(!it){
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${applicationContext.packageName}")
                    startActivity(intent)
                }
            }

            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("CUCUY","$grantResults")
        when (requestCode) {
            201 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[0] == Manifest.permission.CAMERA
                ) {
                    permission.setPermissionKey(true)
                    Log.d("PERMISSION","AKSES DIIZINKAN")
                } else {
                    Log.d("PERMISSION","AKSES DITOLAK")
                }
            }
            else -> {
                Log.d("PERMISSION","REQUEST CODE NOT SAME")
            }
        }
    }



    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        PixBus.onBackPressedEvent()
    }

    companion object{
        const val EXTRA_NAME = "name_student"
        const val EXTRA_ID = "id_student"
        const val EXTRA_ID_CHILD = "id_child"
        const val EXTRA_ID_PARENT = "id_parent"
    }

}
