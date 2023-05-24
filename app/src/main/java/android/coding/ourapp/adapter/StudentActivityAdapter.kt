package android.coding.ourapp.adapter



import android.coding.ourapp.databinding.ListItemStudentsActivityBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StudentActivityAdapter (
    private val data : ArrayList<String>,
    private val listener : OnClick
):RecyclerView.Adapter<StudentActivityAdapter.AViewHolder>() {

    inner class AViewHolder(val binding : ListItemStudentsActivityBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AViewHolder {
        return AViewHolder(ListItemStudentsActivityBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: AViewHolder, position: Int) {
        holder.binding.apply {
            tvStudentActivity.text = data[position]
            imageDelete.setOnClickListener {
                listener.onDelete(position,data[position])
            }
        }
    }

    override fun getItemCount(): Int = data.size

    interface OnClick{
        fun onDelete(data : Int,student : String)
    }
}