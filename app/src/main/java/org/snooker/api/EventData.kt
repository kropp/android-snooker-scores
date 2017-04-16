package org.snooker.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class EventData(
        val ID: Long,
        val Name: String,
        val StartDate: String,
        val EndDate: String,
        val Sponsor: String,
        val Season: Int,
        val Type: String,
        val Num: Int,
        val Venue: String,
        val City: String,
        val Country: String,
        val Discipline: String,
        val Main: Long,
        val Sex: String,
        val AgeGroup: String,
        val Url: String,
        val Related: String,
        val Stage: String,
        val ValueType: String,
        val ShortName: String,
        val WorldSnookerId: Long,
        val RankingType: String,
        val EventPredictionID: Long,
        val Team: Boolean,
        val Format: Int,
        val Twitter: String,
        val HashTag: String,
        val ConversionRate: Double,
        val AllRoundsAdded: Boolean,
        val PhotoURLs: String,
        val NumCompetitors: Int,
        val NumUpcoming: Int,
        val NumActive: Int,
        val NumResults: Int,
        val Note: String,
        val CommonNote: String,
        val DefendingChampion: Long,
        val PreviousEdition: Long
)