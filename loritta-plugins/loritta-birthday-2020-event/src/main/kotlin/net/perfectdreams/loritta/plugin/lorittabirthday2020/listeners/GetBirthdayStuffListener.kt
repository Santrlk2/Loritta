package net.perfectdreams.loritta.plugin.lorittabirthday2020.listeners

import com.google.common.cache.CacheBuilder
import com.mrpowergamerbr.loritta.dao.Background
import com.mrpowergamerbr.loritta.network.Databases
import com.mrpowergamerbr.loritta.utils.extensions.await
import com.mrpowergamerbr.loritta.utils.loritta
import com.mrpowergamerbr.loritta.utils.lorittaShards
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.plugin.lorittabirthday2020.LorittaBirthday2020
import net.perfectdreams.loritta.plugin.lorittabirthday2020.LorittaBirthday2020Event
import net.perfectdreams.loritta.plugin.lorittabirthday2020.tables.Birthday2020Drops
import net.perfectdreams.loritta.plugin.lorittabirthday2020.tables.Birthday2020Players
import net.perfectdreams.loritta.plugin.lorittabirthday2020.tables.CollectedBirthday2020Points
import net.perfectdreams.loritta.plugin.lorittabirthday2020.utils.BirthdayTeam
import net.perfectdreams.loritta.tables.BackgroundPayments
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.TimeUnit

class GetBirthdayStuffListener(val m: LorittaBirthday2020Event) : ListenerAdapter() {
	companion object {
		private val logger = KotlinLogging.logger {}
		private val mutexes = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build<Long, Mutex>()
				.asMap()
	}

	override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
		if (event.user.isBot)
			return

		if (event.reactionEmote.isEmoji) {
			if (event.reactionEmote.name !in LorittaBirthday2020.emojis)
				return
		} else
			if ("${event.reactionEmote.name}:${event.reactionEmote.idLong}" !in LorittaBirthday2020.emojis)
				return

		if (!LorittaBirthday2020.isEventActive())
			return

		GlobalScope.launch(loritta.coroutineDispatcher) {
			val lorittaProfile = loritta.getLorittaProfile(event.user.idLong) ?: return@launch

			val birthdayPlayerParticipation = transaction(Databases.loritta) {
				Birthday2020Players.select {
					Birthday2020Players.user eq lorittaProfile.id
				}.firstOrNull()
			} ?: return@launch

			val dropsForTheMessage = transaction(Databases.loritta) {
				Birthday2020Drops.select {
					Birthday2020Drops.messageId eq event.messageIdLong
				}.firstOrNull()
			} ?: return@launch

			val hasGotTheDrop = transaction(Databases.loritta) {
				(CollectedBirthday2020Points innerJoin Birthday2020Drops).select {
					CollectedBirthday2020Points.user eq event.userIdLong and
							(Birthday2020Drops.messageId eq event.messageIdLong)
				}.firstOrNull()
			}

			if (hasGotTheDrop != null)
				return@launch

			if (!event.reaction.retrieveUsers().await().any { it.id == loritta.discordConfig.discord.clientId })
				return@launch

			val rewards = if (birthdayPlayerParticipation[Birthday2020Players.team] == BirthdayTeam.PANTUFA) {
				LorittaBirthday2020.pantufaRewards
			} else {
				LorittaBirthday2020.gabrielaRewards
			}

			val mutex = mutexes.getOrPut(event.user.idLong, { Mutex() })

			mutex.withLock {
				val pointsCount = CollectedBirthday2020Points.points.sum()

				val currentCount = transaction(Databases.loritta) {
					CollectedBirthday2020Points.slice(CollectedBirthday2020Points.user, pointsCount).select {
						CollectedBirthday2020Points.user eq lorittaProfile.id
					}.count()
				}

				transaction(Databases.loritta) {
					CollectedBirthday2020Points.insert {
						it[user] = lorittaProfile.id
						it[message] = dropsForTheMessage[Birthday2020Drops.id]
						it[points] = 1
					}
				}

				val newCount = transaction(Databases.loritta) {
					CollectedBirthday2020Points.slice(CollectedBirthday2020Points.user, pointsCount).select {
						CollectedBirthday2020Points.user eq lorittaProfile.id
					}.count()
				}

				val newRewards = rewards.filter { it.requiredPoints in currentCount..newCount }

				newRewards.forEach {
					transaction(Databases.loritta) {
						if (it is LorittaBirthday2020.SonhosReward) {
							lorittaProfile.money += it.sonhosReward
						} else if (it is LorittaBirthday2020.BackgroundReward) {
							val internalName = it.internalName
							BackgroundPayments.insert {
								it[userId] = lorittaProfile.id.value
								it[cost] = 0
								it[background] = Background.findById(internalName)!!.id
								it[boughtAt] = System.currentTimeMillis()
							}
						}
					}
				}

				logger.info { "Sending stuff I guess idk lol" }
				lorittaShards.queryMasterLorittaCluster("/api/v1/birthday-2020/sync-points/${event.user.idLong}")
			}
		}
	}
}