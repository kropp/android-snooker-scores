package org.snooker.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.snooker.api.SnookerOrgRepository

val repository = SnookerOrgRepository()

class MainActivity : AppCompatActivity() {
    private val matchesListAdapter = MatchesListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        matches_list.adapter = matchesListAdapter
        matches_list.layoutManager = LinearLayoutManager(this)

        launch(UI) {
            matchesListAdapter.setMatches(repository.matches())
            matchesListAdapter.notifyDataSetChanged()
        }
    }
}