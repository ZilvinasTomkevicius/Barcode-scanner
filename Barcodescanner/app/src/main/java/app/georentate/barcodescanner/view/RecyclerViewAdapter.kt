package app.georentate.barcodescanner.view

import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaBrowserCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import app.georentate.barcodescanner.Item
import app.georentate.barcodescanner.R
import app.georentate.barcodescanner.db.Repository
import app.georentate.barcodescanner.view.MainActivity.Companion.resultList
import kotlinx.android.synthetic.main.scan_result_item.view.*
import java.lang.Exception

/*
    recycler view realisation
 */
class RecyclerViewAdapter(var resultList: ArrayList<Item>): RecyclerView.Adapter<RecyclerViewAdapter.ResultViewHolder>() {

    public fun updateResultList(newResults: List<Item>) {
        resultList.clear()
        resultList.addAll(newResults)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ResultViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.scan_result_item, parent, false)
    )

    fun removeAt(position: Int) {
        var newArray = resultList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    override fun getItemCount() = resultList.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(resultList[position].code, resultList[position].date, resultList[position], position)
    }

    inner class ResultViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var resultText = view.result_text
        var deleteImageView = view.delete_code
        lateinit var repository: Repository

        fun bind(result: String, resultDate: String, item: Item, position: Int) {
            repository = Repository()
            resultText?.setText(result)
            resultText.setOnClickListener {
                var intent = Intent(MainActivity.context, BarcodeActivity::class.java)
                intent.putExtra("Title", resultText.text)
                intent.putExtra("Date", resultDate)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                MainActivity.context.startActivity(intent)
            }

            deleteImageView.setOnClickListener {
                removeAt(position)
                repository.removeBarcode(resultDate)
            }
        }
    }
}