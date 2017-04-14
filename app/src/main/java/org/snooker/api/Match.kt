package org.snooker.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Match {
    lateinit var ID: String
    lateinit var Player1ID: String
    lateinit var Player2ID: String
}