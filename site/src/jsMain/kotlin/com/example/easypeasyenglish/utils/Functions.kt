package com.example.easypeasyenglish.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.models.ControlStyle
import com.example.easypeasyenglish.models.ControlStyle.Bold
import com.example.easypeasyenglish.models.ControlStyle.Code
import com.example.easypeasyenglish.models.ControlStyle.Italic
import com.example.easypeasyenglish.models.ControlStyle.Quote
import com.example.easypeasyenglish.models.ControlStyle.Subtitle
import com.example.easypeasyenglish.models.ControlStyle.Title
import com.example.easypeasyenglish.models.EditorControl
import com.example.easypeasyenglish.navigation.Screen
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.outline
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import kotlin.js.Date

@Composable
fun isUserLoggedIn(content: @Composable () -> Unit) {
    val context = rememberPageContext()
    val remembered = remember { localStorage.getItem("remember").toBoolean() }
    val userId = remember { localStorage.getItem("userId") }
    var userIdExists by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        userIdExists = if (!userId.isNullOrEmpty()) checkUserId(userId) else false

        if (!userIdExists || !remembered) {
            context.router.navigateTo(Screen.AdminLogin.route)
        }
    }

    if (userIdExists && remembered) {
        content()
    } else {
        println("Loading....")
    }

}

fun logout() {
    localStorage.setItem("remember", "")
    kotlinx.browser.localStorage.setItem("userId", "")
    kotlinx.browser.localStorage.setItem("username", "")
}

fun Modifier.noBorder(): Modifier {
    return this.border(
            width = 0.px,
    style = LineStyle.None,
    color = Colors.Transparent
    )
    .outline(
        width = 0.px,
        style = LineStyle.None,
        color = Colors.Transparent
    )
}


fun getEditor() = document.getElementById(Id.EDITOR) as HTMLTextAreaElement


fun updatePreview(thumbnail: String = "") {
    val editor = document.getElementById(Id.EDITOR) as? HTMLTextAreaElement ?: return
    val preview = document.getElementById(Id.EDITOR_PREV) as? HTMLElement ?: return

    val resolvedThumbnail = thumbnail.ifBlank {
        (document.getElementById(Id.THUMBNAIL_INPUT) as? org.w3c.dom.HTMLInputElement)?.value.orEmpty()
    }
    val thumbnailHtml = resolvedThumbnail.takeIf { it.isNotBlank() }?.let { url ->
        """<img src="$url" alt="Thumbnail" style="max-width:100%;display:block;border-radius:6px;margin-bottom:12px;" />"""
    }.orEmpty()
    preview.innerHTML = thumbnailHtml + editor.value
}

fun syncEditorPreviewVisibility(showEditor: Boolean, thumbnail: String = "") {
    val editor = document.getElementById(Id.EDITOR) as? HTMLTextAreaElement ?: return
    val preview = document.getElementById(Id.EDITOR_PREV) as? HTMLElement ?: return

    if (showEditor) {
        editor.style.display = "block"
        editor.style.width = "100%"
        editor.style.height = "100%"
        editor.style.boxSizing = "border-box"
        preview.style.display = "none"
    } else {
        updatePreview(thumbnail = thumbnail)
        editor.style.display = "none"
        preview.style.display = "block"
        preview.style.width = "100%"
        preview.style.height = "100%"
        preview.style.boxSizing = "border-box"
    }
}

fun getSelectedIntRange(): IntRange? {
    val editor = getEditor()
    val start = editor.selectionStart
    val end = editor.selectionEnd
    return if (start != null && end != null) {
        IntRange(start = start, endInclusive = (end - 1))
    } else {
        null
    }
}

fun saveEditorSelection(): Pair<Int, Int> {
    val editor = getEditor()
    val start = editor.selectionStart ?: editor.value.length
    val end = editor.selectionEnd ?: start
    return start to end
}

fun getSelectedText(selection: Pair<Int, Int>? = null): String? {
    val editor = getEditor()
    if (selection != null) {
        val (start, end) = selection
        if (start == end) return null
        return editor.value.substring(minOf(start, end), maxOf(start, end))
    }
    val range = getSelectedIntRange() ?: return null
    if (range.isEmpty()) return null
    return editor.value.substring(range)
}

fun pairToIntRange(start: Int, end: Int): IntRange {
    return IntRange(
        start = minOf(start, end),
        endInclusive = maxOf(start, end) - 1,
    )
}

fun applyStyle(
    controlStyle: ControlStyle,
    selection: Pair<Int, Int>? = null,
) {
    val editor = getEditor()
    val selectedIntRange = selection?.let { (start, end) ->
        pairToIntRange(start, end)
    } ?: getSelectedIntRange()
    val selectedText = getSelectedText(selection)

    if (selectedIntRange != null && (selectedText != null || selection != null)) {
        editor.value = editor.value.replaceRange(
            range = selectedIntRange,
            replacement = controlStyle.style,
        )
    }
    updatePreview()
}

fun applyControlStyle(
    editorControl: EditorControl,
    onLinkClick: () -> Unit,
    onImageClick: () -> Unit
) {
    when(editorControl) {
        EditorControl.Bold -> {
            applyStyle(controlStyle = Bold(selectedText = getSelectedText()))
        }
        EditorControl.Italic -> {
            applyStyle(controlStyle = Italic(selectedText = getSelectedText()))
        }
        EditorControl.Title -> {
            applyStyle(controlStyle = Title(selectedText = getSelectedText()))
        }
        EditorControl.Subtitle -> {
            applyStyle(controlStyle = Subtitle(selectedText = getSelectedText()))
        }
        EditorControl.Quote -> {
            applyStyle(controlStyle = Quote(selectedText = getSelectedText()))
        }
        EditorControl.Link -> {
            onLinkClick()
        }
        EditorControl.Code -> {
            applyStyle(controlStyle = Code(selectedText = getSelectedText()))
        }
        EditorControl.Image -> {
            onImageClick()
        }
    }
}

fun Long.parseDateString() = Date(this).toDateString()

fun parseSwitchText(selectedPosts: List<String>): String {
    return if (selectedPosts.size == 1) "1 post selected" else "${selectedPosts.size} posts selected."
}



fun isEmailValid(email: String): Boolean {
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return regex.toRegex().matches(email)
}