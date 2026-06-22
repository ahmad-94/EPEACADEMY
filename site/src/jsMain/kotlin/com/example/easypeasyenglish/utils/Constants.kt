package com.example.easypeasyenglish.utils

import org.jetbrains.compose.web.css.rgba


object Dimensions {
    const val SIDE_PANEL_WIDTH = 250
    const val PAGE_WIDTH = 1920
    const val COLLAPSED_PANEL_HEIGHT = 100
    const val HEADER_HEIGHT = 100
}

object Id {
    const val USERNAME_INPUT = "usernameInput"
    const val PASSWORD_INPUT = "passwordInput"

    const val EMAIL_INPUT = "emailInput"
    const val EDITOR ="editor"
    const val EDITOR_PREV = "editorPrev"
    const val TITLE_INPUT = "titleInput"
    const val THUMBNAIL_INPUT = "thumbnailInput"
    const val SUBTITLE_INPUT = "subtitleInput"
    const val LINK_HREF_INPUT = "linkHreInput"
    const val LINK_TITLE_INPUT = "linkTitleInput"
    const val ADMIN_SEARCH_BAR = "adminSearchBar"
    const val UPDATED_PARAM = "updated"
    const val postContent = "postContent"

}

object Res {
    object Icon {
        const val TITLE = "/icons/title.svg"
        const val SUBTITLE = "/icons/subtitle.svg"
        const val BOLD = "/icons/bold.svg"
        const val IMG = "/icons/img.svg"
        const val ITALIC = "/icons/italic.svg"
        const val LINK = "/icons/link.svg"
        const val CODE = "/icons/code.svg"
        const val QUOTE = "/icons/quote.svg"
        const val CHECKMARK = "/icons/checkmark.svg"
    }
}

object Theme {
    val darkGray = rgba(0, 0, 0, 0.8)
    val lightGray = rgba(0,0,0,0.2)
    val lightGray2 = rgba(0,0,0,0.1)
}


