package com.example.textscanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CaptureActivity : AppCompatActivity() {
    lateinit var resultText:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_capture)


        resultText=findViewById(R.id.extractedText)
        val camera=findViewById<ImageView>(R.id.capturebtn)
        val erase=findViewById<ImageView>(R.id.editbtn)
        val copy=findViewById<ImageView>(R.id.copybtn)

        camera.setOnClickListener{
            val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager)!=null){
                startActivityForResult(intent,1234)
            }else{
                Toast.makeText(this,"Error opening the camera",Toast.LENGTH_SHORT).show()
            }
        }
        erase.setOnClickListener {
            resultText.setText("")
        }
        copy.setOnClickListener {
            val clipBoard=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip=ClipData.newPlainText("ExtractedText",resultText.text.toString())
            clipBoard.setPrimaryClip(clip)
            Toast.makeText(this,"Copied to Clipboard",Toast.LENGTH_SHORT).show()
        }

    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1234 && resultCode== RESULT_OK){
            val extras=data?.extras
            val bitmap=extras?.get("data") as? Bitmap
            if(bitmap!=null){
                scanText(bitmap)
            }
        }
    }

    private fun scanText(bitmap: Bitmap) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                resultText.setText(visionText.text.toString())
            }
            .addOnFailureListener { e ->
                Log.d("langError","Error: ${e.message}")
                Toast.makeText(this,"Error Processing the image..",Toast.LENGTH_SHORT).show()
            }
    }
}