package name.kropp.android.snooker

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import name.kropp.android.snooker.api.Event
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

class EventsActivity : AppCompatActivity() {
    private lateinit var events: List<Event>

    private val eventsAdapter = EventsListAdapter(this)

    private val application: SnookerApplication
        get() = getApplication() as SnookerApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        events_list.adapter = eventsAdapter
        events_list.layoutManager = LinearLayoutManager(this)

        // show data from cache immediately
        update(true)
        // and request update to show as soon as it is ready
        update()

        swipe.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryLight)
        swipe.setDistanceToTriggerSync(20)
        swipe.setOnRefreshListener {
            update()
        }
    }

    private fun update(cache: Boolean = false) {
        launch(UI) {
            try {
                events = run(CommonPool) { application.repository.events() }
                eventsAdapter.events = events.filterNot(Event::isQualifying).filter { it.endDate >= Date().apply { date-- } }
                eventsAdapter.notifyDataSetChanged()
            } catch (e: UnknownHostException) {
                showOfflineSnackbar()
            } catch (e: SocketTimeoutException) {
                showOfflineSnackbar()
            } catch (e: Throwable) {
                Log.d("main", "Failed to update", e)
                Toast.makeText(this@EventsActivity, "Unknown error: ${e.message}", Toast.LENGTH_LONG).show()
            }

            swipe.isRefreshing = false
        }
    }

    private fun showOfflineSnackbar() {
        Snackbar.make(swipe, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    swipe.isRefreshing = true
                    update()
                }.show()
    }

    fun onEventClicked(event: Event, eventNameView: View, eventDatesView: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("id", event.id)
        val sceneTransitionAnimation = ActivityOptions.makeSceneTransitionAnimation(this, Pair(eventNameView, "eventName"), Pair(eventDatesView, "eventDates"))
        startActivity(intent, sceneTransitionAnimation.toBundle())
    }
}