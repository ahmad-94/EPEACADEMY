package com.example.easypeasyenglish.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.styles.PostPreviewStyle
import com.example.easypeasyenglish.utils.Theme
import com.example.easypeasyenglish.utils.parseDateString
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.ObjectFit
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.objectFit
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.CheckboxInput

@Composable
fun PostPreview(
    post: PostWithoutDetails,
    selectable: Boolean = false,
    vertical: Boolean = true,
    isMainSection: Boolean? = null,
    thumbnailHeight: CSSSizeValue<CSSUnit.px> = 350.px,
    titleMaxLine: Int = 2,
    onSelect: (String) -> Unit = {},
    onDeSelect: (String) -> Unit = {},
    onClick: (String) -> Unit = {}
) {
    var checked by remember(selectable) { mutableStateOf(false) }
    val context = rememberPageContext()
    if (vertical) {
        Column(
            modifier = PostPreviewStyle.toModifier()
                .padding(if (selectable) 8.px else 0.px)
                .margin(8.px)
                .cursor(Cursor.Pointer)
                .border(
                    width = if (selectable) 4.px else 0.px,
                    style = if (selectable) LineStyle.Solid else LineStyle.None,
                    color = if (checked) Colors.Blue else Theme.lightGray
                )
                .onClick {
                    if (selectable) {
                        checked = !checked
                        if (checked) {
                            onSelect(post.postId)
                        } else {
                            onDeSelect(post.postId)
                        }
                    } else {
                        onClick(post.postId)

                    }
                }
                .styleModifier {
                    property("opacity", if (checked) "0.7" else "1")
                    property(
                        "transition",
                        "border-color 200ms ease-in-out, border-width 200ms ease-in-out"
                    )
                }
        ) {
            postContent(
                post = post,
                selectable = selectable,
                isMainSection = isMainSection,
                checked = checked,
                vertical = vertical,
                titleMaxLine = titleMaxLine,
                thumbnailHeight = thumbnailHeight,

                )
        }
    } else {
        Row(
            modifier = PostPreviewStyle
                .toModifier()
                .fillMaxWidth()
                .onClick { onClick(post.postId) }
                .cursor(Cursor.Pointer)
        ) {

            postContent(
                post = post,
                selectable = selectable,
                checked = checked,
                vertical = vertical,
                isMainSection = isMainSection,
                titleMaxLine = titleMaxLine,
                thumbnailHeight = thumbnailHeight,
            )
        }
    }
}

@Composable
fun postContent(
    post: PostWithoutDetails,
    selectable: Boolean,
    isMainSection: Boolean?,
    checked: Boolean,
    vertical: Boolean,
    titleMaxLine: Int,
    thumbnailHeight: CSSSizeValue<CSSUnit.px>,
) {
    Image(
        src = post.thumbnail,
        description = "Post Thumbnail Image",
        modifier = Modifier
            .margin(
                bottom = if (isMainSection == true && !vertical) 18.px else 20.px
            )
            .thenIf(
                condition = !(isMainSection == true && !vertical),
                other = Modifier
                    .fillMaxWidth()
                    .maxWidth(1280.px)
            )
            .thenIf(
                condition = isMainSection == true && !vertical,
                other = Modifier
                    .maxWidth(250.px)
            )
            .height(thumbnailHeight)
            .objectFit(ObjectFit.Fill)
    )
    Column(
        modifier = Modifier
            .thenIf(
                condition = !vertical,
                other = Modifier.margin(left = 20.px)
            )

    ) {
        SpanText(
            modifier = Modifier
                .fontSize(14.px)
                .color(Theme.lightGray),
            text = post.date.parseDateString()
        )
        SpanText(
            modifier = Modifier
                .fontSize(24.px)
                .color(Colors.Black)
                .textOverflow(TextOverflow.Ellipsis)
                .overflow(Overflow.Hidden)
                .fontWeight(FontWeight.Bold)
                .styleModifier {
                    property("display", "-webkit-box")
                    property("-webkit-line-clamp", "$titleMaxLine")
                    property("line-clamp","2")
                    property("-webkit-box-orient", "vertical")
                }
            ,
            text = post.title
        )
        SpanText(
            modifier = Modifier
                .margin(bottom = 10.px)
                .fontSize(18.px)
                .color(Colors.Black)
                .textOverflow(TextOverflow.Ellipsis)
                .overflow(Overflow.Hidden)
                .styleModifier {
                    property("display", "-webkit-box")
                    property("-webkit-line-clamp", "3")
                    property("line-clamp","3")
                    property("-webkit-box-orient", "vertical")
                }
            ,
            text = post.subtitle ?: ""
        )
        Row(
            modifier = PostPreviewStyle.toModifier()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
//            CategoryChip(category = post.category)
            if (selectable) {
                CheckboxInput(
                    checked = checked,
                    attrs = Modifier
                        .size(20.px)
                        .toAttrs()
                )
            }
        }
    }
}

