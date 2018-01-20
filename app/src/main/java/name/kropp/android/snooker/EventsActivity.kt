package name.kropp.android.snooker

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import name.kropp.android.snooker.api.Event
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*


class EventsActivity : AppCompatActivity() {
    private val eventsAdapter = EventsListAdapter(this)

    private val ongoingMatchesViewModel get() = ViewModelProviders.of(this).get(OngoingMatchesViewModel::class.java)
    private val eventsViewModel get() = ViewModelProviders.of(this).get(EventsViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.app_name, R.string.app_name)
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()

        events_list.adapter = eventsAdapter
        events_list.layoutManager = LinearLayoutManager(this)

        eventsViewModel.events.observe({ lifecycle }) {
            val filtered = it!!.filterNot(Event::isQualifying).filter { it.endDate >= Date().apply { date-- } }
            eventsAdapter.events = if (filtered.isNotEmpty()) filtered else it
            eventsAdapter.notifyDataSetChanged()
        }
        eventsViewModel.refresh()

        ongoingMatchesViewModel.matches.observe({ lifecycle }) {
            if (eventsViewModel.year == 2017L) {
                val ongoingMatchesByRound = matchesByRound(it!!.first, true)
                eventsAdapter.setOngoingMatches(ongoingMatchesByRound, it.second)
                eventsAdapter.notifyDataSetChanged()
            }
        }

        update()

        swipe.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryLight)
        swipe.setDistanceToTriggerSync(200)
        swipe.setOnRefreshListener {
            update()
        }

        navigation.setCheckedItem(R.id.season2017)
        navigation.setNavigationItemSelectedListener {
            it.isChecked = !it.isChecked
            when (it.itemId) {
                R.id.season2017 -> eventsViewModel.year = 2017L
                R.id.season2016 -> eventsViewModel.year = 2016L
                R.id.season2015 -> eventsViewModel.year = 2015L
                R.id.season2014 -> eventsViewModel.year = 2014L
                R.id.season2013 -> eventsViewModel.year = 2013L
                R.id.rankings -> showRankingsActivity()
            }
            drawer_layout.closeDrawers()
            true
        }
    }

    private fun update() = launch(UI) {
        try {
            ongoingMatchesViewModel.refresh()
        } catch (e: UnknownHostException) {
            Log.i("OngoingMatches", "Error retrieving ongoing matches list: ${e.message}")
            showOfflineSnackbar()
        } catch (e: SocketTimeoutException) {
            Log.i("OngoingMatches", "Error retrieving ongoing matches list: ${e.message}")
            showOfflineSnackbar()
        } catch (e: Throwable) {
            Log.d("OngoingMatches", "Failed to update", e)
//            Toast.makeText(this@EventsActivity, "Unknown error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        swipe.isRefreshing = false
    }

    private fun showOfflineSnackbar() {
        Snackbar.make(swipe, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    swipe.isRefreshing = true
                    update()
                }.show()
    }

    fun onEventClicked(event: Event, eventNameView: View, eventDatesView: View) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra("id", event.id)
//        val sceneTransitionAnimation = ActivityOptions.makeSceneTransitionAnimation(this, Pair(eventNameView, "eventName"), Pair(eventDatesView, "eventDates"))
        startActivity(intent/*, sceneTransitionAnimation.toBundle()*/)
    }

    private fun showRankingsActivity() {
        val intent = Intent(this, RankingActivity::class.java)
        startActivity(intent)
    }
}