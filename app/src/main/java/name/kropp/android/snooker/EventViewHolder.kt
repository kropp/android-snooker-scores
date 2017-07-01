package name.kropp.android.snooker

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import name.kropp.android.snooker.api.Event

class EventViewHolder(val activity: EventsActivity, val view: View): RecyclerView.ViewHolder(view) {
    val flag = view.findViewById<AppCompatImageView>(R.id.flag)
    val text = view.findViewById<TextView>(android.R.id.text1)
    val aux = view.findViewById<TextView>(android.R.id.text2)

    var event: Event? = null

    init {
        view.setOnClickListener {
            //match?.let { activity.onMatchClicked(it) }
        }
    }
}