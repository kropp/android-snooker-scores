package name.kropp.android.snooker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup


class RankingsListAdapter(private val activity: RankingActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var rankings = listOf<PlayerRanking>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RankingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rankings_list_item, parent, false))

    override fun getItemCount() = rankings.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RankingViewHolder).bind(position+1, rankings[position])
    }
}