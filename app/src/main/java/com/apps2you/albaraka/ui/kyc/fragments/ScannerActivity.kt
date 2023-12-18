package com.apps2you.albaraka.ui.kyc.fragments


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.apps2you.albaraka.R
import com.google.android.material.textfield.TextInputEditText
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

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
            zxingScannerView.setResultHandler(this)
            zxingScannerView.startCamera(0) // 0 for the back camera, adjust as needed
        }
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
}
