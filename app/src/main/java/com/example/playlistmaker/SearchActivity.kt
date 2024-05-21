package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsetsAnimation.Callback
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var notFoundTrackImage: ImageView
    private lateinit var errorPlayListImage: ImageView
    private lateinit var updateButton: MaterialButton
    private lateinit var placeholderText: TextView
    private lateinit var buttonBack: ImageButton
    companion object{
    const val EDIT_TEXT = "editText"
        val trackList: MutableList<Track> = mutableListOf()
}

    val trackAdapter = TrackAdapter(trackList)
    private var savedText: String? = null
    private val tunesBaseUrl = "https://itunes.apple.com"
    var retrofit = Retrofit.Builder()
        .baseUrl(tunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesApi::class.java)


    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputText = findViewById<EditText>(R.id.inputSearch)
        clearButton = findViewById<ImageButton>(R.id.clear_text)
        updateButton = findViewById<MaterialButton>(R.id.update_button)
        notFoundTrackImage = findViewById<ImageView>(R.id.notFoundTrack)
        errorPlayListImage = findViewById<ImageView>(R.id.wrongImage)
        placeholderText = findViewById<TextView>(R.id.wrongText)
        buttonBack = findViewById<ImageButton>(R.id.back)

        buttonBack.setOnClickListener{
            finish()
        }
        val recycler = findViewById<RecyclerView>(R.id.track_list)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = trackAdapter

        updateButton.setOnClickListener{
            search()
        }
        clearButton.setOnClickListener{
                v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
            inputText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            clearPlaceholders()
        }


        val searchTextWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = inputText.text.toString()
            }
        }
        inputText.addTextChangedListener(searchTextWatcher)
        inputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }


    }
    private fun search(){
        if(inputText.text.isNotEmpty()){
            iTunesService.search(inputText.text.toString()).enqueue(object : retrofit2.Callback<TrackResponse>{
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                )
                    {
                        if(response.code() == 200){
                            trackList.clear()
                            errorPlayListImage.visibility = View.GONE
                            updateButton.visibility = View.GONE
                            if(response.body()?.results?.isNotEmpty() == true){
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if(trackList.isEmpty()){
                                showErrorText(getString(R.string.not_found))
                                notFoundTrackImage.visibility = View.VISIBLE
                            }else{
                                showErrorText("")
                                notFoundTrackImage.visibility = View.GONE
                            }

                        }else{
                            showErrorText(getString(R.string.wrong))
                            errorPlayListImage.visibility = View.VISIBLE
                            updateButton.visibility = View.VISIBLE
                        }
                    }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showErrorText(getString(R.string.wrong))
                    errorPlayListImage.visibility = View.VISIBLE
                    updateButton.visibility = View.VISIBLE
                }
            })
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, savedText)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(EDIT_TEXT)

    }
    private fun showErrorText(text: String) {
        if (text.isNotEmpty()) {
            placeholderText.visibility = View.VISIBLE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderText.text = text

        } else {
            placeholderText.visibility = View.GONE
        }
    }
    private fun clearPlaceholders(){
        errorPlayListImage.visibility = View.GONE
        notFoundTrackImage.visibility = View.GONE
        updateButton.visibility = View.GONE
        placeholderText.visibility = View.GONE
    }
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


}