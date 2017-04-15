package org.snooker.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.snooker.android.repository
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.*

class SnookerOrgRepository {
    private val TAG = "API"

    private val service by lazy {
        val objectMapper = jacksonObjectMapper().enable(JsonParser.Feature.IGNORE_UNDEFINED)
        val jacksonFactory = JacksonConverterFactory.create(objectMapper)

        val retrofit = Retrofit.Builder()
//                .callFactory(noCacheCallFactory)
                .baseUrl("http://api.snooker.org/")
                .addConverterFactory(jacksonFactory)
                .build()

        retrofit.create(SnookerOrgApi::class.java)
    }

    suspend fun matches() = service.matchesOfEvent("536").await().map(::Match).sortedBy { it.date }

    private val players = mutableMapOf<String,Player>()
    suspend fun player(id: String): Player {
        if (id !in players) {
            players[id] = Player(service.player(id).await().first())
        }
        return players[id]!!
    }
}

class Match(private val data: MatchData) {
    suspend fun player1() = repository.player(data.Player1ID)
    suspend fun player2() = repository.player(data.Player2ID)
    val round: String
        get() = "Round ${data.Round}"
    val date: Date
        get() = data.ScheduledDate
}

class Player(private val data: PlayerData) {
    val name: String
        get() = if (data.SurnameFirst) {
            "${data.LastName} ${data.FirstName}"
        } else if (data.MiddleName.isNotEmpty()) {
            "${data.FirstName} ${data.MiddleName} ${data.LastName}"
        } else {
            "${data.FirstName} ${data.LastName}"
        }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlayerData(
    val ID: String,
    val FirstName: String,
    val MiddleName: String,
    val LastName: String,
    val ShortName: String,
    val SurnameFirst: Boolean
)