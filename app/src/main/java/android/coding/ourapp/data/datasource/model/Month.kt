package android.coding.ourapp.data.datasource.model

import android.coding.ourapp.R

data class Month(
    val id : String,
    val name : String,
    val count : Int,
    val background : Int = R.drawable.background_dashed,

)