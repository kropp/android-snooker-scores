package name.kropp.android.snooker.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RankingData(
    val ID: Long,
    val Position: Int,
    val PlayerID: Long,
    val Season: Long,
    val Sum: Long,
    val Type: String
)