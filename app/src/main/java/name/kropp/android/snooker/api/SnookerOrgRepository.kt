package name.kropp.android.snooker.api

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
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

    init {
        val cache = try {
            Cache(File(context.cacheDir, "responses"), 10 * 1024 * 1024)
        } catch(e: IOException) {
            Log.i(TAG, "Failed to created HTTP cache", e)
            null
        }

        okHttpClient = OkHttpClient().newBuilder()
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .cache(cache).build()
    }

    private val service by lazy { createRetrofitService(okHttpClient) }

    private fun <R> withService(call: SnookerOrgApi.() -> Call<R>) = service.call().execute().body()

    private fun createRetrofitService(client: OkHttpClient) = Retrofit.Builder()
            .client(client)
            .baseUrl("http://api.snooker.org/")
            .addConverterFactory(jacksonFactory)
            .build().create(SnookerOrgApi::class.java)

    suspend fun events(cache: Boolean) = withService { events() }.map { Event(it, this@SnookerOrgRepository) }

    suspend fun event(id: Long, cache: Boolean) = Event(withService { event(id) }.first(), this@SnookerOrgRepository)

    suspend fun match(id: Long) = service.match(id).execute().body()//.map { Match(it, this) }.sortedBy { it.date }

    fun matches(id: Long, cache: Boolean) = async(CommonPool) {
        try {
            withService { matchesOfEvent(id) }
        } catch(e: Exception) {
            Log.i(TAG, "Error retrieving matches list for event $id", e)
            null
        }?.map { createMatch(it) } ?: emptyList()
    }

    fun ongoingMatches(cache: Boolean) = async(CommonPool) {
        try {
            withService { ongoingMatches() }
        } catch(e: Exception) {
            Log.i(TAG, "Error retrieving ongoing matches list", e)
            null
        }?.map { createMatch(it) } ?: emptyList()
    }

    fun rounds(id: Long, cache: Boolean) = async(CommonPool) {
        try {
            withService { roundsOfEvent(id) }
        } catch(e: Exception) {
            Log.i(TAG, "Error retrieving rounds list for event $id", e)
            null
        }?.associate { it.Round to it.RoundName + if (it.Distance > 0) " (Best of ${it.Distance*2-1})" else "" } ?: emptyMap()
    }

    private suspend fun createMatch(it: MatchData): Match {
        val player1 = player(it.Player1ID)
        val player2 = player(it.Player2ID)
        return Match(it, player1.await(), player2.await(), this@SnookerOrgRepository)
    }

    fun player(id: Long) = async(CommonPool) {
        val e = database.playerDao().findById(id)
        if (e != null) {
            e
        } else {
            val result = PlayerFromApi(withService { player(id) }.first())
            database.playerDao().add(PlayerEntity(id, result.name, result.nationality))
            result
        }
    }
}