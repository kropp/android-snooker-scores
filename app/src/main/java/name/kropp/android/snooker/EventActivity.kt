package name.kropp.android.snooker

import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import name.kropp.android.snooker.api.Match

class EventActivity : AppCompatActivity() {
    private val eventViewModel by lazy { ViewModelProviders.of(this).get(EventViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        val id = intent.getLongExtra("id", 537)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mathes_frame, MatchesListFragment())
        transaction.commit()

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        eventViewModel.live().observe({lifecycle}) { event ->
            supportActionBar!!.title = event!!.name
            supportActionBar!!.subtitle = formatEventDates(event, this@EventActivity)
        }


        update(id)

        app_bar_layout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            swipe.isEnabled = verticalOffset <= 0
        }

        swipe.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryLight)
        swipe.setDistanceToTriggerSync(200)
        swipe.setOnRefreshListener {
            update(id)
        }
    }

    private fun update(id: Long) {
        launch(UI) {
/*
            try {
            } catch (e: UnknownHostException) {
                showOfflineSnackbar()
            } catch (e: SocketTimeoutException) {
                showOfflineSnackbar()
            } catch (e: Throwable) {
                Log.d("main", "Failed to update", e)
                Toast.makeText(this@MainActivity, "Unknown error: ${e.message}", Toast.LENGTH_LONG).show()
            }
*/
            eventViewModel.setEvent(id)

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
            setToolbarColor(ContextCompat.getColor(this@EventActivity, R.color.colorPrimaryDark))
        }.build()
        customTabsIntent.launchUrl(this, Uri.parse(match.liveUrl))
    }
}