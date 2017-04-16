package name.kropp.android.snooker

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import name.kropp.android.snooker.api.Match

class MatchViewHolder(val activity: MainActivity, val view: View): RecyclerView.ViewHolder(view) {
    val flag1 = view.findViewById(R.id.flag1) as AppCompatImageView
    val text1 = view.findViewById(android.R.id.text1) as TextView
    val aux1 = view.findViewById(R.id.aux1) as TextView

    val flag2 = view.findViewById(R.id.flag2) as AppCompatImageView
    val text2 = view.findViewById(android.R.id.text2) as TextView
    val aux2 = view.findViewById(R.id.aux2) as TextView

    var match: Match? = null

    init {
        view.setOnClickListener {
            match?.let { activity.onMatchClicked(it) }
        }
    }
}

class RoundViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val text = view.findViewById(android.R.id.text1) as TextView
}