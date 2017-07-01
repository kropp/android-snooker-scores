package name.kropp.android.snooker

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import name.kropp.android.snooker.api.Event
import name.kropp.android.snooker.api.Match
import java.text.SimpleDateFormat
import java.util.*

class EventsListAdapter(private val activity: EventsActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val longDateFormat = DateFormat.getLongDateFormat(activity)

    var events = listOf<Event>()

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder) {
            val event = events[position]

            holder.flag.setImageResource(flagResource(event.country))
            holder.text.text = event.name
            holder.aux.text = "${longDateFormat.format(event.startDate)} — ${longDateFormat.format(event.endDate)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            EventViewHolder(activity, LayoutInflater.from(parent.context).inflate(R.layout.events_list_item, parent, false))
}