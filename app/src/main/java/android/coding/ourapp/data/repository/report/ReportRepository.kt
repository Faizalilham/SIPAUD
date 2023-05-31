package android.coding.ourapp.data.repository.report

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.DataReport
import androidx.lifecycle.LiveData

interface ReportRepository {

    fun getAllReport(): LiveData<Resource<MutableList<DataReport>>>

    fun getReportById(id : String):LiveData<Resource<DataReport>>

    fun createReport(
        name : String,
        date : String,
        indicator : MutableList<String>,
        images : MutableList<String>,
    ): Resource<String>

}