package android.coding.ourapp.data.repository.asesment

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.datasource.model.AssessmentResponse
import androidx.lifecycle.LiveData

interface AssessmentRepository {

    fun getAssessment(): LiveData<Resource<AssessmentResponse>>

    fun getByIdAssessment(id : String): LiveData<Resource<AssessmentRequest>>

    fun deleteAssessment(id : String) : Resource<String>

    suspend fun updateAssessment(
        assessmentRequest: AssessmentRequest
    ): Resource<String>

    suspend fun createAssessment(
        tittle : String,
        description : String,
        date : String,
        images : ArrayList<String>,
        students : ArrayList<String>,
        achievementActivity : ArrayList<String>,
        feedback : String,
        isFavorite : Boolean,
    ): Resource<String>

    fun searchAssessment(query : String):LiveData<Resource<AssessmentResponse>>

}