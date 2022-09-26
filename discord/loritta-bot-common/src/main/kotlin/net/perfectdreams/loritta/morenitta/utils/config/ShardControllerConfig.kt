package net.perfectdreams.loritta.morenitta.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class ShardControllerConfig(
		val enabled: Boolean,
		val url: String,
		val buckets: Int
)