package android.coding.ourapp.presentation.ui

import android.coding.ourapp.R
import android.coding.ourapp.adapter.AchievementActivityAdapter
import android.coding.ourapp.databinding.ActivityAchievementBinding
import android.coding.ourapp.presentation.viewmodel.AchievementViewModel
import android.coding.ourapp.utils.Utils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.leo.searchablespinner.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AchievementActivity : AppCompatActivity() {
    private var _binding : ActivityAchievementBinding? = null
    private val binding get() = _binding!!

    private val listAchievementActivity = arrayListOf<String>()
    private val listAchievementAgama = arrayListOf<String>()
    private val listAchievementMoral = arrayListOf<String>()
    private val listAchievementPekerti = arrayListOf<String>()
    private lateinit var searchableSpinnerFrom : SearchableSpinner
    private lateinit var adapterAchievementActivityAdapter: AchievementActivityAdapter
    private val achievementActivityViewModel by viewModels<AchievementViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAchievementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchableSpinnerFrom = SearchableSpinner(this)
        listAchievementAgama.addAll(resources.getStringArray(R.array.agama))
        listAchievementMoral.addAll(resources.getStringArray(R.array.moral))
        listAchievementPekerti.addAll(resources.getStringArray(R.array.pekerti))
        selectSpinner()
        doSaveToLocal()
        back()
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
//                    listAchievementActivity.removeAt(data)
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
            layoutManager = LinearLayoutManager(this@AchievementActivity)
        }
    }

    private fun back(){ binding.imageBack.setOnClickListener{finish()}}

    private fun doSaveToLocal(){
       val name =  intent.getStringExtra(CreateUpdateReportActivity.EXTRA_NAME)
       val id =  intent.getStringExtra(CreateUpdateReportActivity.EXTRA_ID)
        binding.btnSave.setOnClickListener {
            if(listAchievementActivity.isNotEmpty()){
                achievementActivityViewModel.setAchievementKey(listAchievementActivity.toList())


                startActivity(Intent(this,CreateUpdateReportActivity::class.java).also{
                    it.putExtra(CreateUpdateReportActivity.EXTRA_NAME, name)
                    it.putExtra(CreateUpdateReportActivity.EXTRA_ID, id)
                    finish()
                })
                Log.d("Hahai","$listAchievementActivity")
            }else{
                Toast.makeText(this, "Minimal ada 1 pencapaian siswa yang akan direkam", Toast.LENGTH_SHORT).show()
            }
        }
    }


}