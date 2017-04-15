package org.snooker.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchData(
    val ID: String,
    val Round: String,
    val Player1ID: String,
    val Player2ID: String,
    var ScheduledDate: Date
)