package com.apps2you.albaraka.ui.kyc.fragments


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
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
import com.google.android.material.textfield.TextInputEditText
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import com.bumptech.glide.request.target.Target

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var zxingScannerView: ZXingScannerView
    val gifImageView: ImageView = findViewById(R.id.gifImageView)
    val gifUrl = "your_gif_url_here"


    override fun onCreate(savedInstanceState: Bundle?) {


        Glide.with(this)
            .asGif()
            .load(R.drawable.modal1)  // Load the GIF directly from resources
            .apply(RequestOptions().placeholder(R.drawable.ic_logo_outlined)) // Placeholder image while loading
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Start playing the GIF if needed
                    resource?.start()
                    return false
                }
            })
            .into(gifImageView)


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
