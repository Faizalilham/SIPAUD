package android.coding.ourapp.adapter

import android.coding.ourapp.data.datasource.model.Achievement
import android.coding.ourapp.databinding.ListItemAchievementBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AchievementAdapter(
    private val data : List<Achievement>,
    private val listener : OnClick
):RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(val binding : ListItemAchievementBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
       return AchievementViewHolder(ListItemAchievementBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
       holder.binding.apply {
           tittle.text = data[position].name
           isMuncul.isChecked = data[position].isChecked
           val listChecked = mutableListOf<String>()
           isMuncul.setOnCheckedChangeListener { _, _ ->
               isMuncul.isChecked = !isMuncul.isChecked
               if(isMuncul.isChecked){
                   listChecked.add(data[position].name)
                   listener.onChecked(listChecked)
               }else{
                   listChecked.remove(data[position].name)
                   listener.onChecked(listChecked)
               }
           }

       }
    }

    override fun getItemCount(): Int = data.size


    interface OnClick{
        fun onChecked(name : List<String>)
    }

}