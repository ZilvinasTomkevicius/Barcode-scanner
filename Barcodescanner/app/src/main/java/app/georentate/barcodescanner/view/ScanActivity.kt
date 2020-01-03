package app.georentate.barcodescanner.view

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.georentate.barcodescanner.Item
import app.georentate.barcodescanner.R
import app.georentate.barcodescanner.db.Repository
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.text.SimpleDateFormat
import java.util.*

class ScanActivity: AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null
    lateinit var stopScanButton: Button
    lateinit var mediaPlayer: MediaPlayer
    private var requestCode = 1

    val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        mScannerView = findViewById(R.id.scan_view)
        mediaPlayer = MediaPlayer.create(this, R.raw.unsure)

        stopScanButton = findViewById(R.id.stop_scan_button)
        stopScanButton.setOnClickListener {
            mScannerView!!.stopCamera()
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), requestCode)
        else {
            mScannerView!!.setResultHandler(this)
            mScannerView!!.startCamera()
        }
    }

    /*
        camera permission handling
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            requestCode -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView!!.setResultHandler(this)
                    mScannerView!!.startCamera()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mScannerView!!.stopCamera()
    }

    override fun handleResult(result: Result?) {
        val smdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        var tmpArray = arrayListOf<String>()
        if(MainActivity.resultList.size > 0) {
            for(r in MainActivity.resultList) {
                tmpArray.add(r.code)
            }
        }
        if(!tmpArray.contains(result!!.text)) {
            MainActivity.resultList.add(Item("", result.text, smdf.format(Date())))
            MainActivity.recyclerViewAdapter.updateResultList(MainActivity.resultList)
            mediaPlayer.start()
            Toast.makeText(this, "Scanned!", Toast.LENGTH_SHORT).show()
            repository.addBarcode(Item("", result.text, smdf.format(Date())))
        } else
            Toast.makeText(this, "The barcode has already been scanned.", Toast.LENGTH_SHORT).show()

        mScannerView!!.resumeCameraPreview(this)
    }
}