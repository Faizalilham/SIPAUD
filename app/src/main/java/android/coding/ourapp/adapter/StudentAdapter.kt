package android.coding.ourapp.adapter

import android.annotation.SuppressLint
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
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class StudentAdapter(private val studentViewModel: StudentViewModel) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    private val listStudent = ArrayList<Student>()
    private var selectedItems = HashSet<Int>()

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
        holder.bind(listStudent[position])
    }

    override fun getItemCount(): Int = listStudent.size

    inner class StudentViewHolder(private val binding: ListItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context: Context = binding.root.context

        fun bind(student: Student) {
            with(binding) {
                tvTittle.text = student.nameStudent
                tvCompany.text = student.company
                tvGroup.text = student.group

                btnAddReport.setOnClickListener {
                    val intent = Intent(context, CreateUpdateReportActivity::class.java)
                    intent.putExtra(EXTRA_NAME, student.nameStudent)
                    intent.putExtra(EXTRA_ID, student.id)
                    context.startActivity(intent)
                }

                btnHistoryReport.setOnClickListener {
                    val intent = Intent(context, ReportActivity::class.java)
                    intent.putExtra(NAME_STUDENT, student.nameStudent)
                    intent.putExtra(ID_STUDENT, student.id)
                    context.startActivity(intent)
                }

                btnMenus.setOnClickListener {
                    val popupMenu = PopupMenu(context, btnMenus)
                    popupMenu.menuInflater.inflate(R.menu.menu_dropdown, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_update -> {
                                val intent =
                                    Intent(context, CreateUpdateStudentActivity::class.java)
                                intent.putExtra(EXTRA_STUDENT, student)
                                context.startActivity(intent)
                                true
                            }
                            R.id.menu_delete -> {
                                showConfirmationDialog(student)
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun showConfirmationDialog(student: Student) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_component, null)
            val alertDialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            val titleTextView = dialogView.findViewById<TextView>(R.id.tv_tittle)
            val messageTextView = dialogView.findViewById<TextView>(R.id.tv_subTittle)

            titleTextView.text = "Peringatan !!!"
            messageTextView.text = "Apakah Anda Yakin Ingin Menghapus Data Siswa Ini?"

            val btnConfirm = dialogView.findViewById<Button>(R.id.btn_yes)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

            btnConfirm.setOnClickListener {
                alertDialog.dismiss()
                studentViewModel.deleteData(student)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}
