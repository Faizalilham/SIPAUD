package android.coding.ourapp.data.repository.asesment

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.datasource.model.AssessmentResponse
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class AssessmentRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
):AssessmentRepository {

    override  fun getAssessment(): LiveData<Resource<AssessmentResponse>> {
        val assessmentLiveData = MutableLiveData<Resource<AssessmentResponse>>()
        assessmentLiveData.value = Resource.Loading
        try {
           firebaseDatabase.getReference("assessment").addValueEventListener(object : ValueEventListener{
               override fun onDataChange(snapshot: DataSnapshot) {
                   val result = arrayListOf<AssessmentRequest>()

                   if(snapshot.exists()){
                       for(data in snapshot.children){
                           val item = data.getValue(AssessmentRequest::class.java)
                           result.add(item!!)
                       }
                   }
                   assessmentLiveData.value = Resource.Success(AssessmentResponse(result,null))
               }

               override fun onCancelled(error: DatabaseError) {
                   assessmentLiveData.value = Resource.Failure(error.toException())
               }
           })

           return assessmentLiveData
       }catch (e: Exception){
           Log.d("GET ASSESSMENT","${e.message}")
           e.printStackTrace()
           assessmentLiveData.value = Resource.Failure(e)
           return assessmentLiveData
       }
    }

    override fun getByIdAssessment(id: String): LiveData<Resource<AssessmentRequest>> {
        val assessmentLiveData = MutableLiveData<Resource<AssessmentRequest>>()
        assessmentLiveData.value = Resource.Loading
        try{
            firebaseDatabase.reference.child("assessment").child(id).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(AssessmentRequest::class.java)
                    assessmentLiveData.value= Resource.Success(item!!)
                }

                override fun onCancelled(error: DatabaseError) {
                    assessmentLiveData.value = Resource.Failure(error.toException())
                }

            })
        return assessmentLiveData
        }catch (e : Exception){
            Log.d("GET ASSESSMENT","${e.message}")
            e.printStackTrace()
            assessmentLiveData.value = Resource.Failure(e)
            return assessmentLiveData
        }
    }

    override fun deleteAssessment(id: String) : Resource<String>{
        return try {
            firebaseDatabase.reference.child("assessment").child(id).removeValue { error, _ ->
                if(error == null){
                   Resource.Success("")
                }else{
                    Resource.Failure(error.toException())
                }
            }
             return Resource.Success("")
        }catch (e : Exception){
            Log.d("GET ASSESSMENT","${e.message}")
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateAssessment(
        assessmentRequest: AssessmentRequest
    ): Resource<String> {
        return try{
            firebaseDatabase.reference.child("assessment").child(assessmentRequest.id.toString())
                .setValue(assessmentRequest)
                .addOnSuccessListener {
                    Log.d("UPDATE ASSESSMENT","SUCCESS")
                }
                .addOnFailureListener {
                    Resource.Failure(it)
                }
            return Resource.Success("Success update data")
        }catch (e : Exception){
            Log.d("CREATE ASSESSMENT","${e.message}")
            e.printStackTrace()
            Resource.Failure(e)
        }

    }

    override suspend fun createAssessment(
        tittle : String,
        description : String,
        date : String,
        images : ArrayList<String>,
        students : ArrayList<String>,
        achievementActivity : ArrayList<String>,
        feedback : String,
        isFavorite : Boolean,
    ):Resource<String> {
        var resultFix = ""
        var resultErr : Exception = Exception()

        return try{
            val id = firebaseDatabase.reference.push().key.toString()
            firebaseDatabase.getReference("assessment")
                .child(id).setValue(AssessmentRequest(
                    tittle, description, date, students,images,achievementActivity, feedback, isFavorite,id
                ))
                .addOnSuccessListener {
                    resultFix = "Success add data"
                    Log.d("FIX POST",resultFix)
                }
                .addOnFailureListener {
                    resultErr = it
                    Resource.Failure(it)
                    Log.d("BAD POST","${resultErr.message}")
                }
            return if(resultFix != "")  Resource.Failure(resultErr) else  Resource.Success(resultFix)
        }catch (e : Exception){
            Log.d("CREATE ASSESSMENT","${e.message}")
            e.printStackTrace()
            Resource.Failure(e)

        }
    }

    override fun searchAssessment(
        query: String
    ): LiveData<Resource<AssessmentResponse>> {
        val assessmentSearchLiveData = MutableLiveData<Resource<AssessmentResponse>>()
        assessmentSearchLiveData.value = Resource.Loading
        try {
            firebaseDatabase.getReference("assessment")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val result = arrayListOf<AssessmentRequest>()

                        if (snapshot.exists()) {
                            for (data in snapshot.children) {
                                val item = data.getValue(AssessmentRequest::class.java)
//                                if (item != null && Utils.userMatchesSearch(item, query)) {
//                                    result.add(item)
//                                }

                            }
                        }
                        assessmentSearchLiveData.value =
                            Resource.Success(AssessmentResponse(result, null))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        assessmentSearchLiveData.value = Resource.Failure(error.toException())
                    }
                })

            return assessmentSearchLiveData
        }catch (e : Exception){
            Log.d("SEARCH ASSESSMENT","${e.message}")
            e.printStackTrace()
            assessmentSearchLiveData.value = Resource.Failure(e)
            return assessmentSearchLiveData
        }
    }
}