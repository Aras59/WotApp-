package clans.clandetails

import java.io.Serializable

data class Member(
    val account_id: Int,
    val account_name: String,
    val joined_at: Int,
    val role: String,
    val role_i18n: String
):Serializable