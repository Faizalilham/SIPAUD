package android.coding.ourapp.data.datasource.model

data class AssessmentResponse(
    val assessment : MutableList<AssessmentRequest>? = null,
    val exception : Exception? = null
)