package name.kropp.android.snooker

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import name.kropp.android.snooker.api.Event
import name.kropp.android.snooker.api.Match
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var event: Event
    private val liveMatchesListAdapter = MatchesListAdapter(this)
    private val allMatchesListAdapter = MatchesListAdapter(this)

    private val application: SnookerApplication
        get() = getApplication() as SnookerApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val id = intent.getLongExtra("id", 537)



        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mathes_frame, MatchesListFragment(allMatchesListAdapter))
        transaction.commit()

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        update(id, true)

        app_bar_layout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            swipe.isEnabled = verticalOffset <= 0
        }

        swipe.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryLight)
        swipe.setDistanceToTriggerSync(200)
        swipe.setOnRefreshListener {
            update(id)
        }
    }

    private fun update(id: Long, cache: Boolean = false) {
        launch(UI) {
            try {
                event = run(CommonPool) { application.repository.event(id) }

/*
                event_location.text = event.location
                event_location_flag.setImageResource(flagResource(event.country))
                event_dates.text = formatEventDates(event, this@MainActivity)
*/

//                toolbar.title = "${event.name} ${yearFormat.format(event.endDate)}"
                supportActionBar!!.title = event.name
                supportActionBar!!.subtitle = formatEventDates(event, this@MainActivity)
/*
                event_name.text = event.name
*/

                val r = event.rounds()
                val allMatches = event.matches(cache)
                val liveMatches = event.ongoingMatches(cache)
                r.await()

                liveMatchesListAdapter.setEvent(matchesByRound(liveMatches.filter { val date = it.date
                    it.isStarted || (date != null && date <= Date() && !it.isFinished) }, true), event.rounds)
                liveMatchesListAdapter.notifyDataSetChanged()
                allMatchesListAdapter.setEvent(matchesByRound(allMatches.await(), false), event.rounds)
                allMatchesListAdapter.notifyDataSetChanged()
            } catch (e: UnknownHostException) {
                showOfflineSnackbar()
            } catch (e: SocketTimeoutException) {
                showOfflineSnackbar()
            } catch (e: Throwable) {
                Log.d("main", "Failed to update", e)
                Toast.makeText(this@MainActivity, "Unknown error: ${e.message}", Toast.LENGTH_LONG).show()
            }

            swipe.isRefreshing = false
        }
    }

    private fun showOfflineSnackbar() {
        Snackbar.make(main_layout, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    swipe.isRefreshing = true
                    update(intent.getLongExtra("id", 537))
                }.show()
    }

    fun onMatchClicked(match: Match) {
//        val intent = Intent(this, MatchActivity::class.java)
//        intent.putExtra("id", match.id)
        //startActivity(intent)
//        val url = "http://livescores.worldsnookerdata.com/Matches/LiveScoring/${event.worldSnookerId}/${match.worldSnookerId}"
        val customTabsIntent = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark))
        }.build()
        customTabsIntent.launchUrl(this, Uri.parse(match.liveUrl))
    }
}