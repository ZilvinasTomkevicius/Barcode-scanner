package app.georentate.barcodescanner.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import app.georentate.barcodescanner.ModelPost
import app.georentate.barcodescanner.R
import app.georentate.barcodescanner.RequestApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.R.attr.start
import android.animation.ValueAnimator



class RequestActivity : AppCompatActivity() {

    private val BASE_URL = "http://jsonplaceholder.typicode.com"
    lateinit var  progressBar: ProgressBar
    lateinit var resultTextView: TextView
    lateinit var requestButton: Button

    lateinit var indicator: CustomIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(app.georentate.barcodescanner.R.layout.activity_request)

        progressBar = findViewById(app.georentate.barcodescanner.R.id.progressBar)
        resultTextView = findViewById(app.georentate.barcodescanner.R.id.requestResultTextView)
        requestButton = findViewById(app.georentate.barcodescanner.R.id.requestButton)

        indicator = findViewById(app.georentate.barcodescanner.R.id.resultView)

        progressBar.visibility = View.GONE

        requestButton.setOnClickListener {
            getRequest()
        }
    }

    private fun getRequest() {
      //  progressBar.visibility = View.VISIBLE
        indicator.draw(3)
        resultTextView.text = ""

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RequestApi::class.java)
        service.getData().enqueue(object: Callback<ArrayList<ModelPost>> {
            override fun onResponse(call: Call<ArrayList<ModelPost>>, response: Response<ArrayList<ModelPost>>) {
                resultTextView.text = "The number of items retrieved is: " + response.body()!!.size.toString()
                progressBar.visibility = View.GONE
                indicator.draw(1)
            }
            override fun onFailure(call: Call<ArrayList<ModelPost>>, t: Throwable) {
                resultTextView.text = "Couldn't retrieve any items."
                progressBar.visibility = View.GONE
                indicator.draw(2)
            }
        })

    }
}
