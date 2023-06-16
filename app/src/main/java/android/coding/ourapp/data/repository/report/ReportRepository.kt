package android.coding.ourapp.data.repository.report

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.DataReport
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.data.datasource.model.Report
import androidx.lifecycle.LiveData

interface ReportRepository {

    fun getAllReport(): LiveData<Resource<MutableList<DataReport>>>

    fun getReportById(id : String):LiveData<Resource<DataReport>>

    fun createReport(
        idStudent:String,
        studentName : String,
        reportName : String,
        date : String,
        indicator : MutableList<String>,
        images : MutableList<String>,
    ): Resource<String>

    fun updateReport(
        idParent : String,
        idChild : String,
        reportName : String,
        date : String,
        indicator : MutableList<String>,
        images : MutableList<String>,
        listReport : MutableList<Report>,
        listNarrative : MutableList<Narrative>
    ): Resource<String>

    fun deleteReport(
        idParent : String,
        idChild : String,
    ): Resource<String>

    fun deleteNarrative(
        idParent : String,
    ): Resource<String>

}