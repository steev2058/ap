package com.apps2you.albaraka.ui.financeForm



import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.File
import java.io.FileOutputStream

class CompressImageTask(
    private val filePath: File,
    private val maxWidth: Int,
    private val maxHeight: Int,
    private val quality: Int = 100
) : AsyncTask<Unit, Unit, File?>() {
    lateinit var file: File

    override fun doInBackground(vararg params: Unit?): File? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            BitmapFactory.decodeFile(filePath.toString(), options)

            var width = options.outWidth
            var height = options.outHeight

            if (width > height) {
                if (width > maxWidth) {
                    height = (height * maxWidth) / width
                    width = maxWidth
                }
            } else {
                if (height > maxHeight) {
                    width = (width * maxHeight) / height
                    height = maxHeight
                }
            }

            BitmapFactory.decodeFile(filePath.toString())?.let { originalBitmap ->
                val compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)

                file = File.createTempFile("compressed_", ".jpg")
                val outputStream = FileOutputStream(file)

                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.close()

                file
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}



class ValidateImageTask(private val file: File) : AsyncTask<Unit, Unit, Boolean>() {

    override fun doInBackground(vararg params: Unit?): Boolean {
        return try {
            val allowedExtensions = listOf(".jpg", ".jpeg", ".png")
            val allowedMimeTypes = listOf("image/jpeg", "image/jpg", "image/png")

            val extension = file.extension.toLowerCase()
            if (!allowedExtensions.contains(".$extension")) {
                return false
            }

            val mimeType = getImageMimeType(file)
            if (!allowedMimeTypes.contains(mimeType)) {
                return false
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getImageMimeType(file: File): String {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(file.path, options)

        return options.outMimeType
    }
}
