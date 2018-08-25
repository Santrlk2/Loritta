package com.mrpowergamerbr.loritta.utils.config

import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game

data class LorittaConfig(
		val mongoDbIp: String,
		val lavalinkIp: String,
		val shards: Int,
		val clientToken: String,
		val clientId: String,
		val clientSecret: String,
		val userStatus: OnlineStatus,
		val databaseName: String,
		val environment: EnvironmentType,
		val youtubeKeys: List<String>,
		val websiteApiKeys: List<AuthenticationKey>,
		val ownerId: String,
		val websiteUrl: String,
		val websitePort: Int,
		val socketPort: Int,
		val lorittaFolder: String,
		val assetsFolder: String,
		val tempFolder: String,
		val localesFolder: String,
		val frontendFolder: String,
		val authorizationUrl: String,
		val addBotUrl: String,
		val mercadoPagoClientId: String,
		val mercadoPagoClientToken: String,
		val mashapeKey: String,
		val discordBotsKey: String,
		val discordBotsOrgKey: String,
		val openWeatherMapKey: String,
		val aminoEmail: String,
		val aminoPassword: String,
		val aminoDeviceId: String,
		val facebookToken: String,
		val googleVisionKey: String,
		val simsimiKey: String,
		val myAnimeListAuth: String,
		val patreonClientId: String,
		val patreonClientSecret: String,
		val patreonAccessToken: String,
		val patreonRefreshToken: String,
		val twitchClientId: String,
		val mixerClientId: String,
		val mixerClientSecret: String,
		val mixerWebhookSecret: String,
		val apoiaSeCookies: String,
		val recaptchaToken: String,
		val pomfSpaceToken: String,
		val vagalumeKey: String,
		val ghostIds: List<String>,
		val fanArtExtravaganza: Boolean,
		val fanArts: List<LorittaAvatarFanArt>,
		val currentlyPlaying: List<LorittaGameStatus>) {
	constructor() : this("10.0.0.3",
			"10.0.0.22",
			20,
			"Token do Bot",
			"Client ID do Bot",
			"Client Secret do Bot",
			OnlineStatus.ONLINE,
			"Nome da Database no MongoDB",
			EnvironmentType.PRODUCTION,
			listOf(),
			listOf(),
			"ID do dono do bot, usado para alguns comandos \"especiais\"",
			"Website do Bot",
			4568,
			10699,
			"Pasta da Loritta (normalmente a pasta \"root\", ou seja, a mesma pasta aonde fica a JAR)",
			"Pasta de assets da Loritta (imagens, fontes, etc)",
			"Pasta temporária da Loritta",
			"Pasta de locales (traduções) da Loritta",
			"Pasta da frontend do Bot",
			"URL para autorização no painel, usado para pedir o OAuth2 code do usuário",
			"URL para adicionar a Loritta",
			"Client ID do MercadoPago",
			"Client Token do MercadoPago",
			"Key do Mashape",
			"Key do Discord Bots",
			"Key do Discord Bots (discordbots.org)",
			"Key do Open Weather Map",
			"Email de uma conta do Amino",
			"Senha de uma conta do Amino",
			"Device ID de uma conta do Amino",
			"Token da API do Facebook",
			"Key do Google Vision",
			"Key do Simsimi",
			"Autenticação do MyAnimeList",
			"Client ID do Patreon",
			"Client Secret do Patreon",
			"Access Token do Patreon",
			"Refresh Token do Patreon",
			"Client ID do Twitch",
			"Client ID do Mixer",
			"Client Secret do Mixer",
			"Webhook Secret do Mixer, utilizado para codificar Webhooks",
			"Cookie do apoia.se",
			"Token do No Captcha reCAPTCHA",
			"Token do pomf.space",
			"Key do Vagalume",
			listOf<String>(),
			true,
			listOf<LorittaAvatarFanArt>(),
			listOf(LorittaGameStatus("Shantae: Half-Genie Hero", Game.GameType.DEFAULT.name)))

	class LorittaGameStatus(val name: String, val type: String)

	class LorittaAvatarFanArt(val fileName: String, val artistId: String, val fancyName: String?)

	class AuthenticationKey(val name: String, val description: String, val allowed: List<String>)
}