package net.perfectdreams.loritta.morenitta.website.routes

import net.perfectdreams.loritta.common.locale.BaseLocale
import io.ktor.server.application.*
import net.perfectdreams.loritta.morenitta.platform.discord.LorittaDiscord
import net.perfectdreams.loritta.morenitta.website.utils.extensions.respondHtml
import net.perfectdreams.loritta.morenitta.website.views.SponsorsView

class SponsorsRoute(loritta: LorittaDiscord) : LocalizedRoute(loritta, "/sponsors") {
	override val isMainClusterOnlyRoute = true

	override suspend fun onLocalizedRequest(call: ApplicationCall, locale: BaseLocale) {
		call.respondHtml(
			SponsorsView(
				locale,
				getPathWithoutLocale(call),
			).generateHtml()
		)
	}
}