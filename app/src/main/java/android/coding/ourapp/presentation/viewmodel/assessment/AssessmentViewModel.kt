package android.coding.ourapp.presentation.viewmodel.assessment

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
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

    private val _message : MutableLiveData<Resource<String>> = MutableLiveData()
    val message : LiveData<Resource<String>> = _message


    fun createAssessment(assessmentRequest: AssessmentRequest){
        viewModelScope.launch {
           _message.value = Resource.Loading
           val result = assessmentRepositoryImpl.createAssessment(assessmentRequest)
            _message.value = result

        }
    }



}