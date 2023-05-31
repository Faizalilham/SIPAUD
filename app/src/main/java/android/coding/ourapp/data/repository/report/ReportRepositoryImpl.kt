package android.coding.ourapp.data.repository.report

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.datasource.model.DataReport
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
        name: String,
        date: String,
        indicator: MutableList<String>,
        images: MutableList<String>
    ): Resource<String> {
        var resultFix = ""
        var resultErr : Exception = Exception()
        val dataReport = mutableListOf<Report>(
            Report(name,date,indicator,images)
        )

        return try{
            val id = firebaseDatabase.reference.push().key.toString()
            firebaseDatabase.getReference("report")
                .child(id).setValue(
                    DataReport(month = Utils.getMonthFromStringDate(date), report = dataReport)
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

}