package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val TRACK_HISTORY = "trackHistory"

class SearchActivity : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var notFoundTrackImage: ImageView
    private lateinit var errorPlayListImage: ImageView
    private lateinit var updateButton: MaterialButton
    private lateinit var placeholderText: TextView
    private lateinit var buttonBack: ImageButton
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyText: TextView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyScrollView: ScrollView
    private lateinit var progressBar: ProgressBar
    private lateinit var recycler: RecyclerView

    companion object {
        const val EDIT_TEXT = "editText"
        val trackList: MutableList<Track> = mutableListOf()
        var trackListHistory: MutableList<Track> = mutableListOf()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    private val trackAdapter = TrackAdapter(trackList, this)
    private val trackHistoryAdapter = TrackAdapter(trackListHistory, this)
    private var savedText: String? = null
    private val tunesBaseUrl = "https://itunes.apple.com"
    var retrofit = Retrofit.Builder()
        .baseUrl(tunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesApi::class.java)

    private val searchRunnable = Runnable { search() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

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
        historyRecycler = findViewById<RecyclerView>(R.id.historyList)
        historyText = findViewById<TextView>(R.id.historyHint)
        clearHistoryButton = findViewById<MaterialButton>(R.id.clearHistoryButton)
        historyScrollView = findViewById<ScrollView>(R.id.historyScroll)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        recycler = findViewById<RecyclerView>(R.id.track_list)

        val sharedPreferences = getSharedPreferences(TRACK_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = trackHistoryAdapter
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = trackAdapter
        buttonBack.setOnClickListener {
            finish()
        }
        updateButton.setOnClickListener {
            search()
        }
        clearButton.setOnClickListener { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
            inputText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            clearPlaceholders()
        }
        clearHistoryButton.setOnClickListener {
            trackListHistory.clear()
            trackHistoryAdapter.notifyDataSetChanged()
            searchHistory.save(trackListHistory)
            showHistory(false)
        }

        inputText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && inputText.text.isNullOrEmpty()) {
                if (trackListHistory.isEmpty()) {
                    trackListHistory = searchHistory.readHistory()
                    trackHistoryAdapter.tracks = trackListHistory
                    trackHistoryAdapter.notifyDataSetChanged()
                }
                showHistory(trackListHistory.isNotEmpty())
            } else {
                showHistory(false)
            }
        }


        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                if (!s.isNullOrEmpty()) {
                    showHistory(false)
                }
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = inputText.text.toString()
            }
        }
        inputText.addTextChangedListener(searchTextWatcher)
        inputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputText.clearFocus()
                search()
                true
            }
            false
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTrackHistory(position: Int) {
        if (!inputText.text.isNullOrEmpty()) {

            if (trackListHistory.size > 0) {
                var isExit = false
                var existsElement: Track? = null
                trackListHistory.forEach { element ->
                    if (element.trackId == trackList[position].trackId) {
                        isExit = true
                        existsElement = element
                    }
                }
                if (isExit) {
                    trackListHistory.remove(existsElement)
                }
                if (trackListHistory.size >= 10) {
                    trackListHistory.removeLast()
                }
            }
            trackListHistory.add(0, trackList[position])
            trackHistoryAdapter.notifyDataSetChanged()
            searchHistory.save(trackListHistory)
            openAudioPlayer(trackList[position])
        } else {
            if (trackListHistory.size > 1) {
                val element = trackListHistory[position]
                trackListHistory.removeAt(position)
                trackListHistory.add(0, element)
                trackHistoryAdapter.notifyDataSetChanged()
                searchHistory.save(trackListHistory)

            }
            openAudioPlayer(trackListHistory[position])
        }
    }

    fun showHistory(show: Boolean) {
        historyText.visibility = elementsVisibility(show)
        historyScrollView.visibility = elementsVisibility(show)
        clearHistoryButton.visibility = elementsVisibility(show)
    }

    private fun elementsVisibility(bool: Boolean): Int {
        return if (bool) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun search() {
        if (inputText.text.isNotEmpty()) {

            progressBar.visibility = View.VISIBLE
            historyRecycler.visibility = View.GONE
            recycler.visibility = View.GONE

            iTunesService.search(inputText.text.toString())
                .enqueue(object : retrofit2.Callback<TrackResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        progressBar.visibility = View.GONE
                        recycler.visibility = View.VISIBLE
                        historyRecycler.visibility = View.VISIBLE

                        if (response.code() == 200) {
                            trackList.clear()
                            errorPlayListImage.visibility = View.GONE
                            updateButton.visibility = View.GONE
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                showErrorText(getString(R.string.not_found))
                                notFoundTrackImage.visibility = View.VISIBLE
                            } else {
                                showErrorText("")
                                notFoundTrackImage.visibility = View.GONE
                            }

                        } else {
                            showErrorText(getString(R.string.wrong))
                            errorPlayListImage.visibility = View.VISIBLE
                            updateButton.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {

                        progressBar.visibility = View.GONE

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

    private fun clearPlaceholders() {
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

    private fun openAudioPlayer(track: Track) {
        if (clickDebounce()) {
            val displayIntent = Intent(this, AudioPlayerActivity::class.java)
            displayIntent.putExtra("trackName", track.trackName)
            displayIntent.putExtra("artistName", track.artistName)
            displayIntent.putExtra("trackTimeMillis", track.trackTime)
            displayIntent.putExtra("artworkUrl100", track.artworkUrl100)
            displayIntent.putExtra("collectionName", track.collectionName)
            displayIntent.putExtra("releaseDate", track.releaseDate)
            displayIntent.putExtra("primaryGenreName", track.primaryGenreName)
            displayIntent.putExtra("country", track.country)

            displayIntent.putExtra("previewUrl", track.previewUrl)
            startActivity(displayIntent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

}


