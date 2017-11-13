package name.kropp.android.snooker.api

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.async
import name.kropp.android.snooker.YMDDateFormat
import java.util.*

class Event(private val data: EventData, private val repository: SnookerOrgRepository) {
    var matches: List<Match> = emptyList()
    var rounds: Map<Long,String> = emptyMap()

    fun rounds(): Deferred<Map<Long,String>> {
        if (rounds.isNotEmpty()) return async(Unconfined) { rounds }
        val result = repository.rounds(data.ID)
        result.invokeOnCompletion { rounds = result.getCompleted() }
        return result
    }

    suspend fun ongoingMatches(cache: Boolean) = repository.ongoingMatches(cache).await().filter { it.eventId == data.ID }

    fun matches(cache: Boolean): Deferred<List<Match>> {
        val result = repository.matches(data.ID, cache)
        result.invokeOnCompletion { matches = result.getCompleted() }
        return result
    }

    val id: Long get() = data.ID
    val worldSnookerId: Long get() = data.WorldSnookerId
    val name: String get() = data.Name
    val location: String get() = "${data.Venue} · ${data.City}, ${data.Country}"
    val country: String get() = data.Country
    val startDate: Date get() = YMDDateFormat.parse(data.StartDate)
    val endDate: Date get() = YMDDateFormat.parse(data.EndDate)

    val isQualifying = data.Type == "Qualifying"
}