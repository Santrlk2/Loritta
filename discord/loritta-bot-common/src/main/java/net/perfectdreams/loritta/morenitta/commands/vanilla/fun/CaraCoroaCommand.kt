package net.perfectdreams.loritta.morenitta.commands.vanilla.`fun`

import net.perfectdreams.loritta.morenitta.Loritta
import net.perfectdreams.loritta.morenitta.commands.AbstractCommand
import net.perfectdreams.loritta.morenitta.commands.CommandContext
import net.perfectdreams.loritta.common.messages.LorittaReply
import net.perfectdreams.loritta.common.locale.BaseLocale
import net.perfectdreams.loritta.common.locale.LocaleKeyData
import net.perfectdreams.loritta.morenitta.utils.OutdatedCommandUtils

class CaraCoroaCommand : AbstractCommand("coinflip", listOf("girarmoeda", "flipcoin", "caracoroa"), net.perfectdreams.loritta.common.commands.CommandCategory.FUN) {
	companion object {
		const val LOCALE_PREFIX = "commands.command.flipcoin"
	}

	override fun getDescriptionKey() = LocaleKeyData("commands.command.flipcoin.description")

	override suspend fun run(context: CommandContext,locale: BaseLocale) {
		OutdatedCommandUtils.sendOutdatedCommandMessage(context, locale, "coinflip")

		val isTails = Loritta.RANDOM.nextBoolean()
		val prefix: String
		val message: String

		if (isTails) {
			prefix = "<:coroa:412586257114464259>"
			message = context.locale["$LOCALE_PREFIX.tails"]
		} else {
			prefix = "<:cara:412586256409559041>"
			message = context.locale["$LOCALE_PREFIX.heads"]
		}

		context.reply(
                LorittaReply(
                        "**$message!**",
                        prefix
                )
		)
	}
}