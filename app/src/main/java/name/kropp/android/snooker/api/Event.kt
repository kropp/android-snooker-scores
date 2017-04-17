package name.kropp.android.snooker.api

import name.kropp.android.snooker.YMDDateFormat
import java.util.*

class Event(private val data: EventData, private val repository: SnookerOrgRepository) {
    var matches: List<Match> = emptyList()
    var rounds: Map<Long,String> = emptyMap()

    suspend fun fetchMatches() {
        val m = repository.matches(data.ID)
        val r = repository.rounds(data.ID)
        matches = m.await()
        rounds = r.await()
    }

    val id: Long get() = data.ID
    val name: String get() = data.Name
    val location: String get() = "${data.Venue} · ${data.City}, ${data.Country}"
    val country: String get() = data.Country
    val startDate: Date get() = YMDDateFormat.parse(data.StartDate)
    val endDate: Date get() = YMDDateFormat.parse(data.EndDate)
}