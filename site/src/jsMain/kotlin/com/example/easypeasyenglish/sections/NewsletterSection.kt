package com.example.easypeasyenglish.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.components.MessagePopup
import com.example.easypeasyenglish.models.Newsletter
import com.example.easypeasyenglish.styles.NewsletterStyle
import com.example.easypeasyenglish.utils.Dimensions.PAGE_WIDTH
import com.example.easypeasyenglish.utils.Id
import com.example.easypeasyenglish.utils.NewsletterStateHolder
import com.example.easypeasyenglish.utils.Theme
import com.example.easypeasyenglish.utils.isEmailValid
import com.example.easypeasyenglish.utils.noBorder
import com.example.easypeasyenglish.utils.subscribeToNewsLetter
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import kotlinx.browser.document
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input
import org.w3c.dom.HTMLInputElement

@Composable
fun NewsletterSection(breakpoint: Breakpoint) {
    val scope = rememberCoroutineScope()
    val newsletterStateHolder = remember { NewsletterStateHolder(scope = scope) }

    if (newsletterStateHolder.showInvalidEmailPopup) {
        MessagePopup(
            message = "Email Address is Invalid.",
            onDialogDismiss = {
                newsletterStateHolder.dismissPopup()
            }
        )
    }

    if (newsletterStateHolder.showSuccessPopup) {
        MessagePopup(
            message = newsletterStateHolder.responseMessage,
            onDialogDismiss = {
                newsletterStateHolder.dismissPopup()
            }
        )
    }

    Box(
        modifier = Modifier
            .margin(topBottom = 250.px)
            .fillMaxWidth()
            .maxWidth(PAGE_WIDTH.px)
    ) {
        NewsletterContent(
            breakpoint = breakpoint,
            onInvalidEmail =  {
                newsletterStateHolder.showInvalidEmailPopup = true
            },
            onSubscribe = {
               scope.launch {
                   // Pass email UP to state holder
                   newsletterStateHolder.subscribe(it)
               }
            }
        )
    }
}


@Composable
fun NewsletterContent(
    breakpoint: Breakpoint,
    onSubscribe: (String) -> Unit,
    onInvalidEmail: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SpanText(
            modifier = Modifier
                .fillMaxWidth()
                .textAlign(TextAlign.Center)
                .fontWeight(FontWeight.Bold)
                .fontSize(36.px),
            text = "Don't miss any new post."
        )
        SpanText(
            modifier = Modifier
                .fillMaxWidth()
                .textAlign(TextAlign.Center)
                .fontWeight(FontWeight.Bold)
                .fontSize(36.px),
            text = "sing up to our newsletter."
        )
        SpanText(
            modifier = Modifier
                .margin(top = 6.px)
                .fontWeight(FontWeight.Normal)
                .fillMaxWidth()
                .textAlign(TextAlign.Center)
                .color(Colors.LightGray)
                .fontSize(36.px),
            text = "Keep up with news and posts."
        )

        if (breakpoint > Breakpoint.SM) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .margin(top = 40.px)
                ,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubscriptionForm(
                    vertical = false,
                    onSubscribe = onSubscribe,
                    onInvalidEmail = onInvalidEmail
                )
            }
        } else {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .margin(top = 40.px)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SubscriptionForm(
                    vertical = true,
                    onSubscribe = onSubscribe,
                    onInvalidEmail = onInvalidEmail
                )
            }
        }

    }
}

@Composable
fun SubscriptionForm(
    vertical: Boolean,
    onSubscribe: (String) -> Unit,
    onInvalidEmail: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Input(
        type = InputType.Text,
        attrs = NewsletterStyle.toModifier()
            .id(Id.EMAIL_INPUT)
            .width(320.px)
            .height(54.px)
            .color(Theme.darkGray)
            .backgroundColor(Theme.lightGray2)
            .padding(leftRight = 24.px)
            .margin(
                right = if (vertical)  0.px else 20.px,
                bottom = if (vertical) 20.px else 0.px
            )
            .fontSize(16.px)
            .borderRadius(100.px)
            .toAttrs {
                placeholder( "Enter Your Email Address")
            }
    )
    Button(
        attrs = Modifier
            .onClick {
                val email = (document.getElementById(Id.EMAIL_INPUT) as HTMLInputElement).value
//                if (isEmailValid(email = email)) {
//                    scope.launch {
//                        onSubscribe(
//                            subscribeToNewsLetter(newsletter = Newsletter(email = email))
//                        )
//                    }
//                } else {
//                    onInvalidEmail()
//                }

                if (isEmailValid(email)) {
                    // Event UP with data
                    onSubscribe(email)
                } else {
                    onInvalidEmail()
                }
            }
            .height(54.px)
            .borderRadius(100.px)
            .padding(leftRight = 50.px)
            .noBorder()
            .backgroundColor(Colors.Blue)
            .cursor(Cursor.Pointer)
            .toAttrs {  }
    ) {
        SpanText(
            modifier = Modifier
                .fontWeight(FontWeight.Normal)
                .color(Colors.White)
                .fontSize(18.px),
            text = "Subscribe"
        )
    }
}