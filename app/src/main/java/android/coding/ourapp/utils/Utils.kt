package android.coding.ourapp.utils


import android.Manifest
import android.app.Activity
import android.coding.ourapp.R
import android.coding.ourapp.data.datasource.model.AssessmentRequest
import android.coding.ourapp.databinding.ActivityCreateUpdateAsesmentBinding
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
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
import kotlin.collections.ArrayList
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.itextpdf.layout.property.UnitValue


object Utils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate():String{
        val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
        val current = LocalDateTime.now().format(formatter)
        return current.toString()
    }

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

    fun uploadImage(uri: Uri, context :Context): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

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

    fun convertListToString(inputList: List<String>): String {
        val stringBuilder = StringBuilder()
        var count = 0
        for (item in inputList) {
            count++
            stringBuilder.append("$count. $item").append("\n")
        }

        return stringBuilder.toString()
    }

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
        if (images.size >= 3) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text.trim())
        }
        shareIntent.`package` = "com.whatsapp"
        context.startActivity(shareIntent)

    }

     fun checkStoragePermission(
        images: List<Bitmap>, text: String,activity : Activity,
        context : Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission( context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 104)
        } else {
           sendMultipleImagesAndText(images, text, context)
        }
    }

    fun showImageAssessment(isShow : Boolean,imageBitmap:ArrayList<Bitmap>?,imageUri:ArrayList<Uri>?,binding : ActivityCreateUpdateAsesmentBinding,context : Context){
        binding.apply {
            if(isShow && imageUri != null){
                linearImage.visibility = View.VISIBLE
                binding.image.visibility = View.GONE
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
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                    Glide.with(context).load(imageUri[1]).into(imageSecond)
                }else{
                    imageFirst.visibility = View.VISIBLE
                    Glide.with(context).load(imageUri[0]).into(imageFirst)
                }
            }else if(isShow && imageBitmap != null){
                linearImage.visibility = View.VISIBLE
                binding.image.visibility = View.GONE
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
                }else{
                    imageFirst.visibility = View.VISIBLE
                    Glide.with(context).load(imageBitmap[0]).into(imageFirst)
                }
            }else{
                binding.linearImage.visibility = View.GONE
                binding.image.visibility = View.VISIBLE
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

    fun exportToPdf(bitmaps: List<Bitmap>, texts: List<String>,context : Context) {
        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputPath = File(outputDir, "${texts[0]}.pdf").path
        val pdfWriter = PdfWriter(outputPath)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val table = Table(UnitValue.createPercentArray(bitmaps.size)).useAllAvailableWidth()
        table.setHorizontalAlignment(HorizontalAlignment.LEFT)


        for (bitmap in bitmaps) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageData = ImageDataFactory.create(stream.toByteArray())
            val image = Image(imageData)
            image.scaleToFit(160f, 160f)
            image.setHorizontalAlignment(HorizontalAlignment.CENTER)
            val cell = Cell()
            cell.setBorder(Border.NO_BORDER)
            cell.add(image)
            table.addCell(cell)
        }

        var count = 0
        for (text in texts) {
            count++
            val paragraph = Paragraph(text)
            paragraph.setFont(PdfFontFactory.createFont())
            paragraph.setFontSize(22f)
            paragraph.setTextAlignment(TextAlignment.CENTER)
            if(count == 2){
                paragraph.setTextAlignment(TextAlignment.RIGHT)

            }
            paragraph.setVerticalAlignment(VerticalAlignment.MIDDLE)
            paragraph.setHorizontalAlignment(HorizontalAlignment.CENTER)
            paragraph.setMarginTop(20f)
            paragraph.setMarginBottom(20f)
            document.add(paragraph)
            if(count == 2){
                document.add(table)
            }

        }

        document.close()
    }


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


}