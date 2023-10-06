package android.coding.ourapp.adapter

import android.coding.ourapp.databinding.ListItemInstructionBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class InstructionAdapter(
    private val data : Array<String>
):RecyclerView.Adapter<InstructionAdapter.AViewHolder>() {


    inner class AViewHolder(val binding : ListItemInstructionBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AViewHolder {
        return AViewHolder(ListItemInstructionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: AViewHolder, position: Int) {
        holder.binding.apply {
            tvInstruktion.text = data[position]

        }
    }

    override fun getItemCount(): Int = data.size



}