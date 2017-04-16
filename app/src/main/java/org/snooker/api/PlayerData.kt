package org.snooker.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlayerData(
        val ID: Long,
        val Type: Long,
        val FirstName: String,
        val MiddleName: String,
        val LastName: String,
        val TeamName: String,
        val TeamNumber: Int,
        val TeamSeason: Int,
        val ShortName: String,
        val Nationality: String,
        val Sex: String,
        val BioPage: String,
        val Born: String,
        val Twitter: String,
        val SurnameFirst: Boolean,
        val License: String,
        val Club: String,
        val URL: String,
        val Photo: String,
        val Info: String
)
