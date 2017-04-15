package org.snooker.android

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.snooker.api.Match

class MatchesListAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val MATCH = 1001
    val ROUND = 1002

    private var matchesByRound = sortedMapOf<String,List<Match>>()

    fun setMatches(matches: List<Match>) {
        matchesByRound = matches.groupBy { it.round }.toSortedMap()
    }

    override fun getItemCount() = matchesByRound.values.sumBy { it.size + 1 }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MatchViewHolder) {
            val match = matchAt(position)!!

            holder.view.setBackgroundResource(if (position % 2 == 0) R.color.colorPrimaryDark else R.color.colorPrimary)
            holder.first.text = ""
            holder.second.text = ""
            holder.auxiliary.text = DateFormat.getDateFormat(holder.view.context).format(match.date)

            if (holder.job?.isActive == true) {
                holder.job!!.cancel()
            }
            holder.job = launch(UI) {
                holder.first.text = match.player1().name
                holder.second.text = match.player2().name
            }
        } else if (holder is RoundViewHolder) {
            holder.view.setBackgroundResource(R.color.colorPrimaryDark)
            holder.text.text = roundAt(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var pos = position
        for (size in matchesByRound.values.map { it.size + 1 }) {
            if (pos == 0) {
                return ROUND
            }
            pos -= size
            if (pos < 0) {
                return MATCH
            }
        }
        return 0
    }

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
                return key
            }
            pos -= (value.size + 1)
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == MATCH)
            MatchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.matches_list_item, parent, false))
        else
            RoundViewHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))
}