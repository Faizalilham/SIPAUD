package android.coding.ourapp.adapter

import android.coding.ourapp.R
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.databinding.ListItemDailyReportBinding
import android.coding.ourapp.utils.Utils
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(
    private val context : Context,
    private var data : MutableList<Report>
) :RecyclerView.Adapter<ReportAdapter.ReportViewHolder>(){

    fun updateData(newData: MutableList<Report>) {
        val diffCallback = ReportDiffCallback(data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ReportViewHolder(val binding : ListItemDailyReportBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
       return ReportViewHolder(ListItemDailyReportBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.binding.apply {
            tvTittle.text = data[position].reportName
            tvDate.text = data[position].reportDate
            val listBitmap = mutableListOf<Bitmap>()
            if(data[position].images.isNotEmpty()){
               for(i in data[position].images){
                   listBitmap.add(Utils.convertStringToBitmap(i))
               }
            }
            if(listBitmap.isNotEmpty()){
                Utils.showImageAssessment(true, ArrayList(listBitmap),null,this,context)
            }
            tvActivity.text = Utils.convertListToString(data[position].indicator)

            linearTittle.setOnClickListener {
                if (expandableLayout.isExpanded) {
                    expandableLayout.collapse()
                    arrowTittle.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    expandableLayout.expand()
                    arrowTittle.setImageResource(R.drawable.ic_arrow_up)
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size
}

class ReportDiffCallback(
    private val oldList: MutableList<Report>,
    private val newList: MutableList<Report>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {

        return oldList[oldPosition].reportName == newList[newPosition].reportName

    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition] == newList[newPosition]
    }
}