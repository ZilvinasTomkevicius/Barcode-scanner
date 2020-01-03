package app.georentate.barcodescanner.view

import android.content.Context
import android.content.Intent
import android.media.Image
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import app.georentate.barcodescanner.Item
import app.georentate.barcodescanner.R
import app.georentate.barcodescanner.db.Repository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.oned.ITFReader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var startScanButton: Button
    lateinit var filterEditText: EditText
    lateinit var openRequestActivityButton: FloatingActionButton
    lateinit var openCompassActivityButton: FloatingActionButton

    lateinit var repository: Repository

    var newArrayList = arrayListOf<Item>()

    companion object {
        lateinit var context: Context
        var resultList = ArrayList<Item>(arrayListOf())
        var recyclerViewAdapter = RecyclerViewAdapter(arrayListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = Repository()

        context = this

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)!!
            adapter = recyclerViewAdapter
        }

        repository.getBarcodes()
        repository.mutableItemList.observeForever(Observer {
            recyclerViewAdapter.updateResultList(it)
        })

        filterEditText = findViewById(R.id.filter_editText)
        recyclerViewAdapter.updateResultList(resultList)

        startScanButton = findViewById(R.id.scan_button)
        startScanButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScanActivity::class.java))
        }

        openRequestActivityButton = findViewById(R.id.open_request_activity)
        openRequestActivityButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, RequestActivity::class.java))
        }

        openCompassActivityButton = findViewById(R.id.open_compass_activity)
        openCompassActivityButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, CompassActivity::class.java))
        }

        filterListByInput()
    }

    /*
        inflating delete button
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean{
       when(item!!.itemId) {
//           R.id.bar_button_id -> {
//               if(resultList.size > 0)
//                   showAlert()
//               return true
//           }
           R.id.bar_button_sort -> {
               if(resultList.size > 0) {
                   resultList.sortBy { it.code }
                   recyclerViewAdapter.updateResultList(resultList)
               }
           }
       }
        return true
    }

    private fun filterListByInput() {
        filterEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(resultList.size > 0) {
                    newArrayList.clear()
                    for(r in resultList) {
                        if(r.code.contains(p0!!.toString().toLowerCase()) || r.code.contains(p0.toString().toUpperCase()))
                            newArrayList.add(r)
                        recyclerViewAdapter.updateResultList(newArrayList)
                    }
                }
            }
        })
    }

    /*
        alert for deleting code list
     */
    private fun showAlert() {
        AlertDialog.Builder(this)
            .setMessage("Clear list?")
            .setPositiveButton("Yes") { dialogInterface, i ->
                resultList.clear()
                recyclerViewAdapter.updateResultList(resultList)
            }
            .setNegativeButton("NO") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        repository.mutableItemList.removeObservers(this)
        super.onDestroy()
    }
}
