
package android.coding.ourapp.adapter

import android.coding.ourapp.R
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.databinding.ListItemStudentBinding
import android.coding.ourapp.helper.StudentDiffCallback
import android.coding.ourapp.presentation.ui.CreateUpdateReportActivity
import android.coding.ourapp.presentation.ui.CreateUpdateReportActivity.Companion.EXTRA_ID
import android.coding.ourapp.presentation.ui.CreateUpdateReportActivity.Companion.EXTRA_NAME
import android.coding.ourapp.presentation.ui.CreateUpdateStudentActivity
import android.coding.ourapp.presentation.ui.CreateUpdateStudentActivity.Companion.EXTRA_STUDENT
import android.coding.ourapp.presentation.ui.ReportActivity
import android.coding.ourapp.presentation.ui.ReportActivity.Companion.ID_STUDENT
import android.coding.ourapp.presentation.ui.ReportActivity.Companion.NAME_STUDENT
import android.coding.ourapp.presentation.viewmodel.student.StudentViewModel
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class StudentAdapter(private val studentViewModel: StudentViewModel) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    private val listStudent = ArrayList<Student>()
    private var selectedItems = HashSet<Int>()
//    private var onDeleteClickListener: OnDeleteClickListener? = null

//    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
//        onDeleteClickListener = listener
//    }

    fun setListStudent(listStudent: List<Student>) {
        val diffCallback = StudentDiffCallback(this.listStudent, listStudent)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listStudent.clear()
        this.listStudent.addAll(listStudent)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentAdapter.StudentViewHolder {
        val binding =
            ListItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentAdapter.StudentViewHolder, position: Int) {
        holder.bind(listStudent[position], position, selectedItems.contains(position))

    }

    override fun getItemCount(): Int = listStudent.size

    inner class StudentViewHolder(private val binding: ListItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student, position: Int, isSelected: Boolean) {
            with(binding) {
                tvTittle.text = student.nameStudent
                tvCompany.text = student.company
                tvGroup.text = student.group

                btnAddReport.setOnClickListener {
                    val intent = Intent(it.context, CreateUpdateReportActivity::class.java)
                    intent.putExtra(EXTRA_NAME, student.nameStudent)
                    intent.putExtra(EXTRA_ID, student.id)
                    it.context.startActivity(intent)
                }

                btnHistoryReport.setOnClickListener {
                    val intent = Intent(it.context, ReportActivity::class.java)
                    intent.putExtra(NAME_STUDENT,student.nameStudent)
                    intent.putExtra(ID_STUDENT,student.id)
                    it.context.startActivity(intent)
                }

                btnMenus.setOnClickListener {
                    val popupMenu = PopupMenu(it.context, btnMenus)
                    popupMenu.menuInflater.inflate(R.menu.menu_dropdown, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_update -> {
                                val intent =
                                    Intent(it.context, CreateUpdateStudentActivity::class.java)
                                intent.putExtra(EXTRA_STUDENT, student)
                                it.context.startActivity(intent)
                                true
                            }
                            R.id.menu_delete -> {
                                val student = listStudent[position]
                                studentViewModel.deleteData(student)
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                }
            }
        }
    }
//
//    interface OnDeleteClickListener {
//        fun onDeleteClick(student: Student)
//    }
}

