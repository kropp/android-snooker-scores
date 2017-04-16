package name.kropp.android.snooker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import name.kropp.android.snooker.api.Event
import name.kropp.android.snooker.api.Match
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var event: Event? = null
    private val matchesListAdapter = MatchesListAdapter(this)

    private val application: SnookerApplication
        get() = getApplication() as SnookerApplication

    private val longDateFormat = DateFormat.getLongDateFormat(this)
    private val yearFormat = SimpleDateFormat("YYYY", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        matches_list.adapter = matchesListAdapter
        matches_list.layoutManager = LinearLayoutManager(this)

        update()

        swipe.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryLight)
        swipe.setOnRefreshListener {
            update()
        }
    }

    private fun update() {
        launch(UI) {
            swipe.isRefreshing = true

            val event = application.repository.event(536)
            this@MainActivity.event = event

            event_location.text = event.location
            event_location_flag.setImageResource(flagResource(event.country))
            event_dates.text = "${longDateFormat.format(event.startDate)} — ${longDateFormat.format(event.endDate)}"

            toolbar.title = "${event.name} ${yearFormat.format(event.endDate)}"

            event.fetchMatches()

            matchesListAdapter.setEvent(event)
            matchesListAdapter.notifyDataSetChanged()
            swipe.isRefreshing = false
        }
    }

    fun onMatchClicked(match: Match) {
//        val intent = Intent(this, MatchActivity::class.java)
//        intent.putExtra("id", match.id)
        //startActivity(intent)
        val url = "http://livescores.worldsnookerdata.com/Matches/Result/${event?.id}/${match.worldSnookerId}"
        val customTabsIntent = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark))
        }.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}