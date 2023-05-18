package android.coding.ourapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Utils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate():String{
        val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
        val current = LocalDateTime.now().format(formatter)
        return current.toString()
    }




}