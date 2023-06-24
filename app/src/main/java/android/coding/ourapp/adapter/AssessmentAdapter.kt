package android.coding.ourapp.adapter

import android.coding.ourapp.R
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.databinding.ListItemAssessmentShimmerBinding
import android.coding.ourapp.databinding.ListItemNarasiBinding
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class AssessmentAdapter(
    private var data : ArrayList<AssessmentRequest>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val ITEM_ACTUAL = 0
    private val ITEM_SHIMMER = 1

    private var listenerOnClick: ((AssessmentRequest) -> Unit)? = null

    fun setItemClickListener(listener: (AssessmentRequest) -> Unit) {
        listenerOnClick = listener
    }

    inner class AViewHolder(val binding : ListItemNarasiBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data : List<AssessmentRequest>,position : Int){
            if(data.isNotEmpty()){
                binding.apply {
                    cardTittle.text = data[position].tittle
                    cardSubTittle.text = data[position].description
                    if(data[position].image.isNotEmpty()){
                        cardImage.visibility = View.VISIBLE
                        val bytes = Base64.decode(data[position].image[0],Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                        cardImage.setImageBitmap(bitmap)
                    }else{
                        cardImage.visibility = View.GONE
                    }
                    if(data[position].favorite!!){
                        favorite.visibility = View.VISIBLE
                        favorite.setImageResource(R.drawable.ic_favorite_active)
                    }else{
                        favorite.visibility = View.GONE
                    }

                    card.setOnClickListener {
                        listenerOnClick?.invoke(data[position])
                    }
                }
            }
        }
    }


    inner class SViewHolder(val binding : ListItemAssessmentShimmerBinding):RecyclerView.ViewHolder(binding.root){
        fun startShimmerEffect() {
            binding.shimmerLayout.startShimmer()
        }
    }

    fun updateData(newData: ArrayList<AssessmentRequest>) {
        val diffCallback = MyDiffCallback(data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }
    fun getData(): ArrayList<AssessmentRequest> {
        return data
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_ACTUAL -> {
                AViewHolder(ListItemNarasiBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            ITEM_SHIMMER -> {
                SViewHolder(ListItemAssessmentShimmerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AViewHolder) {
           if(data.isNotEmpty()){
               holder.bind(data,position)
           }
        } else if (holder is SViewHolder) {
            holder.startShimmerEffect()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data.isNotEmpty()) {
            ITEM_ACTUAL
        } else {
            ITEM_SHIMMER
        }
    }

    override fun getItemCount(): Int  =  if(data.isEmpty()) 10 else data.size

}


class MyDiffCallback(
    private val oldList: ArrayList<AssessmentRequest>,
    private val newList: ArrayList<AssessmentRequest>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {

        return oldList[oldPosition].id == newList[newPosition].id

    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition] == newList[newPosition]
    }
}