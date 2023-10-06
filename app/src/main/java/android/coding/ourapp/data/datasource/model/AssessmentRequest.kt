package android.coding.ourapp.data.datasource.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssessmentRequest(

    val tittle : String? = "",
    val description : String? = "",
    val date : String? = "",
    val students : ArrayList<String> = arrayListOf(),
    val image : ArrayList<String> = arrayListOf(),
    val achievementActivity : ArrayList<String> = arrayListOf(),
    val feedback : String = "",
    val favorite : Boolean? = true,
    val id : String? = "",
):Parcelable
