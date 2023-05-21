package android.coding.ourapp.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.coding.ourapp.R
import android.coding.ourapp.adapter.MultipleImageAdapter
import android.coding.ourapp.utils.TAG
import android.coding.ourapp.utils.Utils
import android.coding.ourapp.utils.fragmentBody
import android.coding.ourapp.utils.options
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.models.SlideModel
import io.ak1.pix.helpers.*


class ImageFragment : AppCompatActivity() {
//    private val resultsFragment = ResultsFragment {
//
//    }
    private val list : MutableList<Uri> = mutableListOf()
     // Create image list


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_image)
        Utils.language(this)
        setupScreen()
        supportActionBar?.hide()
        showCameraFragment()
    }

    private fun showCameraFragment() {
        addPixToActivity(R.id.container, options) {
            when (it.status) {
                PixEventCallback.Status.SUCCESS -> {
                    it.data.forEach {
                        Log.d(TAG, "showCameraFragment: ${it.path}")
                    }
                    list.addAll(it.data)

                    Log.d(TAG,"${list}")
                    if(list.size == 3){
                        finish()
                        Toast.makeText(this, "${list}", Toast.LENGTH_SHORT).show()
                    }
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    supportFragmentManager.popBackStack()
                }
            }

        }
    }

//    private fun showResultsFragment() {
//        showStatusBar()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, resultsFragment).commit()
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        PixBus.onBackPressedEvent()
    }

}

//class ResultsFragment(private val clickCallback: View.OnClickListener) : Fragment() {
//    private val customAdapter = MultipleImageAdapter()
//    fun setList(list: List<Uri>) {
//        customAdapter.apply {
//            this.list.clear()
//            this.list.addAll(list)
//            notifyDataSetChanged()
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View = fragmentBody(requireActivity(), customAdapter, clickCallback)
//
//}