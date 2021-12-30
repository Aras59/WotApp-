package players.playerstats

import java.io.Serializable

data class Statistics(
    val all: All,
    val clan: Clan,
    val company: Company,
    val frags: Any,
    val historical: Historical,
    val regular_team: RegularTeam,
    val stronghold_defense: StrongholdDefense,
    val stronghold_skirmish: StrongholdSkirmish,
    val team: Team,
    val trees_cut: Int
):Serializable