package com.example.easypeasyenglish.components

import androidx.compose.runtime.Composable
import com.example.easypeasyenglish.models.EditorControl
import com.example.easypeasyenglish.utils.Id
import com.example.easypeasyenglish.utils.Theme
import com.example.easypeasyenglish.utils.loadDataUrlFromDisk
import com.example.easypeasyenglish.utils.noBorder
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input
import org.w3c.dom.HTMLInputElement

@Composable
fun MessagePopup(
    message: String,
    onDialogDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .position(Position.Fixed)
            .backgroundColor(Theme.darkGray)
            .onClick { onDialogDismiss() }
            .zIndex(19),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .backgroundColor(Colors.White)
                .padding(24.px)
                .maxWidth(400.px)
                .borderRadius(r = 4.px),
            contentAlignment = Alignment.Center
        ) {
            SpanText(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .fontSize(16.px)
                    .color(Colors.Black)
            )
        }
    }
}

@Composable
fun LinkPopup(
    editorControl: EditorControl,
    onLinkClickAdd: (String, String) -> Unit,
    onDialogDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(100.vw)
            .height(100.vh)
            .position(Position.Fixed)
            .backgroundColor(Colors.Transparent)
            .zIndex(19),
        contentAlignment = Alignment.Center
    ) {
       Box(
           modifier = Modifier
               .fillMaxSize()
               .onClick { onDialogDismiss() }
               .backgroundColor(Theme.lightGray),
       )
        Column(
            modifier = Modifier
                .padding(24.px)
                .width(500.px)
                .backgroundColor(Colors.White)
                .padding(24.px)
                .borderRadius(r = 4.px),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Input(
                type = InputType.Text,
                attrs = Modifier
                    .id(Id.LINK_HREF_INPUT)
                    .backgroundColor(Theme.lightGray)
                    .padding(left = 20.px)
                    .margin(24.px)
                    .height(54.px)
                    .fillMaxWidth()
                    .borderRadius(r = 4.px)
                    .noBorder()
                    .fontSize(14.px)
                    .toAttrs {
                        attr("placeholder", if (editorControl == EditorControl.Link) "Href" else "Image URL")
                    }
            )
            if (editorControl == EditorControl.Image) {
                Button(
                    attrs = Modifier
                        .margin(bottom = 12.px)
                        .height(54.px)
                        .padding(leftRight = 20.px)
                        .backgroundColor(Theme.lightGray)
                        .fillMaxWidth()
                        .noBorder()
                        .fontSize(14.px)
                        .onClick {
                            loadDataUrlFromDisk { dataUrl, _ ->
                                (document.getElementById(Id.LINK_HREF_INPUT) as HTMLInputElement).value = dataUrl
                            }
                        }
                        .toAttrs()
                ) {
                    SpanText("Upload image from disk")
                }
            }
            Input(
                type = InputType.Text,
                attrs = Modifier
                    .id(Id.LINK_TITLE_INPUT)
                    .padding(left = 20.px)
                    .height(54.px)
                    .margin(12.px)
                    .borderRadius(r = 4.px)
                    .backgroundColor(Theme.lightGray)
                    .fillMaxWidth()
                    .noBorder()
                    .fontSize(14.px)
                    .toAttrs {
                        attr("placeholder", if (editorControl == EditorControl.Link) "Title" else "Description")
                    }
            )

            Button(
                attrs = Modifier
                    .onClick {
                        val href = (document.getElementById(Id.LINK_HREF_INPUT) as HTMLInputElement).value
                        val title = (document.getElementById(Id.LINK_TITLE_INPUT) as HTMLInputElement).value
                        onLinkClickAdd(href, title)
                        onDialogDismiss()

                    }
                    .borderRadius(r = 4.px)
                    .padding(left = 20.px)
                    .height(54.px)
                    .backgroundColor(Colors.Blue)
                    .fillMaxWidth()
                    .noBorder()
                    .fontSize(14.px)
                    .toAttrs()
            ) {
                SpanText("Add")
            }
        }
    }
}