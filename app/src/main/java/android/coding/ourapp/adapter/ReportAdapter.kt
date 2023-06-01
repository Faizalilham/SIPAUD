import android.coding.ourapp.R
import android.coding.ourapp.adapter.OnTouchHelper
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.databinding.ListItemDailyReportBinding
import android.coding.ourapp.utils.Utils
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ReportAdapter(
    private val context : Context,
    private var data : MutableList<Report>): RecyclerView.Adapter<RecyclerViewHolder>() {

    fun updateData(newData: MutableList<Report>) {
        val diffCallback = ReportDiffCallback(data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = ListItemDailyReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecyclerViewHolder(view).also { viewHolder ->
            viewHolder.foregroundKnobLayout.setOnClickListener {
                val position = viewHolder.adapterPosition

            }

        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerViewHolder, position: Int) {
        viewHolder.bind(data, position,context)
    }

    override fun getItemCount(): Int = data.size
}

class RecyclerViewHolder(val binding : ListItemDailyReportBinding): RecyclerView.ViewHolder(binding.root), OnTouchHelper.SwipeViewHolder {

    override val foregroundKnobLayout: ViewGroup = binding.foregroundKnobLayout
    override val backgroundLeftButtonLayout: ViewGroup = binding.swipeLayout.backgroundLeftButtonLayout
    override val canRemoveOnSwipingFromRight: Boolean get() = true

    val btnDelete: ImageButton = binding.swipeLayout.btnDelete
    val btnUpdate: ImageButton = binding.swipeLayout.btnUpdate

    fun bind(data : MutableList<Report>, position: Int,context : Context) {
        binding.apply {
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