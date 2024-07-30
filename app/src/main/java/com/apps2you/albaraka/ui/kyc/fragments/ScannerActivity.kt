package com.apps2you.albaraka.ui.kyc.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.apps2you.albaraka.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import com.bumptech.glide.request.target.Target
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var zxingScannerView: ZXingScannerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_screen)
        zxingScannerView = findViewById(R.id.surfaceView)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                1001
            )
        } else {
//            zxingScannerView.setResultHandler(this)
//            zxingScannerView.startCamera(0) // 0 for the back camera, adjust as needed

            setupScanner()
        }
    }
    private fun setupScanner() {
        zxingScannerView.setAutoFocus(true) // Enable autofocus
        zxingScannerView.setResultHandler(this)
        zxingScannerView.startCamera() // Start camera with default settings
    }
    override fun onPause() {
        super.onPause()
        zxingScannerView.stopCamera()
    }



    override fun handleResult(rawResult: com.google.zxing.Result?) {


        try {

            // Decode the encoded string using ISO-8859-1 encoding (Cp1256)
            val encodedString = rawResult?.text
            val isoBytes = encodedString?.toByteArray(StandardCharsets.ISO_8859_1)
            var decodedString: String? = null

            decodedString = isoBytes?.let { String(it, Charset.forName("Cp1256")) }

            // Successfully decoded, update TextInputEditText fields with the decoded information
            val parts = decodedString?.split("#".toRegex())?.dropLastWhile { it.isEmpty() }
                ?.toTypedArray()

            // Pass the result back to MainActivity
            val resultIntent = Intent()
            resultIntent.putExtra("decodedResult", parts)
            setResult(RESULT_OK, resultIntent)
            zxingScannerView.layoutParams.width = 50
            zxingScannerView.layoutParams.height = 50
            zxingScannerView.requestLayout()

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            // Handle decoding errors
            Toast.makeText(
                this,
                "Failed to decode barcode.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Stop the camera after scanning
        zxingScannerView.stopCamera()

        // Finish the ScannerActivity and return to MainActivity
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupScanner()
        } else {
            Toast.makeText(
                this,
                "Camera permission required for scanning.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
}




