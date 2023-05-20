package android.coding.ourapp.data.repository.asesment

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.datasource.model.AssessmentResponse

interface AssessmentRepository {

    suspend fun getAssessment():Resource<AssessmentResponse>

    suspend fun createAssessment(assessmentRequest: AssessmentRequest): Resource<String>




}