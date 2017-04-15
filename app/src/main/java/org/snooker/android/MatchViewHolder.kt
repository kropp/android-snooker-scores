package org.snooker.android

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.experimental.Job
import org.snooker.api.Match

class MatchViewHolder(val activity: MainActivity, val view: View): RecyclerView.ViewHolder(view) {
    val first = view.findViewById(android.R.id.text1) as TextView
    val second = view.findViewById(android.R.id.text2) as TextView
    val auxiliary = view.findViewById(R.id.auxiliaryText) as TextView

    var job: Job? = null
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