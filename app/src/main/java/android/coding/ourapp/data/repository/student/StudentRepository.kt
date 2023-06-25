package android.coding.ourapp.data.repository.studentimport android.coding.ourapp.data.Resourceimport android.coding.ourapp.data.datasource.firebase.FirebaseHelperimport android.coding.ourapp.data.datasource.model.*import android.coding.ourapp.utils.Utilsimport android.util.Logimport androidx.lifecycle.LiveDataimport androidx.lifecycle.MutableLiveDataimport com.google.firebase.database.*@Suppress("ControlFlowWithEmptyBody")class StudentRepository(private val firebaseHelper: FirebaseHelper) {    fun getData(): LiveData<Resource<List<Student>>> {        val studentLiveData = MutableLiveData<Resource<List<Student>>>()        firebaseHelper.dbReference.addValueEventListener(object : ValueEventListener {            override fun onDataChange(snapshot: DataSnapshot) {                val dataList = mutableListOf<Student>()                for (dataSnapshot in snapshot.children) {                    val data = dataSnapshot.getValue(Student::class.java)                    data?.let {                        dataList.add(it)                    }                }                studentLiveData.value = Resource.Success(dataList)            }            override fun onCancelled(error: DatabaseError) {                studentLiveData.value = Resource.Failure(error.toException())            }        })        return studentLiveData    }    fun addData(        nama: String,        sekolah: String,        kelas: String,        onComplete: () -> Unit,        onFailure: (String) -> Unit    ) {        val id = firebaseHelper.dbReference.push().key        id?.let {            val data = Student(id, nama, sekolah, kelas)            firebaseHelper.dbReference.child(it).setValue(data)                .addOnCompleteListener { task ->                    if (task.isSuccessful) {                        onComplete()                    } else {                        onFailure(task.exception?.message ?: "Unknown error")                    }                }                .addOnFailureListener { exception ->                    onFailure(exception.message ?: "Unknown error")                }        }    }    fun updateData(student: Student) {        val dataRef = firebaseHelper.dbReference.child(student.id ?: "")        val newStudent = Student(student.id, student.nameStudent, student.company, student.group)        dataRef.setValue(newStudent)            .addOnCompleteListener { task ->                if (task.isSuccessful) {                    Log.d("UpdateData", "Success Updating Data")                } else {                }            }            .addOnFailureListener { exception ->                Log.e("UpdateData", "Error updating data: ${exception.message}")            }    }    fun deleteData(student: Student) {        student.id?.let {            firebaseHelper.dbReference.child(it).removeValue()        }    }    fun searchData(        query: String    ):  LiveData<Resource<List<Student>>> {        val studentLiveData = MutableLiveData<Resource<List<Student>>>()        studentLiveData.value = Resource.Loading        try {            firebaseHelper.dbReference                .addValueEventListener(object : ValueEventListener {                    override fun onDataChange(snapshot: DataSnapshot) {                        val result = arrayListOf<Student>()                        if (snapshot.exists()) {                            for (data in snapshot.children) {                                val item = data.getValue(Student::class.java)                                if (item != null && Utils.userMatchesSearch(item, query)) {                                    result.add(item)                                }                            }                        }                        studentLiveData.value =                            Resource.Success(result)                    }                    override fun onCancelled(error: DatabaseError) {                        studentLiveData.value = Resource.Failure(error.toException())                    }                })            return studentLiveData        }catch (e : Exception){            Log.d("SEARCH ASSESSMENT","${e.message}")            e.printStackTrace()            studentLiveData.value = Resource.Failure(e)            return studentLiveData        }    }}