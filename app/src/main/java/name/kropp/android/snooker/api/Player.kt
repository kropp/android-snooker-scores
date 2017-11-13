package name.kropp.android.snooker.api

interface Player {
    val name: String
    val nationality: String
}

class PlayerFromApi(private val data: PlayerData) : Player {
    override val name: String
        get() = if (data.LastName.isEmpty()) {
            data.TeamName + if (data.TeamNumber > 0) " ${data.TeamNumber}" else ""
        } else if (data.SurnameFirst) {
            "${data.LastName} ${data.FirstName}"
        } else if (data.MiddleName.isNotEmpty()) {
            "${data.FirstName} ${data.MiddleName} ${data.LastName}"
        } else {
            "${data.FirstName} ${data.LastName}"
        }
    override val nationality: String get() = data.Nationality
}