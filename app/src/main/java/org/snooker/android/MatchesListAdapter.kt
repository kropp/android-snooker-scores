package org.snooker.android

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.snooker.api.Match

class MatchesListAdapter(private val context: Context) : RecyclerView.Adapter<MatchViewHolder>() {
    var matches: List<Match> = emptyList()
    override fun getItemCount() = matches.size

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.view.setBackgroundResource(if (position % 2 == 0) R.color.colorPrimaryDark else R.color.colorPrimary)
        holder.first.text = ""
        holder.second.text = ""
        holder.auxiliary.text = DateFormat.getDateFormat(holder.view.context).format(matches[position].date)

        if (holder.job?.isActive == true) {
            holder.job!!.cancel()
        }
        holder.job = launch(UI) {
            holder.first.text = matches[position].player1().name
            holder.second.text = matches[position].player2().name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MatchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.matches_list_item, parent, false))
}