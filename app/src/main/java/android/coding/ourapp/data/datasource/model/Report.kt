package android.coding.ourapp.data.datasource.model



data class DataReport(
    val id : String = "",
    val studentName : String = "",
    val date : String = "",
    val month : String = "",
    val totalIndicator : Int = 0,
    val narrative : String = "",
    val report : MutableList<Report>
)

data class Report(
    val reportName : String = "",
    val reportDate : String = "",
    val indicator : MutableList<String> = mutableListOf(),
    val images : MutableList<String> = mutableListOf()
)
