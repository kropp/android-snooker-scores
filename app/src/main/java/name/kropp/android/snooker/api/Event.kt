package name.kropp.android.snooker.api

import name.kropp.android.snooker.YMDDateFormat
import java.util.*

open class Event(private val data: EventData) {
    val id: Long get() = data.ID
    val worldSnookerId: Long get() = data.WorldSnookerId
    val name: String get() = data.Name
    val location: String get() = "${data.Venue} · ${data.City}, ${data.Country}"
    val country: String get() = data.Country
    val startDate: Date get() = YMDDateFormat.parse(data.StartDate)
    val endDate: Date get() = YMDDateFormat.parse(data.EndDate)

    val isQualifying = data.Type == "Qualifying"
}

class EventComplete(data: EventData, val matches: List<Match>, val rounds: Map<Long,String>): Event(data) {}