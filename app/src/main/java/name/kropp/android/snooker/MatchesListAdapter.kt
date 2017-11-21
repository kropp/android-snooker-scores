package name.kropp.android.snooker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import name.kropp.android.snooker.api.Match
import java.util.*

fun matchesByRound(matches: List<Match>, reorder: Boolean): SortedMap<Long, List<Match>> {
    return matches.groupBy { it.round }
            .map {
                it.key to it.value.sortedBy {
                    if (reorder) {
                        if (it.isActive) -100000 + it.number
                        else if (it.isStarted) -10000 + it.number
                        else if (it.isFinished) 100000 - it.number
                        else it.number
                    } else {
                        it.number
                    }
                }
            }.toMap().toSortedMap()
}

val MATCH_VIEWTYPE = 1001
val ROUND_VIEWTYPE = 1002
val EVENT_VIEWTYPE = 1003

fun getItemViewType(matchesByRound: SortedMap<Long, List<Match>>, position: Int): Int {
    var pos = position
    for (size in matchesByRound.values.map { it.size + 1 }) {
        if (pos == 0) {
            return ROUND_VIEWTYPE
        }
        pos -= size
        if (pos < 0) {
            return MATCH_VIEWTYPE
        }
    }
    return 0
}

class MatchesListAdapter(private val activity: MainActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var matchesByRound = sortedMapOf<Long,List<Match>>()
    private var rounds = mapOf<Long,String>()

    fun setEvent(matchesByRound: SortedMap<Long, List<Match>>, rounds: Map<Long,String>) {
        this.matchesByRound = matchesByRound
        this.rounds = rounds
    }

    override fun getItemCount() = matchesByRound.values.sumBy { it.size + 1 }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MatchViewHolder -> holder.bind(matchAt(position))
            is RoundViewHolder -> holder.bind(roundAt(position))
        }
    }

    override fun getItemViewType(position: Int) = getItemViewType(matchesByRound, position)

    fun matchAt(position: Int): Match? {
        var pos = position
        for (matches in matchesByRound.values) {
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
        for ((key, value) in matchesByRound) {
            if (pos == 0) {
                return rounds[key]
            }
            pos -= (value.size + 1)
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        MATCH_VIEWTYPE -> MatchViewHolder(activity, LayoutInflater.from(parent.context).inflate(R.layout.matches_list_item, parent, false))
        ROUND_VIEWTYPE -> RoundViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.matches_list_round, parent, false))
        else -> null
    }
}