package org.snooker.api

import android.content.ContentValues
import android.content.Context
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.snooker.db.SnookerOrgDbHelper
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.*

class SnookerOrgRepository(context: Context) {
    private val TAG = "API"
    private val dbHelper by lazy { SnookerOrgDbHelper(context) }
    private val db
        get() = dbHelper.writableDatabase

    val objectMapper: ObjectMapper = jacksonObjectMapper()
            .enable(JsonParser.Feature.IGNORE_UNDEFINED)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

    private val service by lazy {
        val jacksonFactory = JacksonConverterFactory.create(objectMapper)

        val retrofit = Retrofit.Builder()
//                .callFactory(noCacheCallFactory)
                .baseUrl("http://api.snooker.org/")
                .addConverterFactory(jacksonFactory)
                .build()

        retrofit.create(SnookerOrgApi::class.java)
    }

    suspend fun matches() = service.matchesOfEvent("536").await().map { Match(it, this) }.sortedBy { it.date }

    suspend fun player(id: String): Player {
        val cursor = db.query(SnookerOrgDbHelper.TABLE_PLAYERS, SnookerOrgDbHelper.COLUMN_PLAYERS, "ID = ?", arrayOf(id), null, null, null)
        if (cursor.count == 1) {
            cursor.moveToNext()
            return objectMapper.readValue<Player>(cursor.getString(1), Player::class.java)
        }

        val playerData = service.player(id).await().first()
        val json = objectMapper.writeValueAsString(playerData)
        val values = ContentValues()
        values.put("id", playerData.ID)
        values.put("json", json)
        db.insert(SnookerOrgDbHelper.TABLE_PLAYERS, null, values)
        return Player(playerData)
    }
}

class Match(private val data: MatchData, private val repository: SnookerOrgRepository) {
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
    val ID: Long,
    val FirstName: String,
    val MiddleName: String,
    val LastName: String,
    val ShortName: String,
    val SurnameFirst: Boolean
)