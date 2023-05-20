package android.coding.ourapp.data.datasource.model

data class AssessmentRequest(
    val tittle : String,
    val description : String,
    val date : String,
    val students : ArrayList<String> = arrayListOf(),
    val image : ArrayList<String> = arrayListOf(),
    val achievementActivity : ArrayList<String> = arrayListOf(),
    val feedback : String,
    val isFavorite : Boolean

)
