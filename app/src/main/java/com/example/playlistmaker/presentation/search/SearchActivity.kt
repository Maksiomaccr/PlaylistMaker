package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.interactor.TrackInteractor
import com.example.playlistmaker.domain.entities.Resource
import com.example.playlistmaker.domain.entities.Track
import com.example.playlistmaker.presentation.audioplayer.AudioPlayerActivity
import com.google.gson.Gson

const val TRACK_HISTORY = "trackHistory"

class SearchActivity : AppCompatActivity() {

    companion object {
        private val EDIT_TEXT = "editText"
        const val LAST_RESPONSE = "LastResponse"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private var editText = ""
    private var lastResponse: String = ""
    private lateinit var binding: ActivitySearchBinding

    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    private val onClick: (Track) -> Unit = {
        if (clickDebounce()) {
            if (it.previewUrl != null) {
                searchHistoryInteractor.addToTrackHistory(it)
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("track", Gson().toJson(it))
                Creator.initTrack(it)
                startActivity(intent)
                trackHistoryAdapter.notifyDataSetChanged()
            }
        }
    }
    private val trackList = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(trackList, onClick)
    private val trackHistoryAdapter = TrackAdapter(searchHistoryInteractor.getTrackHistory(), onClick)

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        val requestText = binding.inputSearch.text.toString()
        search(requestText)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trackLayout.adapter = trackAdapter
        binding.historyList.adapter = trackHistoryAdapter
        binding.clearText.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)
            binding.inputSearch.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            binding.clearText.isVisible = false
            binding.errorPlaceholder.isVisible = false
            binding.inputSearch.clearFocus()
        }

        binding.back.setOnClickListener {
            finish()
        }
        binding.updateButton.setOnClickListener {
            search(lastResponse)
        }
        binding.clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearTrackHistory()
            binding.historyLayout.isVisible = false
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val editText = s.toString()
                searchDebounce()
                if (binding.inputSearch.hasFocus() && s?.isEmpty() == true && searchHistoryInteractor.getTrackHistory().isNotEmpty()) {
                    binding.historyLayout.isVisible = true
                    binding.errorPlaceholder.isVisible = false
                    binding.clearText.isVisible = true
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    savedInstanceState?.putString(EDIT_TEXT, editText)
                } else {
                    binding.historyLayout.isVisible = false
                }
                binding.clearText.isVisible = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.inputSearch.addTextChangedListener(searchTextWatcher)
        binding.trackLayout.layoutManager = LinearLayoutManager(this)
        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(binding.inputSearch.text.toString())
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
                true
            }
            false
        }
        binding.inputSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputSearch.text.isEmpty() && searchHistoryInteractor.getTrackHistory().isNotEmpty()) {
                binding.historyLayout.isVisible = true
                binding.errorPlaceholder.isVisible = false
            } else {
                binding.historyLayout.isVisible = false
                binding.errorPlaceholder.isVisible = false
            }

        }
    }

    private fun search(text: String) {
        if (text.isNotEmpty()) {
            binding.errorPlaceholder.isVisible = false
            binding.progressBar.isVisible = true
            Creator.provideTrackInteractor()
                .searchTrack(text, object : TrackInteractor.TrackConsumer {
                    override fun consume(foundTrack: Resource) {
                        runOnUiThread {
                            binding.progressBar.isVisible = false
                            if (foundTrack.responseCode == 200) {
                                trackList.clear()
                                if (foundTrack.track!!.isNotEmpty()) {
                                    binding.errorPlaceholder.isVisible = false
                                    trackList.addAll(foundTrack.track)
                                    trackAdapter.notifyDataSetChanged()
                                } else {
                                    showErrorPlaceholder(1)
                                }
                            } else {
                                showErrorPlaceholder(0)
                                lastResponse = text
                            }
                            binding.progressBar.isVisible = false
                        }
                    }
                })
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, editText)
        outState.putString(LAST_RESPONSE, lastResponse)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.inputSearch.setText(savedInstanceState.getString(EDIT_TEXT))
        editText = savedInstanceState.getString(EDIT_TEXT, "")
        lastResponse = savedInstanceState.getString(LAST_RESPONSE, "")
        binding.inputSearch.setText(editText)
        search(editText)
    }

    private fun showErrorText(text: String) {
        if (text.isNotEmpty()) {
            binding.wrongMessage.text = text
        }
    }

    private fun showErrorImage(image: String) {
        if (image == "NOT_INTERNET") {
            binding.wrongMessage.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.track_error,
                0,
                0
            )
        }
        if (image == "NOT_FOUND_TRACK") {
            binding.wrongMessage.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.not_found_track,
                0,
                0
            )
        }
    }

    private fun showErrorPlaceholder(code: Int) {
        binding.errorPlaceholder.isVisible = true
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        if (code == 0) {
            showErrorText(getString(R.string.wrong))
            showErrorImage("NOT_INTERNET")
            binding.updateButton.isVisible = true
        }
        if (code == 1) {
            showErrorText(getString(R.string.not_found))
            showErrorImage("NOT_FOUND_TRACK")
            binding.updateButton.isVisible = false
        }
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean = !s.isNullOrEmpty()

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
        handler.removeCallbacks { isClickAllowed = true }
    }

    override fun onStop() {
        super.onStop()
    }

}