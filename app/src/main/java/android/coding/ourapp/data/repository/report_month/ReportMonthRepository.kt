package android.coding.ourapp.data.repository.report_month
import android.coding.ourapp.data.datasource.firebase.FirebaseHelper
import android.coding.ourapp.data.datasource.model.DataReport
import android.coding.ourapp.data.datasource.model.ReportMonth
import android.util.Log

@Suppress("ControlFlowWithEmptyBody", "ControlFlowWithEmptyBody")
class ReportMonthRepository(private val firebaseHelper: FirebaseHelper) {

    fun addData(
        nameStudent: String,
        month: String,
        point: String,
        category: String,
        desc: String,
        onComplete: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val id = firebaseHelper.dbReferences.push().key
        id?.let {
            val data = ReportMonth(id, nameStudent, month, point, category, desc)
            firebaseHelper.dbReferences.child(it).child("report_month").setValue(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onComplete()
                    } else {
                        onFailure(task.exception?.message ?: "Unknown error")
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Unknown error")
                }
        }
    }

    fun updateData(reportMonth: DataReport) {
        val dataRef = firebaseHelper.dbReferences.child(reportMonth.id).child("narrative")
        val newStudent = DataReport(
            id = reportMonth.id,
            idStudent = "",
            reportMonth.studentName, reportMonth.reports, reportMonth.narratives)
        dataRef.setValue(newStudent)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("UpdateData", "Success Updating Data")
                } else {

                }
            }
            .addOnFailureListener { exception ->
                Log.e("UpdateData", "Error updating data: ${exception.message}")
            }
    }

}
