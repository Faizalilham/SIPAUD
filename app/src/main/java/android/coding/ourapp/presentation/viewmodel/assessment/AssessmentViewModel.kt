package android.coding.ourapp.presentation.viewmodel.assessment

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.datasource.model.AssessmentResponse
import android.coding.ourapp.data.repository.asesment.AssessmentRepositoryImpl
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentViewModel @Inject constructor(
   private val  assessmentRepositoryImpl: AssessmentRepositoryImpl
):ViewModel() {

    private val  _getAssessment : LiveData<Resource<AssessmentResponse>> = assessmentRepositoryImpl.getAssessment()
    val  getAssessment : LiveData<Resource<AssessmentResponse>> = _getAssessment


    private val _message : MutableLiveData<Resource<String>> = MutableLiveData()
    val message : LiveData<Resource<String>> = _message

    fun createAssessment(
        tittle : String,
        description : String,
        date : String,
        images : ArrayList<String>,
        students : ArrayList<String>,
        achievementActivity : ArrayList<String>,
        feedback : String,
        isFavorite : Boolean,
    ){
        viewModelScope.launch {
           _message.value = Resource.Loading
           val result = assessmentRepositoryImpl.createAssessment(
               tittle, description, date, students,images,achievementActivity, feedback, isFavorite
           )
            _message.value = result

        }
    }

    fun updateAssessment(assessmentRequest: AssessmentRequest){
        viewModelScope.launch {
            _message.value = Resource.Loading
            val result = assessmentRepositoryImpl.updateAssessment(assessmentRequest)
            _message.value = result
        }
    }

    fun deleteAssessment(id : String){
        viewModelScope.launch {
            _message.value = Resource.Loading
            val result = assessmentRepositoryImpl.deleteAssessment(id)
            _message.value = result

        }
    }

    fun getDataById(id : String):LiveData<Resource<AssessmentRequest>>{
        return assessmentRepositoryImpl.getByIdAssessment(id)
    }

    fun searchAssessment(query : String):LiveData<Resource<AssessmentResponse>>{
        return assessmentRepositoryImpl.searchAssessment(query)
    }




}