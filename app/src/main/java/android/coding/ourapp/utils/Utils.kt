package android.coding.ourapp.utils


import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.coding.ourapp.R
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.data.datasource.model.Report
import android.coding.ourapp.data.datasource.model.Student
import android.coding.ourapp.databinding.ActivityCreateUpdateReportBinding
import android.coding.ourapp.databinding.ListItemDailyReportBinding
import android.coding.ourapp.databinding.ListItemMonthBinding
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.renderer.LineSeparatorRenderer
import kotlin.collections.ArrayList
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.*
import java.time.LocalDate
import kotlin.coroutines.coroutineContext


class Key{
    companion object{
        const val MONTH = "month"
        const val ID_PARENT = "id_parent"
        const val ID_CHILD = "id_child"
    }
}

object Utils {

    // FUNCTION FOR DATE PICKER CHOOSE
    fun datePicker(context : Context,e : TextView) {
        val formatDate = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener{ _, mYear, mMount, mDay ->
                now.set(Calendar.YEAR,mYear)
                now.set(Calendar.MONTH,mMount)
                now.set(Calendar.DAY_OF_MONTH,mDay)
                e.setText(formatDate.format(now.time))
            },year,month,day
        )
        datePicker.show()
    }

    // FUNCTION FOR GET CURRENT DATE
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate():String{
        val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
        val current = LocalDateTime.now().format(formatter)
        return current.toString()
    }

    // FUNCTION TO SHOW SPINNER DIALOG
    fun spinnerDialog(searchableSpinner : SearchableSpinner, et : EditText, arr : ArrayList<String>,data : (String) -> Unit){
        searchableSpinner.windowTitle = "Tambahkan siswa"
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(position: Int, selectedString: String) {
                et.text.clear()
                data(selectedString)
            }
        }
        searchableSpinner.windowTopBackgroundColor = R.color.primary_color
        searchableSpinner.setSpinnerListItems(arr)
        searchableSpinner.show()
    }

    // FUNCTION FOR UPLOAD IMAGE TO FIREBASE WITH BASE64
    fun uploadImage(uri: Uri, context :Context): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


    // FUNCTION TO GET MONTH FROM STRING LIKE 00-MEI-2023
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthFromStringDate(dateString : String):String{
        val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale("id"))
        val date = LocalDate.parse(dateString, formatter)
        val month = date.month

        return month.name
    }

    // FUNCTION TO CHECK DATA IS NOT EMPTY
    fun createUpdateAssessmentCondition(
        tittle : String,
        description : String,
        date : String,
        students : ArrayList<String>,
        achievementActivity : ArrayList<String>,
        feedback : String,
    ):Boolean{
        return (tittle.isNotBlank() && description.isNotBlank()
                && date.isNotBlank() && students.isNotEmpty() && achievementActivity.isNotEmpty()
                && feedback.isNotBlank())
    }

    // FUNCTION TO SHOW STRING LIKE
    // 1. String 1
    // 2. String 2
    // FROM LIST
    fun convertListToString(inputList: List<String>): String {
        val stringBuilder = StringBuilder()
        var count = 0
        for (item in inputList) {
            count++
            stringBuilder.append("$count. $item").append("\n")
        }

        return stringBuilder.toString()
    }

    // FUNCTION TO CONVERT STRING FROM FIREBASE TO BITMAP, TO SHOW IMAGE IN ACTIVITY
    fun convertStringToBitmap(image : String):Bitmap{
        val decodedBytes = Base64.decode(image, Base64.DEFAULT)
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val quality = 100
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, options)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBytes = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
    }


    // FUNCTION TO SAVE IMAGE TO LOCAL STORAGE
    private fun saveImageToExternalStorage(bitmap: ArrayList<Bitmap>): ArrayList<Uri> {
        val path = Environment.getExternalStorageDirectory().toString()
        val file = File(path, "${UUID.randomUUID()}.jpeg")
        val list = ArrayList<Uri>()

        for(i in bitmap){
            try {
                val stream: OutputStream = FileOutputStream(file)
                i.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e : Exception){
                println(e.printStackTrace())
            }
            list.add(Uri.parse(file.absolutePath))
        }
        return list
    }

    // FUNCTION TO GET URI FROM STRING FIREBASE
    fun base64ToUri(base64String: String): Uri? {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val decodedString = String(decodedBytes)
            return Uri.parse(decodedString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // FUNCTION TO SEND MULTI IMAGE WITH TEXT INTO WHATSAPP
    fun sendMultipleImagesAndText(images: List<Bitmap>, text: String,context : Context) {
        val uriList = ArrayList<Uri>()

        for (bitmap in images) {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(path, "${UUID.randomUUID()}.jpeg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            uriList.add(Uri.parse(file.absolutePath))
        }

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
        shareIntent.type = "text/plain"
        shareIntent.type = "image/*"
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text.trim())
        shareIntent.`package` = "com.whatsapp"
        context.startActivity(shareIntent)

    }

    // FUNCTION TO CHECK PERMISSION
    fun checkStoragePermission(
        activity : Activity,
        context : Context):Boolean {
        return if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission( context,Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ), 104)
            true
        }else false
    }

    // FUNCTION TO SHOW IMAGE WITH ANYTHING CONDITION IN REPORT DETAIL ACTIVITY
    fun showImageReportDetail(isShow: Boolean, imageBitmap:ArrayList<Bitmap>?, imageUri:ArrayList<Uri>?, binding: ListItemDailyReportBinding, context: Context){
        binding.apply {
            if(isShow && imageUri != null){
                linearImage.visibility = View.VISIBLE
                if(imageUri.size == 3){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.VISIBLE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                    Glide.with(context).load(imageUri[1]).into(imageSecond)
                    Glide.with(context).load(imageUri[2]).into(imageThird)
                }else if(imageUri.size == 2){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.GONE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                    Glide.with(context).load(imageUri[1]).into(imageSecond)
                }else if(imageUri.size == 1){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.GONE
                    imageThird.visibility = View.GONE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                }else{
                    linearImage.visibility = View.GONE
                }
            }else if(isShow && imageBitmap != null){
                linearImage.visibility = View.VISIBLE
                if(imageBitmap.size == 3){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                    Glide.with(context).load(imageBitmap[1]).into(imageSecond)
                    Glide.with(context).load(imageBitmap[2]).into(imageThird)
                }else if(imageBitmap.size == 2){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                    Glide.with(context).load(imageBitmap[1]).into(imageSecond)
                }else if(imageBitmap.size == 1){
                    imageFirst.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                }else{
                    linearImage.visibility = View.GONE
                }
            }else{
                binding.linearImage.visibility = View.GONE
            }
        }
    }


    // FUNCTION TO SHOW IMAGE WITH ANYTHING CONDITION IN REPORT DETAIL ACTIVITY
    fun showImageReportDetails(isShow: Boolean, imageBitmap:ArrayList<Bitmap>?, imageUri:ArrayList<Uri>?, binding: ListItemMonthBinding, context: Context){
        binding.apply {
            if(isShow && imageUri != null){
                linearImage.visibility = View.VISIBLE
                if(imageUri.size == 3){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.VISIBLE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                    Glide.with(context).load(imageUri[1]).into(imageSecond)
                    Glide.with(context).load(imageUri[2]).into(imageThird)
                }else if(imageUri.size == 2){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.GONE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                    Glide.with(context).load(imageUri[1]).into(imageSecond)
                }else if(imageUri.size == 1){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.GONE
                    imageThird.visibility = View.GONE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                }else{
                    linearImage.visibility = View.GONE
                }
            }else if(isShow && imageBitmap != null){
                linearImage.visibility = View.VISIBLE
                if(imageBitmap.size == 3){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                    Glide.with(context).load(imageBitmap[1]).into(imageSecond)
                    Glide.with(context).load(imageBitmap[2]).into(imageThird)
                }else if(imageBitmap.size == 2){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                    Glide.with(context).load(imageBitmap[1]).into(imageSecond)
                }else if(imageBitmap.size == 1){
                    imageFirst.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                }else{
                    linearImage.visibility = View.GONE
                }
            }else{
                binding.linearImage.visibility = View.GONE
            }
        }
    }

    // FUNCTION TO SHOW IMAGE WITH ANYTHING CONDITION IN REPORT DETAIL ACTIVITY
    fun showImageReport(isShow : Boolean, imageBitmap:ArrayList<Bitmap>?, imageUri:ArrayList<Uri>?, binding : ActivityCreateUpdateReportBinding, context : Context){
        binding.apply {
            if(isShow && imageUri != null){
                image.visibility = View.GONE
                linearImage.visibility = View.VISIBLE
                if(imageUri.size == 3){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.VISIBLE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                    Glide.with(context).load(imageUri[1]).into(imageSecond)
                    Glide.with(context).load(imageUri[2]).into(imageThird)
                }else if(imageUri.size == 2){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.GONE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                    Glide.with(context).load(imageUri[1]).into(imageSecond)
                }else if(imageUri.size == 1){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.GONE
                    imageThird.visibility = View.GONE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                }else{
                    linearImage.visibility = View.GONE
                    image.visibility = View.VISIBLE
                }
            }else if(isShow && imageBitmap != null){
                linearImage.visibility = View.VISIBLE
                image.visibility = View.GONE
                if(imageBitmap.size == 3){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    imageThird.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                    Glide.with(context).load(imageBitmap[1]).into(imageSecond)
                    Glide.with(context).load(imageBitmap[2]).into(imageThird)
                }else if(imageBitmap.size == 2){
                    imageFirst.visibility = View.VISIBLE
                    imageSecond.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                    Glide.with(context).load(imageBitmap[1]).into(imageSecond)
                }else if(imageBitmap.size == 1){
                    imageFirst.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                }else{
                    linearImage.visibility = View.GONE
                    image.visibility = View.VISIBLE
                }
            }else{
                binding.linearImage.visibility = View.GONE
            }
        }
    }

    fun filter(data : ArrayList<AssessmentRequest>,typeFilter : Int):ArrayList<AssessmentRequest>{
        val resultFilter = arrayListOf<AssessmentRequest>()
        val dateFormat = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
        when(typeFilter){
            1 -> {
                data.sortedBy{
                    dateFormat.parse(it.date.toString())
                }
            }
            2 -> {
                data.sortedByDescending{
                    dateFormat.parse(it.date.toString())
                }
            }
            else -> {
                data.forEach {
                    if(it.favorite == true) resultFilter.add(it)
                }


            }
        }
        return resultFilter
    }

    // FUNCTION TO EXPORT PDF TO LOCAL STORAGE
    suspend fun exportToPdf(name : String, summary : String,categoryAgama : String,categoryMoral : String,categoryPekerti : String, reports: List<Report>, month : String,context: Context) {
        val directoryName = "Export Pdf"
        val directory = File(context.filesDir, directoryName)
        var size = 0
        reports.forEach {
            size = it.images.size
            Log.d("SIZE","$size ${it.images.size}")
        }

        if (!directory.exists()) {
            directory.mkdir()
        }
        Log.d("DIRECTORY", "$directory")

        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputPath = File(outputDir, "${name}_${System.currentTimeMillis()}.pdf").path
        val outputFile = File(outputPath)
        if (outputFile.exists()) {
            outputFile.delete()
        }

        val pdfWriter = PdfWriter(outputPath)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        setParagraph("LAPORAN BULANAN",TextAlignment.CENTER,document,10f,20f,22f,true)
        val horizontalLine = LineSeparator(null)
        horizontalLine.setMarginTop(10f)
        horizontalLine.setMarginBottom(10f)
        horizontalLine.setNextRenderer(LineSeparatorRenderer(horizontalLine))
        document.add(horizontalLine)

        setParagraph("Nama : $name",TextAlignment.LEFT,document,20f,10f,18f,false)
        setParagraph("Laporan bulan : $month",TextAlignment.LEFT,document,5f,10f,18f,false)
        setParagraph("Kategori Agama : $categoryAgama",TextAlignment.LEFT,document,5f,10f,18f,false)
        setParagraph("Kategori Moral : $categoryMoral",TextAlignment.LEFT,document,5f,10f,18f,false)
        setParagraph("Kategori Budi Pekerti : $categoryPekerti",TextAlignment.LEFT,document,5f,10f,18f,false)
        setParagraph("Narasi :",TextAlignment.LEFT,document,5f,10f,18f,false)
        setParagraph(summary,TextAlignment.LEFT,document,5f,10f,18f,false)



        val table = Table(UnitValue.createPercentArray(size)).useAllAvailableWidth()
        table.setHorizontalAlignment(HorizontalAlignment.LEFT)

        for (reports in reports) {
            setParagraph(reports.reportName,TextAlignment.LEFT,document,20f,10f,18f,true)
            setParagraph("Tanggal",TextAlignment.LEFT,document,10f,10f,18f,false)
            setParagraph(reports.reportDate,TextAlignment.LEFT,document,5f,10f,18f,false)
            if(reports.images.isNotEmpty()){
                for(bitmaps in reports.images){
                    val bitmap = convertStringToBitmap(bitmaps)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val imageData = ImageDataFactory.create(stream.toByteArray())
                    val image = Image(imageData)
                    image.scaleToFit(160f, 160f)
                    image.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    val cell = Cell()
                    cell.setBorder(Border.NO_BORDER)
                    if (reports.images.size == 1) {
                        image.scaleToFit(260f, 160f)
                    }
                    cell.add(image)
                    cell.setMarginBottom(20f)

                    if (reports.images.size == 1) {
                        cell.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    }

                    table.addCell(cell)
                    document.add(table)
                }
            }
            setParagraph("Capaian Kegiatan",TextAlignment.LEFT,document,20f,10f,18f,false)
            setParagraph(convertListToString(reports.indicatorAgama),TextAlignment.LEFT,document,5f,10f,18f,false)

        }

        document.close()
    }

    private fun setParagraph(
        text : String,textAligment : TextAlignment,document : Document,
        marginTop : Float,
        marginBottom : Float,
        fontSize : Float,
        isBold : Boolean){
        val paragraph = Paragraph(text)
        paragraph.setFont(PdfFontFactory.createFont())
        paragraph.setFontSize(fontSize)
        paragraph.setTextAlignment(textAligment)
        paragraph.setMarginTop(marginTop)
        paragraph.setMarginBottom(marginBottom)
        if(isBold){ paragraph.setBold() }
        document.add(paragraph)
    }

    fun category(skor : Int):String{
        return when(skor){
            in 1..5 -> "Belum berkembang"
            in 6..10 -> "Mulai berkembang"
            in 11..15 -> "Berkembang sesuai harapan"
            in 16..20 -> "Berkembang sangat baik"
            else -> "Tidak terdefinisi"
        }
    }



    // FUNCTION TO GET UNIQUE DATA FROM ARRAYLIST
    fun unique(arr1 : ArrayList<String>,arr2 : ArrayList<String>):ArrayList<String>{
        val uniqueArray = mutableListOf<String>()

        for (element in arr1) {
            if (!arr2.contains(element) && !uniqueArray.contains(element)) {
                uniqueArray.add(element)
            }
        }

        for (element in arr2) {
            if (!arr1.contains(element) && !uniqueArray.contains(element)) {
                uniqueArray.add(element)
            }
        }

        return ArrayList(uniqueArray)
    }

    fun checkDataIsExist(){}


