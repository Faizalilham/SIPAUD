package android.coding.ourapp.data.datasource.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Achievement(
    val name : String,
    var isChecked : Boolean
):Parcelable
