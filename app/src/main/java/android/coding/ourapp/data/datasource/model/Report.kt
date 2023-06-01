package android.coding.ourapp.data.datasource.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class DataReport(
    val id : String = "",
    val studentName : String = "",
    val reports : MutableList<Report> = mutableListOf(),
    val narratives : MutableList<Narrative> = mutableListOf()
)

@Parcelize
data class Report(
    val id : String = "",
    val reportName : String = "",
    val reportDate : String = "",
    val month : String = "",
    val indicator : MutableList<String> = mutableListOf(),
    val images : MutableList<String> = mutableListOf()
):Parcelable

data class Narrative(
    val summary : String = "",
    val month : String = "",
    val totalIndicator : Int = 0,
)
