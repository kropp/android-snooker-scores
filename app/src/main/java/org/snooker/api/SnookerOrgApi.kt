package org.snooker.api

import retrofit2.http.*
import retrofit2.Call

interface SnookerOrgApi {
    @GET("/")
    fun matchesOfEvent(@Query("e") eventId: String , @Query("t") requestType: String = "6"): Call<List<MatchData>>

    @GET("/")
    fun player(@Query("p") playerId: String): Call<List<PlayerData>>
}