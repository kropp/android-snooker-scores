package name.kropp.android.snooker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import name.kropp.android.snooker.api.Event
import name.kropp.android.snooker.api.Match
import java.util.*

class EventsListAdapter(private val activity: EventsActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var events = listOf<Event>()

    private var ongoingMatchesByRound = sortedMapOf<Long, List<Match>>()
    private var rounds = emptyMap<Long, String>()

    private var liveMatchesCount = 0

    fun setOngoingMatches(ongoingMatchesByRound: SortedMap<Long, List<Match>>, rounds: Map<Long,String>) {
        this.ongoingMatchesByRound = ongoingMatchesByRound
        this.rounds = rounds
        liveMatchesCount = ongoingMatchesByRound.values.sumBy { it.size + 1 }
    }

    override fun getItemCount() = events.size + liveMatchesCount

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventViewHolder -> holder.bind(events[position - liveMatchesCount])
            is MatchViewHolder -> holder.bind(matchAt(position))
            is RoundViewHolder -> holder.bind(roundAt(position))
        }
    }

    fun matchAt(position: Int): Match? {
        var pos = position
        for (matches in ongoingMatchesByRound.values) {
            pos -= 1
            if (pos < matches.size) {
                return matches[pos]
            }
            pos -= matches.size
        }
        return null
    }

    fun roundAt(position: Int): String? {
        var pos = position
        for ((key, value) in ongoingMatchesByRound) {
            if (pos == 0) {
                return rounds[key]
            }
            pos -= (value.size + 1)
        }
        return null
    }


    override fun getItemViewType(position: Int) =
        if (position >= liveMatchesCount) {
            EVENT_VIEWTYPE
        } else {
            getItemViewType(ongoingMatchesByRound, position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        MATCH_VIEWTYPE -> MatchViewHolder(activity, LayoutInflater.from(parent.context).inflate(R.layout.matches_list_item, parent, false))
        ROUND_VIEWTYPE -> RoundViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.matches_list_round, parent, false))
        EVENT_VIEWTYPE -> EventViewHolder(activity, LayoutInflater.from(parent.context).inflate(R.layout.events_list_item, parent, false))
        else -> null
    }
}