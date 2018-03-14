package name.kropp.android.snooker

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_event.*

class RankingActivity : AppCompatActivity() {
    private val rankingViewModel by lazy { ViewModelProviders.of(this).get(RankingViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val rankingsList = findViewById<RecyclerView>(R.id.rankings_list)
        val adapter = RankingsListAdapter(this)

        rankingsList.adapter = adapter
        rankingsList.layoutManager = LinearLayoutManager(this)

        rankingViewModel.live().observe({lifecycle}) { rankings ->
            rankings?.let {
                adapter.rankings = it
                adapter.notifyDataSetChanged()
            }
        }
    }
}
