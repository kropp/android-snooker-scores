package name.kropp.android.snooker

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import java.text.NumberFormat

class RankingViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private val number = view.findViewById<TextView>(R.id.textNumber)
    private val flag = view.findViewById<AppCompatImageView>(R.id.flag)
    private val playerName = view.findViewById<TextView>(android.R.id.text1)
    private val ranking = view.findViewById<TextView>(android.R.id.text2)

    fun bind(i: Int, pr: PlayerRanking) {
        number.text = i.toString()
        flag.setImageResource(flagResource(pr.first.nationality))
        playerName.text = pr.first.name
        ranking.text = NumberFormat.getNumberInstance().format(pr.second)
    }
}