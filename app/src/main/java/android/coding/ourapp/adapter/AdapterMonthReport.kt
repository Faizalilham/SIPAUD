package android.coding.ourapp.adapter

import android.coding.ourapp.data.datasource.model.Month
import android.coding.ourapp.databinding.ListItemReportBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterMonthReport(
    private val data : MutableList<Month>,
    private val listener : OnClick
) : RecyclerView.Adapter<AdapterMonthReport.MonthViewHolder>() {

    inner class MonthViewHolder(val binding : ListItemReportBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        return MonthViewHolder(ListItemReportBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.binding.apply {
            tittle.text = data[position].name
            subTittle.text = data[position].count.toString()
            card.setCardBackgroundColor(data[position].background)
            card.setOnClickListener {
                listener.onDetail(data[position].id)
            }

        }
    }

    override fun getItemCount(): Int  = data.size

    interface OnClick{
        fun onDetail(id : String)
    }
}