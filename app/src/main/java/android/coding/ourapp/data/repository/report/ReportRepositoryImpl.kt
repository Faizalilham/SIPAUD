package android.coding.ourapp.data.repository.report

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.datasource.model.DataReport
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.utils.Utils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject


class ReportRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
):ReportRepository {

    override fun getAllReport(): LiveData<Resource<MutableList<DataReport>>> {
        val assessmentLiveData = MutableLiveData<Resource<MutableList<DataReport>>>()
        assessmentLiveData.value = Resource.Loading
        try {
            firebaseDatabase.getReference("report").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = mutableListOf<DataReport>()

                    if(snapshot.exists()){
                        for(data in snapshot.children){
                            val item = data.getValue(DataReport::class.java)
                            result.add(item!!)
                            
                        }
                    }
                    assessmentLiveData.value = Resource.Success(result)
                }

                override fun onCancelled(error: DatabaseError) {
                    assessmentLiveData.value = Resource.Failure(error.toException())
                }
            })

            return assessmentLiveData
        }catch (e: Exception){
            Log.d("GET REPORT","${e.message}")
            e.printStackTrace()
            assessmentLiveData.value = Resource.Failure(e)
            return assessmentLiveData
        }
    }

    override fun getReportById(id: String):LiveData<Resource<DataReport>> {
        val assessmentLiveData = MutableLiveData<Resource<DataReport>>()
        assessmentLiveData.value = Resource.Loading
        try{
            firebaseDatabase.reference.child("report").child(id).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(DataReport::class.java)
                    assessmentLiveData.value= Resource.Success(item!!)
                }

                override fun onCancelled(error: DatabaseError) {
                    assessmentLiveData.value = Resource.Failure(error.toException())
                }

            })
            return assessmentLiveData
        }catch (e : Exception){
            Log.d("GET REPORT","${e.message}")
            e.printStackTrace()
            assessmentLiveData.value = Resource.Failure(e)
            return assessmentLiveData
        }
    }

    override fun createReport(
        studentName: String,
        reportName: String,
        date: String,
        indicator: MutableList<String>,
        images: MutableList<String>
    ): Resource<String> {
        var resultFix = ""
        var resultErr : Exception = Exception()
        val idChild =  firebaseDatabase.reference.push().key.toString()
        val dataReport = mutableListOf<Report>(
            Report(id=idChild,reportName,date,Utils.getMonthFromStringDate(date),indicator,images)
        )

        return try{
            val id = firebaseDatabase.reference.push().key.toString()
            firebaseDatabase.getReference("report")
                .child(id).setValue(
                    DataReport(id=id,studentName=studentName,reports = dataReport)
                )
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

    override fun updateReport(
        idParent : String,
        idChild : String,
        reportName: String,
        date: String,
        indicator: MutableList<String>,
        images: MutableList<String>,
        listReport : MutableList<Report>
    ): Resource<String> {
       return if(listReport.isEmpty()){
           firebaseDatabase.reference.child("report").child(idParent).child("reports").child(idChild)
               .setValue(Report(reportName = reportName, reportDate = date, month = Utils.getMonthFromStringDate(date), indicator = indicator,images = images))
               .addOnSuccessListener {
                   Log.d("UPDATE REPORT","SUCCESS")
               }
               .addOnFailureListener {
                   Resource.Failure(it)
               }
           Resource.Success("Success update data")
       }else{
           firebaseDatabase.reference.child("report").child(idParent).child("reports")
               .setValue(listReport)
               .addOnSuccessListener {
                   Log.d("UPDATE REPORT","SUCCESS")
               }
               .addOnFailureListener {
                   Resource.Failure(it)
               }
           return Resource.Success("Success update data")
       }
    }

    override fun deleteReport(idParent: String,idChild:String) : Resource<String>{
        return try {
            firebaseDatabase.reference.child("report").child(idParent).child("reports").child(idChild).removeValue { error, _ ->
                if(error == null){
                    Resource.Success("Success delete data")
                }else{
                    Resource.Failure(error.toException())
                }
            }
            return Resource.Success("")
        }catch (e : Exception){
            Log.d("DELETE REPORT","${e.message}")
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

}