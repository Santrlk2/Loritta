package com.mrpowergamerbr.loritta.commands.vanilla.discord

import com.mrpowergamerbr.loritta.Loritta
import com.mrpowergamerbr.loritta.LorittaLauncher
import com.mrpowergamerbr.loritta.commands.AbstractCommand
import com.mrpowergamerbr.loritta.commands.CommandCategory
import com.mrpowergamerbr.loritta.commands.CommandContext
import com.mrpowergamerbr.loritta.utils.LorittaUtilsKotlin
import com.mrpowergamerbr.loritta.utils.locale.BaseLocale
import com.mrpowergamerbr.loritta.utils.lorittaShards
import net.dv8tion.jda.core.EmbedBuilder
import java.awt.Color
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

class BotInfoCommand : AbstractCommand("botinfo", category = CommandCategory.DISCORD) {
	override fun getDescription(locale: BaseLocale): String {
		return locale.get("BOTINFO_DESCRIPTION")
	}

	override fun run(context: CommandContext, locale: BaseLocale) {
		val embed = EmbedBuilder()

		var jvmUpTime = ManagementFactory.getRuntimeMXBean().uptime

		val days = TimeUnit.MILLISECONDS.toDays(jvmUpTime)
		jvmUpTime -= TimeUnit.DAYS.toMillis(days)
		val hours = TimeUnit.MILLISECONDS.toHours(jvmUpTime)
		jvmUpTime -= TimeUnit.HOURS.toMillis(hours)
		val minutes = TimeUnit.MILLISECONDS.toMinutes(jvmUpTime)
		jvmUpTime -= TimeUnit.MINUTES.toMillis(minutes)
		val seconds = TimeUnit.MILLISECONDS.toSeconds(jvmUpTime)

		val sb = StringBuilder(64)
		sb.append(days)
		sb.append("d ")
		sb.append(hours)
		sb.append("h ")
		sb.append(minutes)
		sb.append("m ")
		sb.append(seconds)
		sb.append("s")

		embed.setAuthor("${locale["BOTINFO_TITLE"]} 💁", Loritta.config.websiteUrl, "${Loritta.config.websiteUrl}assets/img/loritta_gabizinha_v1.png")
		embed.setThumbnail("${Loritta.config.websiteUrl}assets/img/loritta_gabizinha_v1.png")
		embed.setColor(Color(0, 193, 223))
		embed.setDescription(locale["BOTINFO_EMBED_INFO", lorittaShards.getGuildCount(), LorittaLauncher.loritta.lorittaShards.getUserCount(), sb.toString(), LorittaLauncher.loritta.commandManager.commandMap.size, lorittaShards.getChannelCount(), lorittaShards.getEmoteCount(), LorittaUtilsKotlin.executedCommands])
		embed.addField("\uD83C\uDF80 ${context.locale["WEBSITE_DONATE"]}", "${Loritta.config.websiteUrl}donate", true)
		embed.addField("<:loritta:331179879582269451> ${context.locale["WEBSITE_ADD_ME"]}", "${Loritta.config.websiteUrl}dashboard", true)
		embed.addField("<:lori_ok_hand:426183783008698391> ${context.locale["WEBSITE_COMMANDS"]}", "${Loritta.config.websiteUrl}commands", true)
		embed.addField("\uD83D\uDC81 ${context.locale["WEBSITE_Support"]}", "${Loritta.config.websiteUrl}support", true)
		embed.addField("\uD83C\uDFC5 ${context.locale.get("BOTINFO_HONORABLE_MENTIONS")}", context.locale.get("BOTINFO_MENTIONS", context.userHandle.name, context.userHandle.discriminator), false)
		embed.setFooter("${locale["BOTINFO_CREATEDBY"]} - https://mrpowergamerbr.com/", lorittaShards.getUserById("123170274651668480")!!.effectiveAvatarUrl)
		context.sendMessage(context.getAsMention(true), embed.build())
	}
}