package accounts

data class Player(
    val account_id: Int,
    val clan_id: Int,
    val client_language: String,
    val created_at: Int,
    val global_rating: Int,
    val last_battle_time: Int,
    val logout_at: Int,
    val nickname: String,
    val private: Any,
    val statistics: Statistics,
    val updated_at: Int
)