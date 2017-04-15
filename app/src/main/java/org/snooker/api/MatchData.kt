package org.snooker.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchData(
    val ID: Long,
    val Round: Long,
    val Number: Long,
    val Player1ID: Long,
    val Player2ID: Long,
    val Score1: Long,
    val Score2: Long,
    var ScheduledDate: Date
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RoundData(
    val Round: Long,
    val RoundName: String
)