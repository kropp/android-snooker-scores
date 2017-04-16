package name.kropp.android.snooker.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RoundData(
    val Round: Long,
    val RoundName: String,
    val EventID: Long,
    val MainEvent: Long,
    val Distance: Int,
    val NumLeft: Int,
    val NumMatches: Int,
    val Note: String,
    val ValueType: String,
    val Rank: Int,
    val Money: Int,
    val SeedGetsHalf: Int,
    val ActualMoney: Int,
    val Currency: String,
    val ConversionRate: Double,
    val Points: Int,
    val SeedPoints: Int
)