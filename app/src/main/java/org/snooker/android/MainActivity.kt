package org.snooker.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.snooker.api.Match

class MainActivity : AppCompatActivity() {
    private val matchesListAdapter = MatchesListAdapter(this)

    val application: SnookerApplication
        get() = getApplication() as SnookerApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        matches_list.adapter = matchesListAdapter
        matches_list.layoutManager = LinearLayoutManager(this)

        update()

        swipe.setOnRefreshListener {
            update()
        }
    }

    private fun update() {
        launch(UI) {
            matchesListAdapter.setMatches(application.repository.matches(), application.repository.rounds())
            matchesListAdapter.notifyDataSetChanged()
            swipe.isRefreshing = false
        }
    }

    fun onMatchClicked(match: Match) {
        val intent = Intent(this, MatchActivity::class.java)
        intent.putExtra("id", match.id)
        startActivity(intent)
    }
}