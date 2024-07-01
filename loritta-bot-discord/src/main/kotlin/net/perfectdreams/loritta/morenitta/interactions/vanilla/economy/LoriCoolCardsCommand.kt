package net.perfectdreams.loritta.morenitta.interactions.vanilla.economy

import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.perfectdreams.loritta.cinnamon.discord.interactions.commands.styled
import net.perfectdreams.loritta.cinnamon.emotes.Emotes
import net.perfectdreams.loritta.cinnamon.pudding.tables.loricoolcards.LoriCoolCardsEventCards
import net.perfectdreams.loritta.cinnamon.pudding.tables.loricoolcards.LoriCoolCardsEvents
import net.perfectdreams.loritta.cinnamon.pudding.tables.loricoolcards.LoriCoolCardsSeenCards
import net.perfectdreams.loritta.cinnamon.pudding.tables.loricoolcards.LoriCoolCardsUserOwnedCards
import net.perfectdreams.loritta.common.commands.CommandCategory
import net.perfectdreams.loritta.common.utils.TodoFixThisData
import net.perfectdreams.loritta.i18n.I18nKeysData
import net.perfectdreams.loritta.morenitta.LorittaBot
import net.perfectdreams.loritta.morenitta.interactions.UnleashedButton
import net.perfectdreams.loritta.morenitta.interactions.UnleashedContext
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import net.perfectdreams.loritta.morenitta.interactions.commands.options.OptionReference
import net.perfectdreams.loritta.morenitta.loricoolcards.StickerAlbumTemplate
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.awt.Color
import java.time.Instant

class LoriCoolCardsCommand(private val loritta: LorittaBot) : SlashCommandDeclarationWrapper {
    companion object {
        private val I18N_PREFIX = I18nKeysData.Commands.Command.Loricoolcards
    }

    val stickStickers = LoriCoolCardsStickStickersExecutor(loritta, this)
    val buyStickers = LoriCoolCardsBuyStickersExecutor(loritta, this)
    val viewAlbum = LoriCoolCardsViewAlbumExecutor(loritta, this)

    override fun command() = slashCommand(I18N_PREFIX.Label, TodoFixThisData, CommandCategory.ECONOMY) {
        this.enableLegacyMessageSupport = true
        this.integrationTypes = listOf(Command.IntegrationType.GUILD_INSTALL, Command.IntegrationType.USER_INSTALL)

        subcommand(I18N_PREFIX.Buy.Label, I18N_PREFIX.Buy.Description) {
            // Buy
            executor = buyStickers
        }

        subcommand(I18N_PREFIX.View.Label, I18N_PREFIX.View.Description) {
            // View Figurinhas Stats
            executor = LoriCoolCardsViewExecutor()
        }

        subcommand(I18N_PREFIX.Stick.Label, I18N_PREFIX.Stick.Description) {
            // Stick stickers
            executor = stickStickers
        }

        subcommand(I18N_PREFIX.Album.Label, I18N_PREFIX.Album.Description) {
            // View album
            executor = viewAlbum
        }

        subcommand(I18N_PREFIX.Stats.Label, I18N_PREFIX.Stats.Description) {
            // Event stats
            executor = LoriCoolCardsStatsExecutor(loritta, this@LoriCoolCardsCommand)
        }

        subcommand(I18N_PREFIX.Compare.Label, I18N_PREFIX.Compare.Description) {
            // Compare stickers
            executor = LoriCoolCardsCompareStickersExecutor(loritta, this@LoriCoolCardsCommand)
        }

        subcommand(I18N_PREFIX.Duplicates.Label, I18N_PREFIX.Duplicates.Description) {
            // Duplicate stickers
            executor = LoriCoolCardsDuplicateStickersInventoryExecutor(loritta, this@LoriCoolCardsCommand)
        }

        subcommand(I18N_PREFIX.Missing.Label, I18N_PREFIX.Missing.Description) {
            // Missing stickers
            executor = LoriCoolCardsMissingStickersInventoryExecutor(loritta, this@LoriCoolCardsCommand)
        }

        subcommand(I18N_PREFIX.Give.Label, I18N_PREFIX.Give.Description) {
            // Give stickers
            executor = LoriCoolCardsGiveStickersExecutor(loritta, this@LoriCoolCardsCommand)
        }

        subcommand(I18N_PREFIX.Trade.Label, I18N_PREFIX.Trade.Description) {
            // Trade stickers
            executor = LoriCoolCardsTradeStickersExecutor(loritta, this@LoriCoolCardsCommand)
        }
    }

