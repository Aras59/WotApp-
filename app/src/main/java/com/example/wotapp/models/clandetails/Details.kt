package com.example.wotapp.models.clandetails

data class Details(
    val accepts_join_requests: Boolean,
    val clan_id: Int,
    val color: String,
    val created_at: Int,
    val creator_id: Int,
    val creator_name: String,
    val description: String,
    val description_html: String,
    val emblems: Emblems,
    val is_clan_disbanded: Boolean,
    val leader_id: Int,
    val leader_name: String,
    val members: List<Member>,
    val members_count: Int,
    val motto: String,
    val name: String,
    val old_name: String,
    val old_tag: String,
    val `private`: Any,
    val renamed_at: Int,
    val tag: String,
    val updated_at: Int
)