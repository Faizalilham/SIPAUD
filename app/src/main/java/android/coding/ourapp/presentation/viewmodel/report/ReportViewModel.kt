package android.coding.ourapp.presentation.viewmodel.report

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentResponse
import android.coding.ourapp.data.datasource.model.DataReport
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.data.repository.report.ReportRepositoryImpl
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepositoryImpl: ReportRepositoryImpl
): ViewModel() {

    private val  _getAllReport : LiveData<Resource<MutableList<DataReport>>> = reportRepositoryImpl.getAllReport()
    val  getAllReport : LiveData<Resource<MutableList<DataReport>>> = _getAllReport


    private val _message : MutableLiveData<Resource<String>> = MutableLiveData()
    val message : LiveData<Resource<String>> = _message

    fun createReport(
        idStudent : String,
        nameStudent : String,
        tittle : String,
        date : String,
        indicatorAgama : MutableList<String>,
        indicatorMoral : MutableList<String>,
        indicatorPekerti : MutableList<String>,
        images : MutableList<String>,

    ){
        viewModelScope.launch {
            _message.value = Resource.Loading
            val result = reportRepositoryImpl.createReport(
                idStudent,
                nameStudent,tittle,date,indicatorAgama,indicatorMoral,indicatorPekerti,images
            )
            _message.value = result
        }
    }

    fun updateReport(
        idParent : String,
        idChild : String,
        tittle : String,
        date : String,
        indicatorAgama : MutableList<String>,
        indicatorMoral : MutableList<String>,
        indicatorPekerti : MutableList<String>,
        images : MutableList<String>,
        listReport : MutableList<Report>,
        listNarrative : MutableList<Narrative>

        ){
        viewModelScope.launch {
            _message.value = Resource.Loading
            val result = reportRepositoryImpl.updateReport(
                idParent,idChild,tittle,date,indicatorAgama,indicatorMoral,indicatorPekerti,images,listReport,listNarrative
            )
            _message.value = result
        }
    }

    fun getDataReportById(id : String):LiveData<Resource<DataReport>>{
        return reportRepositoryImpl.getReportById(id)
    }

    fun deleteAssessment(id : String,idChild : String){
        viewModelScope.launch {
            _message.value = Resource.Loading
            val result = reportRepositoryImpl.deleteReport(id,idChild)
            _message.value = result

        }
    }

    fun deleteNarrative(id : String){
        viewModelScope.launch {
            _message.value = Resource.Loading
            val result = reportRepositoryImpl.deleteNarrative(id)
            _message.value = result

        }
    }



}