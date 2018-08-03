package com.mrpowergamerbr.loritta.listeners

import com.mrpowergamerbr.loritta.utils.loritta
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ChannelListener : ListenerAdapter() {
	override fun onTextChannelCreate(event: TextChannelCreateEvent) {
		if (event.channel.canTalk()) {
			loritta.executor.execute {
				val config = loritta.getServerConfigForGuild(event.guild.id)

				if (config.miscellaneousConfig.enableQuirky) {
					event.channel.sendMessage("First!").queue()
				}
			}
		}
	}
}