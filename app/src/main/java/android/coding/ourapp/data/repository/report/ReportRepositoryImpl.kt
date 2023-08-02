package android.coding.ourapp.data.repository.report

import android.coding.ourapp.data.Resource
import android.coding.ourapp.data.datasource.model.DataReport
import android.coding.ourapp.data.datasource.model.Narrative
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.utils.Utils
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject


@Suppress("NAME_SHADOWING")
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
                            if(data != null){
                                val item = data.getValue(DataReport::class.java)
                                if(item != null){

                                    result.add(item)

                                }
                            }
                            
                        }
                    }

                    assessmentLiveData.value = Resource.Success(result)
                    Log.d("datasKUE","${assessmentLiveData.value} ")
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
        Log.d("ERROR","SAMPAI SINI")
        val assessmentLiveData = MutableLiveData<Resource<DataReport>>()
        assessmentLiveData.value = Resource.Loading
        try{
            firebaseDatabase.reference.child("report").child(id).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(DataReport::class.java)
                    Log.d("ERROR","SAMPAI SINI 2")

                    assessmentLiveData.value= Resource.Success(item!!)
                }

                override fun onCancelled(error: DatabaseError) {
                    assessmentLiveData.value = Resource.Failure(error.toException())
                    Log.d("ERROR","${error.toException()}")
                }

            })
            return assessmentLiveData
        }catch (e : Exception){
            Log.d("ERROR","${e.message}")
            e.printStackTrace()
            assessmentLiveData.value = Resource.Failure(e)
            return assessmentLiveData
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createReport(
        idStudent : String,
        studentName: String,
        reportName: String,
        date: String,
        indicatorAgama: MutableList<String>,
        indicatorMoral: MutableList<String>,
        indicatorPekerti : MutableList<String>,
        images: MutableList<String>,
    ): Resource<String> {
        var resultFix = ""
        var resultErr : Exception = Exception()
        val idChild =  firebaseDatabase.reference.push().key.toString()
        val dataReport = mutableListOf(
            Report(id=idChild,reportName,date,Utils.getMonthFromStringDate(date),indicatorAgama,indicatorMoral,indicatorPekerti,images)
        )
        val narrative = mutableListOf(Narrative())

        return try{
            val id = firebaseDatabase.reference.push().key.toString()
            firebaseDatabase.getReference("report")
                .child(id).setValue(
                    DataReport(
                        id =id,
                        idStudent =idStudent,
                        studentName =studentName,
                        reports = dataReport, narratives = narrative)
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
        indicatorAgama: MutableList<String>,
        indicatorMoral: MutableList<String>,
        indicatorPekerti : MutableList<String>,
        images: MutableList<String>,
        listReport : MutableList<Report>,
        listNarrative : MutableList<Narrative>
    ): Resource<String> {
       return if(listReport.isEmpty() && listNarrative.isEmpty()){
           firebaseDatabase.reference.child("report").child(idParent).child("reports").orderByChild("id").equalTo(idChild).addListenerForSingleValueEvent(object : ValueEventListener{
                   @RequiresApi(Build.VERSION_CODES.O)
                   override fun onDataChange(snapshot: DataSnapshot) {
                       if (snapshot.exists()) {
                           for (snapshot in snapshot.children) {
                               val childRef = snapshot.ref
                               childRef.child("indicatorAgama").setValue(indicatorAgama)
                               childRef.child("indicatorMoral").setValue(indicatorMoral)
                               childRef.child("indicatorPekerti").setValue(indicatorPekerti)
                               childRef.child("images").setValue(images)
                               childRef.child("reportName").setValue(reportName)
                               childRef.child("reportDate").setValue(date)
                               childRef.child("month").setValue(Utils.getMonthFromStringDate(date))
                           }
                       }
                   }
                   override fun onCancelled(error: DatabaseError) {
                       Log.d("ERROR","${error.toException()}")
                   }

               })

           Resource.Success("Success update data")
       }else{
           return if(listNarrative.isNotEmpty()){
               Log.d("CEK","SAMPAI SINI $idParent")
               firebaseDatabase.reference.child("report").child(idParent).child("narratives")
                   .setValue(listNarrative)
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
    }


    override fun deleteReport(idParent: String,idChild:String) : Resource<String> {
        return try {
            firebaseDatabase.reference.child("report").child(idParent).child("reports")
                .orderByChild("id").equalTo(idChild).limitToFirst(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (snapshot in snapshot.children) {
                                // Remove data from the top index using setValue(null)
                                snapshot.ref.setValue(null)
                                    .addOnSuccessListener {
                                        println("Data berhasil dihapus")
                                    }
                                    .addOnFailureListener { e: Exception ->
                                        println("Gagal menghapus data: ${e.message}")
                                    }
                                // Break the loop after deleting the top index
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("ERROR", "${error.toException()}")
                    }

                })
            Resource.Success("Success delete data")
        }catch (e : Exception){
            Log.d("DELETE REPORT","${e.message}")
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun deleteNarrative(idParent: String): Resource<String> {
        return try {
            val indexToRemove = 0
            firebaseDatabase.reference.child("report").child(idParent).child("narratives")
                .child(indexToRemove.toString()).removeValue()
            Resource.Success("Success delete data")
        }catch (e : Exception){
            Log.d("DELETE REPORT","${e.message}")
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

}