package accounts

import com.google.gson.annotations.SerializedName

data class Players(
    @SerializedName("account_id")val account_id: Int,@SerializedName("nickname") val nickname: String)