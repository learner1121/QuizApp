// data/model/UserProfile.kt
package com.gautam.quiz_app.data.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("userId")       val userId       : String  = "",
    @SerializedName("name")         val name         : String  = "",
    @SerializedName("email")        val email        : String  = "",
    @SerializedName("photoUrl")     val photoUrl     : String  = "",
    @SerializedName("rank")         val rank         : Int     = 0,
    @SerializedName("totalPlayed")  val totalPlayed  : Int     = 0,
    @SerializedName("highestScore") val highestScore : Int     = 0,
    @SerializedName("averageScore") val averageScore : Float   = 0f,
    @SerializedName("bestSection")  val bestSection  : String  = "",
    @SerializedName("worstSection") val worstSection : String  = ""
)