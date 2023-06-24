package android.coding.ourapp.presentation.ui

import android.Manifest
import android.app.Activity
import android.coding.ourapp.R
import android.coding.ourapp.presentation.viewmodel.PermissionViewModel
import android.coding.ourapp.utils.TAG
import android.coding.ourapp.utils.options
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.pix.helpers.*


@AndroidEntryPoint
class ImageFragment : AppCompatActivity() {

    private val listUri : MutableList<Uri> = mutableListOf()
    private val listPath : MutableList<String> = mutableListOf()
    var i : String? = null
    private val permission by viewModels<PermissionViewModel>()

    @RequiresApi(Build.VERSION_CODES.M)
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
                        val data = Intent()
                        data.putParcelableArrayListExtra("list_uri", ArrayList(listUri))
                        setResult(Activity.RESULT_OK, data)
                        finish()
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission() {
        requestPermissions(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,), 201)
    }

    @RequiresApi(Build.VERSION_CODES.M)
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



    override fun onBackPressed() {
        super.onBackPressed()
        PixBus.onBackPressedEvent()
    }

}
