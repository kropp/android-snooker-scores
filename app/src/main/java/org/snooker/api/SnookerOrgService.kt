package org.snooker.api

import retrofit2.http.*
import retrofit2.Call

interface SnookerOrgService {
    @GET("/")
    fun matchesOfEvent(@Query("e") eventId: String , @Query("t") requestType: String = "6"): Call<List<Match>>
}