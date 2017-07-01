package name.kropp.android.snooker.api

import retrofit2.http.*
import retrofit2.Call

interface SnookerOrgApi {
    @GET("/")
    fun events(@Query("s") season: Long = 2017, @Query("t") requestType: String = "5"): Call<List<EventData>>

    @GET("/")
    fun event(@Query("e") eventId: Long): Call<List<EventData>>

    @GET("/")
    fun ongoingMatches(@Query("t") requestType: String = "7"): Call<List<MatchData>>

    @GET("/")
    fun matchesOfEvent(@Query("e") eventId: Long, @Query("t") requestType: String = "6"): Call<List<MatchData>>

    @GET("/")
    fun roundsOfEvent(@Query("e") eventId: Long, @Query("t") requestType: String = "12"): Call<List<RoundData>>

    @GET("/")
    fun player(@Query("p") playerId: Long): Call<List<PlayerData>>

    @GET("/")
    fun match(id: Long): Call<Match>
}