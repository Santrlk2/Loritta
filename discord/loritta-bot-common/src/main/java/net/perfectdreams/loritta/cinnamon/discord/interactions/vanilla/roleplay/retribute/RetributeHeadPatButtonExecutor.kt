package net.perfectdreams.loritta.cinnamon.discord.interactions.vanilla.roleplay.retribute

import net.perfectdreams.loritta.cinnamon.discord.LorittaCinnamon
import net.perfectdreams.loritta.cinnamon.discord.utils.ComponentExecutorIds
import net.perfectdreams.loritta.cinnamon.discord.interactions.vanilla.roleplay.RoleplayUtils
import net.perfectdreams.loritta.cinnamon.discord.interactions.components.ButtonExecutorDeclaration
import net.perfectdreams.randomroleplaypictures.client.RandomRoleplayPicturesClient

class RetributeHeadPatButtonExecutor(
    loritta: LorittaCinnamon,
    client: RandomRoleplayPicturesClient
) : RetributePictureExecutor(
    loritta,
    client,
    RoleplayUtils.HEAD_PAT_ATTRIBUTES
) {
    companion object : ButtonExecutorDeclaration(ComponentExecutorIds.RETRIBUTE_HEAD_PAT_BUTTON_EXECUTOR)
}