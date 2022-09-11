package com.example.content_provider_sample

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.UserDictionary
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.content_provider_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBinding()
        setupSearchButton()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            val searchText = binding.searchEditText.text.toString()
            var selectionClause: String? = null
            val selectionArgs = searchText.takeIf { it.isNotEmpty() }?.let {
                selectionClause = "${UserDictionary.Words.WORD} = ?"
                arrayOf(it)
            } ?: run {
                selectionClause = null
                emptyArray()
            }
            Log.d("Tatsuya", "selectionClause→: ${selectionClause}")
            Log.d("Tatsuya", "これで検索→: ${selectionArgs}")

            var mCursor: Cursor? = null

            try {
                mCursor = contentResolver.query(
                    Uri.parse("content://user_dictionary/words"),
                    createProjection(),
                    selectionClause,
                    selectionArgs,
                    null,
                )

                when (mCursor?.count) {
                    null -> {
                        Log.d("Tatsuya", "null: ${mCursor}")
                    }
                    0 -> {
                        Log.d("Tatsuya", "0: ${mCursor}")
                    }
                    else -> {
                        Log.d("Tatsuya", "createSelectionArgs: ${mCursor}")
                    }
                }

                mCursor?.apply {
                    val index: Int = getColumnIndex(UserDictionary.Words.WORD)
                    while (moveToNext()) {
                        val newWord = getString(index)
                        Log.d("Tatsuya", "取得: ${newWord}")
                    }
                }
            } finally {
                mCursor?.close()
            }
        }
    }

    private fun createProjection(): Array<String> {
        val projection = arrayOf(
            UserDictionary.Words._ID,
            UserDictionary.Words.WORD,
            UserDictionary.Words.LOCALE,
        )
        Log.d("Tatsuya", "createProjection: ${projection}")
        return projection
    }
}