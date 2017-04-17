package name.kropp.android.snooker.api

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class SnookerOrgRepository(context: Context) {
    private val TAG = "API"

    private val jacksonFactory = JacksonConverterFactory.create(
            jacksonObjectMapper()
            .enable(JsonParser.Feature.IGNORE_UNDEFINED)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true))

    private val okHttpClient: OkHttpClient
    private val cachingOkHttpClient: OkHttpClient

    init {
        val cache = try {
            Cache(File(context.cacheDir, "responses"), 10 * 1024 * 1024)
        } catch(e: IOException) {
            Log.i(TAG, "Failed to created HTTP cache", e)
            null
        }

        okHttpClient = OkHttpClient().newBuilder()
                .addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build())
                        .newBuilder().header("Cache-Control", "public, max-age=3600").build()
                }
                .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .cache(cache).build()

        cachingOkHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(0L, TimeUnit.MILLISECONDS)
                .readTimeout(0L, TimeUnit.MILLISECONDS)
                .addInterceptor { chain ->
                    val age = 60 * 60 * 24 * 30
                    chain.proceed(chain.request().newBuilder().cacheControl(CacheControl.FORCE_CACHE).build())
                        .newBuilder().header("Cache-Control", "public, max-age=$age, max-stale=$age").build()
                }
                .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .cache(cache).build()
    }

    private val service by lazy { createRetrofitService(okHttpClient) }
    private val cachingService by lazy { createRetrofitService(cachingOkHttpClient) }
    fun service(cache: Boolean) = if (cache) cachingService else service

    private fun createRetrofitService(client: OkHttpClient) = Retrofit.Builder()
            .client(client)
            .baseUrl("http://api.snooker.org/")
            .addConverterFactory(jacksonFactory)
            .build().create(SnookerOrgApi::class.java)

    suspend fun event(id: Long, cache: Boolean) = Event(service(cache).event(id).execute().body().first(), this@SnookerOrgRepository)

    suspend fun match(id: Long) = service.match(id).execute().body()//.map { Match(it, this) }.sortedBy { it.date }

    fun matches(id: Long, cache: Boolean) = async(CommonPool) {
        service(cache).matchesOfEvent(id).execute().body()?.map { createMatch(it) } ?: emptyList()
    }

    fun ongoingMatches(cache: Boolean) = async(CommonPool) {
        service(cache).ongoingMatches().execute().body()?.map { createMatch(it) } ?: emptyList()
    }

    fun rounds(id: Long, cache: Boolean) = async(CommonPool) {
        service(cache).roundsOfEvent(id).execute().body()?.associate { it.Round to "${it.RoundName} (Best of ${it.Distance*2-1})" } ?: emptyMap()
    }

    private suspend fun createMatch(it: MatchData): Match {
        val player1 = player(it.Player1ID)
        val player2 = player(it.Player2ID)
        return Match(it, player1.await(), player2.await(), this@SnookerOrgRepository)
    }

    fun player(id: Long) = async(CommonPool) {
        Player(cachingService.player(id).execute().body().first())
    }
}