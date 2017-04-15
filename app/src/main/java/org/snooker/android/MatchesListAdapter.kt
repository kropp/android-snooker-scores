package org.snooker.android

import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.format.DateFormat
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.snooker.api.Match

class MatchesListAdapter(private val activity: MainActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val MATCH = 1001
    val ROUND = 1002

    private var matchesByRound = sortedMapOf<Long,List<Match>>()
    private var rounds = mapOf<Long,String>()

    fun setMatches(matches: List<Match>, rounds: Map<Long,String>) {
        this.rounds = rounds
        matchesByRound = matches
                .groupBy { it.round }
                .map { it.key to it.value.sortedBy { if (it.isActive) -100000+it.number else if (it.isStarted) -10000+it.number else if (it.isFinished) 100000-it.number else it.number } }
                .toMap().toSortedMap()
    }

    override fun getItemCount() = matchesByRound.values.sumBy { it.size + 1 }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MatchViewHolder) {
            val match = matchAt(position)!!
            holder.match = match

            holder.view.setBackgroundResource(if (position % 2 == 0) R.color.colorPrimaryDark else R.color.colorPrimary)
            holder.auxiliary.setTextColor(ContextCompat.getColor(holder.view.context, if (match.isActive) R.color.colorAccent else R.color.textColor))

            if (match.isStarted || match.isFinished) {
                holder.auxiliary.text = "${match.score1}\n${match.score2}"
            } else {
                holder.auxiliary.text = DateFormat.getDateFormat(holder.view.context).format(match.date)
            }

            if (holder.job?.isActive == true) {
                holder.job!!.cancel()
            }
            holder.job = launch(UI) {
                val player1 = async(CommonPool) { match.player1() }
                val player2 = async(CommonPool) { match.player2() }

                holder.first.text = playerName(player1.await().name, match.isPlayer1Winner)
                holder.second.text = playerName(player2.await().name, match.isPlayer2Winner)
            }
        } else if (holder is RoundViewHolder) {
            holder.text.text = roundAt(position)
        }
    }

    private fun playerName(name: String, bold: Boolean) = SpannableString(name).apply {
        if (bold) {
            setSpan(StyleSpan(Typeface.BOLD), 0, name.length, 0)
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
                return rounds[key]
            }
            pos -= (value.size + 1)
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == MATCH)
            MatchViewHolder(activity, LayoutInflater.from(parent.context).inflate(R.layout.matches_list_item, parent, false))
        else
            RoundViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.matches_list_round, parent, false))
}