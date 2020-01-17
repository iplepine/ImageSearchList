package com.lezhin.test.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lezhin.test.search.search.SearchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.root, SearchFragment())
            .commit()
    }
}
