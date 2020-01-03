package app.georentate.barcodescanner.db

import android.renderscript.Sampler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.georentate.barcodescanner.Item
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class Repository {

    val database = FirebaseDatabase.getInstance()
    val barcodeInserted = MutableLiveData<Boolean>()
    val mutableItemList = MutableLiveData<List<Item>>()
    val mutableKeyList = MutableLiveData<List<String>>()

    fun addBarcode(item: Item) {
        val ref = database.getReference("barcodes")

        val barcodeId = ref.push().key

        ref.child(barcodeId!!).setValue(item).addOnSuccessListener {
            barcodeInserted.value = true
        }
            .addOnFailureListener{
                barcodeInserted.value = false
                print(it.message)
            }
    }

    fun getBarcodes() {
        val itemList = mutableListOf<Item>()
        val keyList = mutableListOf<String>()

        val ref = database.getReference("barcodes")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("error", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(postSnapshot in p0.children) {
                    val item = postSnapshot.getValue(Item::class.java)!!
                    itemList.add(item)
                    keyList.add(postSnapshot.key!!)
                    mutableItemList.value = itemList
                    mutableKeyList.value = keyList
                    Log.i("add", itemList.size.toString())
                }
            }
        })
        Log.i("add", itemList.size.toString())
    }

    fun removeBarcode(date: String) {
        val ref = database.getReference()
        val query = ref.child("barcodes").orderByChild("date").equalTo(date)

        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.e("remove", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(postSnapshot in p0.children)
                    postSnapshot.ref.removeValue()
            }
        })
    }
}