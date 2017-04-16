package org.snooker.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.snooker.api.Event
import org.snooker.api.Match

class MainActivity : AppCompatActivity() {
    private var event: Event? = null
    private val matchesListAdapter = MatchesListAdapter(this)

    private val application: SnookerApplication
        get() = getApplication() as SnookerApplication

    private val longDateFormat = DateFormat.getLongDateFormat(this)

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
            val event = application.repository.event(536)

            event_name.text = event.name
            event_location.text = event.location
            event_location_flag.setImageResource(flagResource(event.country))
            event_dates.text = "${longDateFormat.format(event.startDate)} — ${longDateFormat.format(event.endDate)}"

            this@MainActivity.event = event

            matchesListAdapter.setEvent(event)

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