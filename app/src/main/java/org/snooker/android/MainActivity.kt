package org.snooker.android

import org.snooker.api.SnookerOrgService
import org.snooker.api.Match

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_main.*

import retrofit2.*
import retrofit2.converter.jackson.JacksonConverterFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonParser

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val objectMapper = ObjectMapper().enable(JsonParser.Feature.IGNORE_UNDEFINED)
        val jacksonFactory = JacksonConverterFactory.create(objectMapper)

        val retrofit = Retrofit.Builder()
//                .callFactory(noCacheCallFactory)
                .baseUrl("http://api.snooker.org/")
                .addConverterFactory(jacksonFactory)
                .build()

        val service = retrofit.create(SnookerOrgService::class.java);

        val call = service.matchesOfEvent("536")

        call.enqueue(object : retrofit2.Callback<List<Match>> {
            override fun onResponse(call: Call<List<Match>>, response: Response<List<Match>>) {
                val matches = response.body()
                tmp.text = matches.map { it.ID }.joinToString(",")
            }
            override fun onFailure(call: Call<List<Match>>, e: Throwable) {
                tmp.text = e.message
            }
        })
    }
}