//    fun exportToPdf(images: List<Bitmap>, texts: List<String>, outputPath: String) {
//        val document = Documen
//        PdfWriter.getInstance(document, FileOutputStream(outputPath))
//        document.open()
//
//        for (i in 0 until images.size) {
//            val image = Image(images[i], UnitValue.createPercentValue(50f))
//            image.setHorizontalAlignment(HorizontalAlignment.CENTER)
//            image.setVerticalAlignment(VerticalAlignment.MIDDLE)
//
//            val paragraph = Paragraph(texts[i])
//            paragraph.setFont(PdfFontFactory.createFont())
//            paragraph.setFontSize(12f)
//            paragraph.setTextAlignment(TextAlignment.CENTER)
//            paragraph.setVerticalAlignment(VerticalAlignment.MIDDLE)
//            paragraph.setHorizontalAlignment(HorizontalAlignment.CENTER)
//            paragraph.setMarginTop(20f)
//            paragraph.setMarginBottom(20f)
//
//            document.add(image)
//            document.add(paragraph)
//        }
//
//        document.close()
//    }

    // FUNCTION TO SEARCH STUDENT
    fun userMatchesSearch(student : Student, searchQuery: String): Boolean {
        val lowercaseQuery = searchQuery.toLowerCase()
        val nameMatches = student.nameStudent?.toLowerCase()?.contains(lowercaseQuery)
        val emailMatches = student.group?.toLowerCase()?.contains(lowercaseQuery)
        return nameMatches == true|| emailMatches == true
    }


    // FUNCTION TO CHANGE LANGUAGE
    fun language(context: Context){
        val languageCode = LanguageManager.loadLanguagePreference(context)
        LanguageManager.setLanguage(context, languageCode)
    }

    // FUNCTION TO HIDE KEYBOARD
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
object LanguageManager {
    private const val PREFERENCE_LANGUAGE = "language"
    private const val DEFAULT_LANGUAGE = "id"

    fun setLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        saveLanguagePreference(context, languageCode)
    }

    fun loadLanguagePreference(context: Context): String {
        val preferences = context.getSharedPreferences("BaseApplication", Context.MODE_PRIVATE)
        return preferences.getString(PREFERENCE_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val preferences = context.getSharedPreferences("BaseApplication", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(PREFERENCE_LANGUAGE, languageCode)
        editor.apply()
    }
}