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

    private val longDateFormat = DateFormat.getLongDateFormat(this)
    private val yearFormat = SimpleDateFormat("YYYY", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout.setupWithViewPager(pager)
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        pager.adapter = EventPagesAdapter(this, supportFragmentManager, liveMatchesListAdapter, allMatchesListAdapter)

        // show data from cache immediately
        update(true)
        // and request update to show as soon as it is ready
        update()

        pager.setOnTouchListener { view, event ->
            swipe.isEnabled = event.action == MotionEvent.ACTION_UP
            false
        }
        app_bar_layout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            swipe.isEnabled = verticalOffset <= 0
        }

        swipe.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryLight)
        swipe.setDistanceToTriggerSync(20)
        swipe.setOnRefreshListener {
            update()
        }
    }

    private fun update(cache: Boolean = false) {
        launch(UI) {
            try {
                event = run(CommonPool) { application.repository.event(536, cache) }

                event_location.text = event.location
                event_location_flag.setImageResource(flagResource(event.country))
                event_dates.text = "${longDateFormat.format(event.startDate)} — ${longDateFormat.format(event.endDate)}"

                toolbar.title = "${event.name} ${yearFormat.format(event.endDate)}"

                val r = event.rounds()
                val allMatches = event.matches(cache)
                val liveMatches = event.ongoingMatches(cache)
                r.await()

                liveMatchesListAdapter.setEvent(liveMatches.filter { it.isStarted || (it.date <= Date() && !it.isFinished) }, event.rounds, true)
                liveMatchesListAdapter.notifyDataSetChanged()
                allMatchesListAdapter.setEvent(allMatches.await(), event.rounds, false)
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
        Snackbar.make(main_layout, R.string.offline, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    swipe.isRefreshing = true
                    update()
                }.show()
    }

    fun onMatchClicked(match: Match) {
//        val intent = Intent(this, MatchActivity::class.java)
//        intent.putExtra("id", match.id)
        //startActivity(intent)
        val url = "http://livescores.worldsnookerdata.com/Matches/Result/${event.id}/${match.worldSnookerId}"
        val customTabsIntent = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark))
        }.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}