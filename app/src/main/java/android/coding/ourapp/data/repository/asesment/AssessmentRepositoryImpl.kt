package android.coding.ourapp.data.repository.asesment

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.datasource.model.AssessmentResponse
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class AssessmentRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
):AssessmentRepository {

    override suspend fun getAssessment(): Resource<AssessmentResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun createAssessment(assessmentRequest: AssessmentRequest):Resource<String> {
        var resultFix = ""
        var resultErr : Exception = Exception()

        return try{
            firebaseDatabase.getReference("assessment")
                .child(firebaseDatabase.reference.push().key.toString()).setValue(assessmentRequest)
                .addOnSuccessListener {
                   resultFix = "Success add data"
                    Log.d("FIX POST",resultFix)
                }
                .addOnFailureListener {
                    resultErr = it
                    Resource.Failure(it)
                    Log.d("BAD POST","${resultErr.message}")
                }
            return if(resultFix != "") Resource.Success(resultFix) else  Resource.Failure(resultErr)
        }catch (e : Exception){
            Log.d("CREATE ASSESSMENT","${e.message}")
            e.printStackTrace()
            Resource.Failure(e)

        }
    }
}