    inner class LoriCoolCardsViewExecutor : LorittaSlashCommandExecutor(), LorittaLegacyMessageCommandExecutor {
        inner class Options : ApplicationCommandOptions() {
            val cardId = string("sticker_id", I18N_PREFIX.View.Options.Sticker.Text) {
                autocomplete {
                    val now = Instant.now()
                    val focusedOptionValue = it.event.focusedOption.value

                    // We also let searchingByCardId = true if empty to make the autocomplete results be sorted from 0001 -> ... by default
                    val searchingByCardId = focusedOptionValue.startsWith("#") || focusedOptionValue.isEmpty() || focusedOptionValue.toIntOrNull() != null

                    return@autocomplete loritta.transaction {
                        val event = LoriCoolCardsEvents.select {
                            LoriCoolCardsEvents.endsAt greaterEq now and (LoriCoolCardsEvents.startsAt lessEq now)
                        }.firstOrNull() ?: return@transaction mapOf()

                        if (searchingByCardId) {
                            var searchQuery = focusedOptionValue
                            if (searchQuery.toIntOrNull() != null) {
                                searchQuery = "#${searchQuery.toInt().toString().padStart(4, '0')}"
                            }

                            val cardEventCardsMatchingQuery = LoriCoolCardsEventCards.select {
                                LoriCoolCardsEventCards.fancyCardId.like(
                                    "${searchQuery.replace("%", "")}%"
                                ) and (LoriCoolCardsEventCards.event eq event[LoriCoolCardsEvents.id])
                            }.limit(25).orderBy(LoriCoolCardsEventCards.fancyCardId, SortOrder.ASC).toList()

                            val cardIds = cardEventCardsMatchingQuery.map { it[LoriCoolCardsEventCards.id] }

                            val seenCards = LoriCoolCardsSeenCards.select {
                                (LoriCoolCardsSeenCards.user eq it.event.user.idLong) and (LoriCoolCardsSeenCards.card inList cardIds)
                            }.map { it[LoriCoolCardsSeenCards.card].value }

                            val results = mutableMapOf<String, String>()
                            for (card in cardEventCardsMatchingQuery) {
                                if (card[LoriCoolCardsEventCards.id].value in seenCards) {
                                    results["${card[LoriCoolCardsEventCards.fancyCardId]} - ${card[LoriCoolCardsEventCards.title]}"] =
                                        card[LoriCoolCardsEventCards.fancyCardId]
                                } else {
                                    results["${card[LoriCoolCardsEventCards.fancyCardId]} - ???"] =
                                        card[LoriCoolCardsEventCards.fancyCardId]
                                }
                            }
                            results
                        } else {
                            val cardEventCardsMatchingQuery = LoriCoolCardsEventCards.select {
                                LoriCoolCardsEventCards.title.like(
                                    "${
                                        focusedOptionValue.replace(
                                            "%",
                                            ""
                                        )
                                    }%"
                                ) and (LoriCoolCardsEventCards.event eq event[LoriCoolCardsEvents.id])
                            }.limit(25).orderBy(LoriCoolCardsEventCards.title, SortOrder.ASC).toList()

                            val cardIds = cardEventCardsMatchingQuery.map { it[LoriCoolCardsEventCards.id] }

                            val seenCards = LoriCoolCardsSeenCards.select {
                                (LoriCoolCardsSeenCards.user eq it.event.user.idLong) and (LoriCoolCardsSeenCards.card inList cardIds)
                            }.map { it[LoriCoolCardsSeenCards.card].value }

                            val results = mutableMapOf<String, String>()
                            for (card in cardEventCardsMatchingQuery) {
                                if (card[LoriCoolCardsEventCards.id].value in seenCards) {
                                    results["${card[LoriCoolCardsEventCards.fancyCardId]} - ${card[LoriCoolCardsEventCards.title]}"] =
                                        card[LoriCoolCardsEventCards.fancyCardId]
                                } else {
                                    results["${card[LoriCoolCardsEventCards.fancyCardId]} - ???"] =
                                        card[LoriCoolCardsEventCards.fancyCardId]
                                }
                            }
                            results
                        }
                    }
                }
            }
        }

