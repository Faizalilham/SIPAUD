package android.coding.ourapp.adapterimport android.coding.ourapp.Rimport android.coding.ourapp.data.datasource.model.Studentimport android.coding.ourapp.databinding.ListItemStudentBindingimport android.coding.ourapp.helper.StudentDiffCallbackimport android.coding.ourapp.presentation.ui.CreateUpdateStudentActivityimport android.coding.ourapp.presentation.ui.CreateUpdateStudentActivity.Companion.EXTRA_STUDENTimport android.coding.ourapp.presentation.ui.StudentsActivityimport android.content.Contextimport android.content.Intentimport android.graphics.Colorimport android.view.LayoutInflaterimport android.view.ViewGroupimport android.widget.Buttonimport android.widget.TextViewimport androidx.appcompat.app.AlertDialogimport androidx.core.content.ContextCompatimport androidx.recyclerview.widget.DiffUtilimport androidx.recyclerview.widget.RecyclerViewclass StudentAdapter(private val context: Context) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {    private val listStudent = ArrayList<Student>()    private var selectedItems = HashSet<Int>()    private var onDeleteClickListener: OnDeleteClickListener? = null    init {        selectedItems = (context as? StudentsActivity)?.selectedItems ?: HashSet()    }    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {        onDeleteClickListener = listener    }    fun setListStudent(listStudent: List<Student>) {        val diffCallback = StudentDiffCallback(this.listStudent, listStudent)        val diffResult = DiffUtil.calculateDiff(diffCallback)        this.listStudent.clear()        this.listStudent.addAll(listStudent)        diffResult.dispatchUpdatesTo(this)        (context as? StudentsActivity)?.toggleDeleteButtonVisibility()    }    fun getListStudent(): List<Student> {        return listStudent    }    fun toggleSelection(position: Int) {        if (selectedItems.contains(position)) {            selectedItems.remove(position)        } else {            selectedItems.add(position)        }        notifyItemChanged(position)        (context as? StudentsActivity)?.toggleDeleteButtonVisibility()    }    fun getSelectedItems(): Set<Int> {        return selectedItems        print("NGENTO $selectedItems")    }    override fun onCreateViewHolder(        parent: ViewGroup,        viewType: Int    ): StudentAdapter.StudentViewHolder {        val binding =            ListItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)        return StudentViewHolder(binding)    }    override fun onBindViewHolder(holder: StudentAdapter.StudentViewHolder, position: Int) {        holder.bind(listStudent[position], position, selectedItems.contains(position))    }    override fun getItemCount(): Int = listStudent.size    inner class StudentViewHolder(private val binding: ListItemStudentBinding) :        RecyclerView.ViewHolder(binding.root) {        fun bind(student: Student, position: Int, isSelected: Boolean) {            with(binding) {                tvTittle.text = student.nameStudent                tvCompany.text = student.company                tvGroup.text = student.group                if (adapterPosition in selectedItems) {                    itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.card_selected_background)                } else {                    itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.card)                }                itemView.setOnClickListener {                    toggleSelection(adapterPosition)                }                btnDelete.setOnClickListener {                    showAlertDelete(itemView.context, student)                }                btnUpdate.setOnClickListener {                    val intent = Intent(it.context, CreateUpdateStudentActivity::class.java)                    intent.putExtra(EXTRA_STUDENT, student)                    it.context.startActivity(intent)                }            }        }    }    interface OnDeleteClickListener {        fun onDeleteClick(student: Student)    }    private fun showAlertDelete(context: Context, student: Student) {        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_component, null)        val alertDialogBuilder = AlertDialog.Builder(context)            .setView(dialogView)        val alertDialog = alertDialogBuilder.create()        val titleTextView = dialogView.findViewById<TextView>(R.id.tv_tittle)        val messageTextView = dialogView.findViewById<TextView>(R.id.tv_subTittle)        titleTextView.text = "Konfirmasi"        messageTextView.text = "Apakah Anda yakin ingin menghapus data ini?"        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)        btnYes.setOnClickListener {            onDeleteClickListener?.onDeleteClick(student)            alertDialog.dismiss()        }        btnCancel.setOnClickListener {            alertDialog.dismiss()        }        alertDialog.show()    }}