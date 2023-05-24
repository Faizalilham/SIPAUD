package android.coding.ourapp.presentation.ui

import android.os.Bundle
import android.coding.ourapp.R
import android.coding.ourapp.utils.TAG
import android.coding.ourapp.utils.options
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.ak1.pix.helpers.*


class ImageFragment : AppCompatActivity() {

    private val listUri : MutableList<Uri> = mutableListOf()
    private val listPath : MutableList<String> = mutableListOf()
    var i : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_image)
        setupScreen()
        supportActionBar?.hide()
        showCameraFragment()
        i = intent.getStringExtra("id")
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
                        val intent = Intent(this, CreateUpdateAsesmentActivity::class.java)
                        intent.putExtra("list_uri", ArrayList(listUri))
                        if(i != null){
                            intent.putExtra("ids", i)
                        }
                        startActivity(intent)
                        finish()

                    }
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    supportFragmentManager.popBackStack()
                }
            }

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        PixBus.onBackPressedEvent()
    }

}
