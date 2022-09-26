package net.perfectdreams.loritta.morenitta.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class TwitterConfig(
        val oAuthConsumerKey: String,
        val oAuthConsumerSecret: String,
        val oAuthAccessToken: String,
        val oAuthAccessTokenSecret: String
)