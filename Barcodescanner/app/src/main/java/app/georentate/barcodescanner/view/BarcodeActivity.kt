package app.georentate.barcodescanner.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import app.georentate.barcodescanner.R

class BarcodeActivity : AppCompatActivity() {

    lateinit var title: TextView
    lateinit var description: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

       // actionBar!!.setDisplayHomeAsUpEnabled(true)

        title = findViewById(R.id.barcode_title)
        description = findViewById(R.id.barcode_description)

        title.setText(intent.getStringExtra("Title"))
        description.setText(intent.getStringExtra("Date"))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
