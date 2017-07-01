package name.kropp.android.snooker

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import name.kropp.android.snooker.api.Match

class MatchViewHolder(val activity: MainActivity, val view: View): RecyclerView.ViewHolder(view) {
    val flag1 = view.findViewById<AppCompatImageView>(R.id.flag1)
    val text1 = view.findViewById<TextView>(android.R.id.text1)
    val aux1 = view.findViewById<TextView>(R.id.aux1)

    val flag2 = view.findViewById<AppCompatImageView>(R.id.flag2)
    val text2 = view.findViewById<TextView>(android.R.id.text2)
    val aux2 = view.findViewById<TextView>(R.id.aux2)

    var match: Match? = null

    init {
        view.setOnClickListener {
            match?.let { activity.onMatchClicked(it) }
        }
    }
}

class RoundViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val text = view.findViewById<TextView>(android.R.id.text1)
}