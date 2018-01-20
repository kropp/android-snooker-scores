package name.kropp.android.snooker.api

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class SnookerOrgRepository(context: Context, private val database: AppDatabase) {
    private val TAG = "API"

    private val jacksonFactory = JacksonConverterFactory.create(
            jacksonObjectMapper()
                    .enable(JsonParser.Feature.IGNORE_UNDEFINED)
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true))

    private val okHttpClient: OkHttpClient
    private val service: SnookerOrgApi


    init {
        val cache = try {
            Cache(File(context.cacheDir, "responses"), 10 * 1024 * 1024)
        } catch (e: IOException) {
            Log.i(TAG, "Failed to created HTTP cache", e)
            null
        }

        okHttpClient = OkHttpClient().newBuilder()
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .cache(cache).build()

        service = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://api.snooker.org/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(jacksonFactory)
                .build().create(SnookerOrgApi::class.java)
    }

    suspend fun events(year: Long = 2017): List<Event> {
        val result = database.eventsDao().events(year)
        return if (result.any()) {
            result
        } else {
            val events = service.events(year).await()
            database.eventsDao().insert(events)
            events
        }.map { Event(it) }
    }

    suspend fun eventFast(id: Long) = async {
        val event = database.eventsDao().event(id) ?: service.event(id).await().first()
        EventComplete(event, database.matchesDao().matchesOfEvent(id).map { createMatch(it) }, database.eventsDao().rounds(id).associate { it.id to it.description })
    }

    suspend fun event(id: Long) = async {
        val event = database.eventsDao().event(id) ?: service.event(id).await().first()

        val rounds = rounds(id)
        val matches = matches(id)

        EventComplete(event, matches.await(), rounds.await())
    }

    suspend fun match(id: Long) = service.match(id).await() //.map { Match(it, this) }.sortedBy { it.date }

    fun matches(id: Long) = async {
        try {
            val matches = service.matchesOfEvent(id).await()
            database.matchesDao().insert(matches)
            matches
        } catch (e: Exception) {
            Log.i(TAG, "Error retrieving matches list for event $id: ${e.message}")
            null
        }?.map {
            database.matchesDao().insert(it)
            createMatch(it)
        } ?: emptyList()
    }

    fun ongoingMatches() = async {
        val ongoingMatches = service.ongoingMatches().await()
        database.matchesDao().insert(ongoingMatches)
        ongoingMatches.map { createMatch(it) }
    }

    fun rounds(id: Long) = async(CommonPool) {
        val rounds = database.eventsDao().rounds(id)
        if (rounds.isNotEmpty()) {
            rounds
        } else {
            val rounds = try {
                service.roundsOfEvent(id).await()
            } catch (e: Exception) {
                Log.i(TAG, "Error retrieving rounds list for event $id: ${e.message}")
                null
            }?.map { createRound(it) } ?: emptyList()
            rounds.forEach { database.eventsDao().insert(it) }
            rounds
        }.associate { it.id to it.description }
    }

    private fun createRound(it: RoundData) =
            Round(it.Round, it.EventID, it.RoundName + if (it.Distance > 0) " (Best of ${it.Distance * 2 - 1})" else "")

    private suspend fun createMatch(it: MatchData): Match {
        val player1 = player(it.Player1ID)
        val player2 = player(it.Player2ID)
        return Match(it, player1.await(), player2.await())
    }

    fun player(id: Long) = async {
        val e = database.playerDao().findById(id)
        if (e != null) {
            e
        } else {
            val result = PlayerFromApi(service.player(id).await().first())
            database.playerDao().add(PlayerEntity(id, result.name, result.nationality))
            result
        }
    }
}
