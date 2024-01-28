package android.coding.ourapp.data.datasource.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class DataReport(
    val ids: String = "",
    var idStudent: String = "",
    var studentName: String = "",
    var reports: MutableList<Report> = mutableListOf(),
    var narratives: MutableList<Narrative> = mutableListOf()
)

@Parcelize
data class Report(
    val id : String = "",
    val reportName : String = "",
    val reportDate : String = "",
    val month : String = "",
    val indicatorAgama : MutableList<String> = mutableListOf(),
    val indicatorMoral : MutableList<String> = mutableListOf(),
    val indicatorPekerti : MutableList<String> = mutableListOf(),
    val images : MutableList<String> = mutableListOf()
):Parcelable

data class Narrative(
    val summary : String = "",
    val month : String? = "",
    val totalIndicatorAgama : Int = 0,
    val totalIndicatorMoral : Int = 0,
    val totalIndicatorPekerti : Int = 0,
)

data class Chart(
    val name : String,
    val month : String,
    val average : Double,
)