        override val options = Options()

        override suspend fun execute(context: UnleashedContext, args: SlashCommandArguments) {
            val now = Instant.now()
            val fancyCardId = args[options.cardId]

            val result = loritta.transaction {
                val event = LoriCoolCardsEvents.select {
                    LoriCoolCardsEvents.endsAt greaterEq now and (LoriCoolCardsEvents.startsAt lessEq now)
                }.firstOrNull() ?: return@transaction GetCardInfoResult.EventUnavailable

                val cardEventCard = LoriCoolCardsEventCards.select {
                    LoriCoolCardsEventCards.fancyCardId eq fancyCardId and (LoriCoolCardsEventCards.event eq event[LoriCoolCardsEvents.id])
                }.limit(1).firstOrNull() ?: return@transaction GetCardInfoResult.UnknownCard

                val template = Json.decodeFromString<StickerAlbumTemplate>(event[LoriCoolCardsEvents.template])

                val isSticked = LoriCoolCardsUserOwnedCards.select {
                    LoriCoolCardsUserOwnedCards.card eq cardEventCard[LoriCoolCardsEventCards.id] and (LoriCoolCardsUserOwnedCards.sticked eq true) and (LoriCoolCardsUserOwnedCards.user eq context.user.idLong)
                }.count() != 0L

                val isSeen = LoriCoolCardsSeenCards.select {
                    LoriCoolCardsSeenCards.card eq cardEventCard[LoriCoolCardsEventCards.id] and (LoriCoolCardsSeenCards.user eq context.user.idLong)
                }.count() != 0L

                val cardsOfThisTypeInCirculation = LoriCoolCardsUserOwnedCards.select {
                    LoriCoolCardsUserOwnedCards.card eq cardEventCard[LoriCoolCardsEventCards.id] and (LoriCoolCardsUserOwnedCards.sticked eq false)
                }.count()

                val cardsOfThisTypeSticked = LoriCoolCardsUserOwnedCards.select {
                    LoriCoolCardsUserOwnedCards.card eq cardEventCard[LoriCoolCardsEventCards.id] and (LoriCoolCardsUserOwnedCards.sticked eq true)
                }.count()

                val cardsOfThisTypeOwnedByTheCurrentUser = LoriCoolCardsUserOwnedCards.select {
                    LoriCoolCardsUserOwnedCards.card eq cardEventCard[LoriCoolCardsEventCards.id] and (LoriCoolCardsUserOwnedCards.user eq context.user.idLong) and (LoriCoolCardsUserOwnedCards.sticked eq false)
                }.count()

                val stickersWeightsInThisEvent = LoriCoolCardsEventCards
                    .select(LoriCoolCardsEventCards.id, LoriCoolCardsEventCards.rarity)
                    .where {
                        LoriCoolCardsEventCards.event eq event[LoriCoolCardsEvents.id]
                    }
                    .map { template.stickerProbabilityWeights[it[LoriCoolCardsEventCards.rarity]]!! } // Should NEVER be null!

                return@transaction GetCardInfoResult.Success(
                    template,
                    cardEventCard,
                    isSeen,
                    isSticked,
                    cardsOfThisTypeOwnedByTheCurrentUser,
                    cardsOfThisTypeInCirculation,
                    cardsOfThisTypeSticked,
                    stickersWeightsInThisEvent
                )
            }

            when (result) {
                GetCardInfoResult.EventUnavailable -> {
                    context.reply(true) {
                        styled(
                            "Nenhum evento de figurinhas ativo"
                        )
                    }
                }
                GetCardInfoResult.UnknownCard -> {
                    context.reply(false) {
                        styled(
                            "Figurinha não existe!"
                        )
                    }
                    return
                }
                is GetCardInfoResult.Success -> {
                    val albumStickerPage = result.template.getAlbumPageThatHasSticker(result.card[LoriCoolCardsEventCards.fancyCardId])!!

                    context.reply(false) {
                        embed {
                            val title = result.card[LoriCoolCardsEventCards.title]
                            val cardId = result.card[LoriCoolCardsEventCards.fancyCardId]
                            val cardFrontImageUrl = result.card[LoriCoolCardsEventCards.cardFrontImageUrl]
                            val cardReceivedImageUrl = result.card[LoriCoolCardsEventCards.cardReceivedImageUrl]

                            this.title = buildString {
                                if (result.isSeen) {
                                    append(result.card[LoriCoolCardsEventCards.rarity].emoji)
                                } else {
                                    append(Emotes.StickerRarityUnknown)
                                }
                                append(" ")
                                append(cardId)
                                append(" - ")
                                if (result.isSeen) {
                                    append(title)
                                } else {
                                    append("???")
                                }
                            }

                            this.description = buildString {
                                if (result.isSticked) {
                                    appendLine("# ${context.i18nContext.get(I18N_PREFIX.View.YouHaveThisStickerInYourAlbum)}")
                                } else {
                                    appendLine("# ${context.i18nContext.get(I18N_PREFIX.View.YouDontHaveThisStickerInYourAlbum)}")
                                }
                                appendLine(context.i18nContext.get(I18N_PREFIX.View.StickersInYourInventory(result.cardsOfThisTypeOwnedByTheCurrentUserNotSticked)))
                                appendLine(context.i18nContext.get(I18N_PREFIX.View.StickersInCirculation(result.cardsInCirculation)))
                                appendLine(context.i18nContext.get(I18N_PREFIX.View.StickersStickedInAlbums(result.cardsInAlbums)))
                                appendLine(context.i18nContext.get(I18N_PREFIX.View.StickersTotal(result.cardsInCirculation + result.cardsInAlbums)))
                                appendLine(context.i18nContext.get(I18N_PREFIX.View.StickerPage(albumStickerPage)))

                                val totalWeight = result.stickersWeightsInThisEvent.sum()
                                val chanceOfSticker = result.template.stickerProbabilityWeights[result.card[LoriCoolCardsEventCards.rarity]]!! / totalWeight

                                appendLine(context.i18nContext.get(I18N_PREFIX.View.ChanceOfGettingTheSticker(chanceOfSticker)))
                            }

                            if (result.isSeen) {
                                this.color = result.card[LoriCoolCardsEventCards.rarity].color.rgb
                            } else {
                                this.color = Color(47, 47, 47).rgb
                            }

                            val frontFacingStickerUrl = UnleashedButton.of(
                                ButtonStyle.LINK,
                                context.i18nContext.get(I18N_PREFIX.View.StickerFront)
                            )

                            val animatedStickerUrl = UnleashedButton.of(
                                ButtonStyle.LINK,
                                context.i18nContext.get(I18N_PREFIX.View.StickerAnimated)
                            )

                            if (result.isSeen) {
                                this.image = cardReceivedImageUrl

                                actionRow(
                                    frontFacingStickerUrl.withUrl(cardFrontImageUrl),
                                    animatedStickerUrl.withUrl(cardReceivedImageUrl)
                                )
                            } else {
                                this.image = result.template.unknownStickerImageUrl

                                actionRow(
                                    frontFacingStickerUrl.asDisabled(),
                                    animatedStickerUrl.asDisabled()
                                )
                            }
                        }
                    }
                }
            }
        }

        override suspend fun convertToInteractionsArguments(
            context: LegacyMessageCommandContext,
            args: List<String>
        ): Map<OptionReference<*>, Any?>? {
            TODO("Not yet implemented")
        }
    }

    sealed class GetCardInfoResult {
        data object EventUnavailable : GetCardInfoResult()
        data object UnknownCard : GetCardInfoResult()
        class Success(
            val template: StickerAlbumTemplate,
            val card: ResultRow,
            val isSeen: Boolean,
            val isSticked: Boolean,
            val cardsOfThisTypeOwnedByTheCurrentUserNotSticked: Long,
            val cardsInCirculation: Long,
            val cardsInAlbums: Long,
            val stickersWeightsInThisEvent: List<Double>
        ) : GetCardInfoResult()
    }
}