package org.snooker.api

import retrofit2.http.*
import retrofit2.Call

interface SnookerOrgApi {
    @GET("/")
    fun matchesOfEvent(@Query("e") eventId: Long , @Query("t") requestType: String = "6"): Call<List<MatchData>>

    @GET("/")
    fun roundsOfEvent(@Query("e") eventId: Long , @Query("t") requestType: String = "12"): Call<List<RoundData>>

    @GET("/")
    fun player(@Query("p") playerId: Long): Call<List<PlayerData>>

    @GET("/")
    fun match(id: Long): Call<Match>
}