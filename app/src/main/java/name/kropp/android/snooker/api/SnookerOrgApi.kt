package name.kropp.android.snooker.api

import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface SnookerOrgApi {
    @GET("/")
    fun events(@Query("s") season: Long = 2017, @Query("t") requestType: String = "5"): Deferred<List<EventData>>

    @GET("/")
    fun event(@Query("e") eventId: Long): Deferred<List<EventData>>

    @GET("/")
    fun ongoingMatches(@Query("t") requestType: String = "7"): Deferred<List<MatchData>>

    @GET("/")
    fun matchesOfEvent(@Query("e") eventId: Long, @Query("t") requestType: String = "6"): Deferred<List<MatchData>>

    @GET("/")
    fun roundsOfEvent(@Query("e") eventId: Long, @Query("t") requestType: String = "12"): Deferred<List<RoundData>>

    @GET("/")
    fun player(@Query("p") playerId: Long): Deferred<List<PlayerData>>

    @GET("/")
    fun match(id: Long): Deferred<Match>

    @GET("/")
    fun rankings(@Query("rt") rankingType: String = "MoneyRankings", @Query("s") season: Long = 2017): Deferred<List<RankingData>>
}