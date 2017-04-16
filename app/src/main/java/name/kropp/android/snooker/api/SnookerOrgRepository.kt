package name.kropp.android.snooker.api

import android.content.ContentValues
import android.content.Context
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import name.kropp.android.snooker.YMDDateFormat
import name.kropp.android.snooker.db.SnookerOrgDbHelper
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

    suspend fun event(id: Long): Event {
        val data = service.event(id)
        val matches = matches(id)
        val rounds = rounds(id)
        return Event(data.await().first(), matches.await(), rounds.await(), this)
    }

    suspend fun match(id: Long) = service.match(id).await()//.map { Match(it, this) }.sortedBy { it.date }

    fun matches(id: Long) = async(CommonPool) {
        service.matchesOfEvent(id).await().map {
            val player1 = player(it.Player1ID)
            val player2 = player(it.Player2ID)
            Match(it, player1.await(), player2.await(), this@SnookerOrgRepository)
        }
    }

    fun rounds(id: Long) = async(CommonPool) {
        service.roundsOfEvent(id).await().associate { it.Round to "${it.RoundName} (Best of ${it.Distance*2-1})" }
    }

    fun player(id: Long) = async(CommonPool) {
        val cursor = db.query(SnookerOrgDbHelper.Companion.TABLE_PLAYERS, SnookerOrgDbHelper.Companion.COLUMN_PLAYERS, "ID = ?", arrayOf(id.toString()), null, null, null)
        if (cursor.count == 1) {
            cursor.moveToNext()
            val json = cursor.getString(1)
            cursor.close()

            objectMapper.readValue<Player>(json, Player::class.java)
        } else {
            cursor.close()

            val playerData = service.player(id).await().first()
            val json = objectMapper.writeValueAsString(playerData)
            val values = ContentValues()
            values.put("id", playerData.ID)
            values.put("json", json)
            db.insert(SnookerOrgDbHelper.Companion.TABLE_PLAYERS, null, values)

            Player(playerData)
        }
    }
}

class Event(private val data: EventData, val matches: List<Match>, val rounds: Map<Long,String>, private val repository: SnookerOrgRepository) {
    val name: String get() = data.Name
    val location: String get() = "${data.Venue} · ${data.City}, ${data.Country}"
    val country: String get() = data.Country
    val startDate: Date get() = YMDDateFormat.parse(data.StartDate)
    val endDate: Date get() = YMDDateFormat.parse(data.EndDate)
}

class Match(private val data: MatchData, val player1: Player, val player2: Player, private val repository: SnookerOrgRepository) {
    val id get() = data.ID
    val number get() = data.Number
    val score1 get() = data.Score1
    val score2 get() = data.Score2
    val round get() = data.Round
    val date: Date
        get() = data.ScheduledDate
    val isStarted: Boolean
        get() = data.Unfinished
    val isFinished: Boolean
        get() = !data.Unfinished && data.WinnerID != 0L
    val isActive: Boolean
        get() = data.Unfinished && !data.OnBreak
    val isPlayer1Winner: Boolean
        get() = data.WinnerID == data.Player1ID
    val isPlayer2Winner: Boolean
        get() = data.WinnerID == data.Player2ID
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
    val nationality: String get() = data.Nationality
}

