package android.coding.ourapp.data.datasource.model

data class AssessmentResponse(
    val assessment : ArrayList<AssessmentRequest>? = null,
    val exception : Exception? = null
)