package com.example.easypeasyenglish.models

private fun String.htmlAttrEscape(): String = replace("\"", "&quot;")

sealed class ControlStyle(val style: String) {

    data class Bold(val selectedText: String?): ControlStyle(
        style = "<strong>${selectedText ?: ""}</strong>"
    )
    data class Italic(val selectedText: String?): ControlStyle(
        style = "<em>${selectedText ?: ""}</em>"
    )
    data class Link(
        val selectedText: String?,
        val href: String,
        val title: String
    ): ControlStyle(
        style = "<a href=\"$href\" title=\"$title\">${if (selectedText.isNullOrEmpty()) title else selectedText}</a>"
    )
    data class Title(val selectedText: String?): ControlStyle(
        style = "<h1><strong>${selectedText ?: ""}</strong></h1>"
    )
    data class Subtitle(val selectedText: String?): ControlStyle(
        style = "<h3>${selectedText ?: ""}</h3>"
    )
    data class Quote(val selectedText: String?): ControlStyle(
        style = "<div style=\"background-color:#FAFAFA;padding:12px;border-radius:6px\"><em>” ${selectedText ?: ""}</em></div>"
    )
    data class Code(val selectedText: String?): ControlStyle(
        style = """
    <div style="padding:12px; border-radius:6px; background-color:#0d1117;">
      <pre><code class="language-kotlin">${selectedText ?: ""}</code></pre>
    </div>
    """.trimIndent()
    )
    data class Image(
        val selectedText: String?,
        val imageUrl: String,
        val desc: String
    ): ControlStyle(
        style = "<div style=\"margin: 12px 0;\"><img src=\"${imageUrl.htmlAttrEscape()}\" alt=\"${desc.htmlAttrEscape()}\" style=\"max-width: 100%; display: block; border-radius: 6px;\" />${if (selectedText != null && selectedText.isNotEmpty()) "<p style=\"margin-top: 8px; color: #666;\">$selectedText</p>" else ""}</div>"
    )
}
