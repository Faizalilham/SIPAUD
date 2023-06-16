package android.coding.ourapp.helperimport android.coding.ourapp.data.repository.student.StudentRepositoryimport android.coding.ourapp.presentation.viewmodel.student.StudentViewModelimport androidx.lifecycle.ViewModelimport androidx.lifecycle.ViewModelProviderclass ViewModelFactory(    private val studentRepository: StudentRepository) : ViewModelProvider.NewInstanceFactory() {    @Suppress("UNCHECKED_CAST")    override fun <T : ViewModel> create(modelClass: Class<T>): T {        if (modelClass.isAssignableFrom(StudentViewModel::class.java)) {            return StudentViewModel(studentRepository) as T        }        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)    }}