package name.kropp.android.snooker

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import name.kropp.android.snooker.api.Match
import java.text.SimpleDateFormat
import java.util.*

class MatchesListAdapter(private val activity: MainActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dateFormat = SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMMd"), Locale.getDefault())
    val MATCH = 1001
    val ROUND = 1002

    private var matchesByRound = sortedMapOf<Long,List<Match>>()
    private var rounds = mapOf<Long,String>()

    fun setEvent(matches: List<Match>, rounds: Map<Long,String>, reorder: Boolean) {
        this.rounds = rounds
        matchesByRound = matches.groupBy { it.round }
                .map { it.key to it.value.sortedBy {
                    if (reorder) {
                        if (it.isActive) -100000 + it.number
                        else if (it.isStarted) -10000 + it.number
                        else if (it.isFinished) 100000 - it.number
                        else it.number
                    } else {
                        it.number
                    }
                } }.toMap().toSortedMap()
    }

    override fun getItemCount() = matchesByRound.values.sumBy { it.size + 1 }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MatchViewHolder) {
            val match = matchAt(position)!!
            holder.match = match

            holder.view.setBackgroundResource(if (position % 2 == 0) R.color.colorPrimaryDark else R.color.colorPrimary)

            if (match.isStarted || match.isFinished) {
                holder.aux1.text = match.score1.toString()
                holder.aux2.text = match.score2.toString()
            } else {
                holder.aux1.text = dateFormat.format(match.date)
                holder.aux2.text = ""
            }

            val normalOrFade1 = ContextCompat.getColor(holder.view.context, if (match.isPlayer2Winner) R.color.colorPrimaryLight else R.color.textColor)
            holder.text1.setTextColor(normalOrFade1)
            holder.aux1.setTextColor(if (match.isActive) ContextCompat.getColor(holder.view.context, R.color.colorAccent) else normalOrFade1)
            val normalOrFade2 = ContextCompat.getColor(holder.view.context, if (match.isPlayer1Winner) R.color.colorPrimaryLight else R.color.textColor)
            holder.text2.setTextColor(normalOrFade2)
            holder.aux2.setTextColor(if (match.isActive) ContextCompat.getColor(holder.view.context, R.color.colorAccent) else normalOrFade2)

            holder.flag1.setImageResource(flagResource(match.player1.nationality))
            holder.text1.text = match.player1.name

            holder.flag2.setImageResource(flagResource(match.player2.nationality))
            holder.text2.text = match.player2.name
        } else if (holder is RoundViewHolder) {
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