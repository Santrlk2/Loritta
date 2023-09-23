package net.perfectdreams.loritta.cinnamon.dashboard.frontend.components

import androidx.compose.runtime.Composable
import net.perfectdreams.loritta.cinnamon.dashboard.frontend.LorittaDashboardFrontend
import net.perfectdreams.loritta.cinnamon.dashboard.frontend.utils.SVGIconManager
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.svg.SVGElement

@Composable
fun DiscordButton(
    type: DiscordButtonType,
    attrs: AttrBuilderContext<HTMLButtonElement>? = null,
    content: ContentBuilder<HTMLButtonElement>? = null
) = Button({
    attrs?.invoke(this)

    classes(
        "discord-button",
        when (type) {
            DiscordButtonType.PRIMARY -> "primary"
            DiscordButtonType.SUCCESS -> "success"
            DiscordButtonType.SECONDARY -> "secondary"
            DiscordButtonType.DANGER -> "danger"
            DiscordButtonType.NO_BACKGROUND_LIGHT_TEXT -> "no-background-light-text"
            DiscordButtonType.NO_BACKGROUND_DARK_TEXT -> "no-background-dark-text"
            DiscordButtonType.NO_BACKGROUND_THEME_DEPENDENT_LIGHT_TEXT -> "no-background-theme-dependent-light-text"
            DiscordButtonType.NO_BACKGROUND_THEME_DEPENDENT_DARK_TEXT -> "no-background-theme-dependent-dark-text"
        }
    )
}, content)

@Composable
fun TextWithIconWrapper(
    icon: SVGIconManager.SVGIcon,
    svgAttrs: AttrsScope<SVGElement>.() -> (Unit),
    content: ContentBuilder<HTMLDivElement>? = null
) = Div({
    classes("text-with-icon-wrapper")
}) {
    UIIcon(icon) {
        classes("text-icon")
        svgAttrs()
    }

    Div(content = content)
}

fun AttrsScope<HTMLButtonElement>.disabledWithSoundEffect(m: LorittaDashboardFrontend) {
    classes("disabled")
    attr("aria-disabled", "true")

    onClick {
        m.soundEffects.error.play(1.0)
    }
}

enum class DiscordButtonType {
    PRIMARY,
    SUCCESS,
    SECONDARY,
    DANGER,
    NO_BACKGROUND_LIGHT_TEXT,
    NO_BACKGROUND_DARK_TEXT,
    NO_BACKGROUND_THEME_DEPENDENT_LIGHT_TEXT,
    NO_BACKGROUND_THEME_DEPENDENT_DARK_TEXT,
}