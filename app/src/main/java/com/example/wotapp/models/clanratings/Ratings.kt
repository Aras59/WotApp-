package com.example.wotapp.models.clanratings

data class Ratings(
    val battles_count_avg: BattlesCountAvg,
    val battles_count_avg_daily: BattlesCountAvgDaily,
    val clan_id: Int,
    val clan_name: String,
    val clan_tag: String,
    val efficiency: Efficiency,
    val exclude_reasons: ExcludeReasons,
    val fb_elo_rating: FbEloRating,
    val fb_elo_rating_10: FbEloRating10,
    val fb_elo_rating_6: FbEloRating6,
    val fb_elo_rating_8: FbEloRating8,
    val global_rating_avg: GlobalRatingAvg,
    val global_rating_weighted_avg: GlobalRatingWeightedAvg,
    val gm_elo_rating: GmEloRating,
    val gm_elo_rating_10: GmEloRating10,
    val gm_elo_rating_6: GmEloRating6,
    val gm_elo_rating_8: GmEloRating8,
    val rating_fort: RatingFort,
    val v10l_avg: V10lAvg,
    val wins_ratio_avg: WinsRatioAvg
)