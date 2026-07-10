package com.example.easypeasyenglish.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaFacebook
import com.varabyte.kobweb.silk.components.icons.fa.FaInstagram
import com.varabyte.kobweb.silk.components.icons.fa.FaTiktok
import com.varabyte.kobweb.silk.components.icons.fa.FaYoutube
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.px

@Composable
fun FooterSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(Colors.Red)
            .padding(topBottom = 50.px),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.margin(bottom = 30.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialIcon(
                    href = " https://tiktok.com/@epeacademy",
                    icon = { FaTiktok(size = IconSize.XL) }
                )
                SocialIcon(
                    href = "https://www.facebook.com/ahmad.shiravand.12",
                    icon = { FaFacebook(size = IconSize.XL) }
                )
                SocialIcon(
                    href = "https://www.instagram.com/epeacademy?igsh=aXlubDJ5bXU3MGE=",
                    icon = { FaInstagram(size = IconSize.XL) }
                )
                SocialIcon(
                    href = "https://www.youtube.com/channel/UCJJ8KFpya16lfWYar7vGB1A?sub_confirmation=1",
                    icon = { FaYoutube(size = IconSize.XL) }
                )


            }
            Row {
                SpanText(
                    modifier = Modifier
                        .fontWeight(FontWeight.Medium)
                        .color(Colors.White)
                        .fontSize(14.px),
                    text = "Copyright ©  2026 • "
                )
                SpanText(
                    modifier = Modifier
                        .fontWeight(FontWeight.Medium)
                        .color(Colors.BlanchedAlmond)
                        .fontSize(14.px),
                    text = "Easy Peasy English "
                )
            }
        }
    }
}

@Composable
private fun SocialIcon(href: String, icon: @Composable () -> Unit) {
    Link(
        modifier = Modifier
            .margin(leftRight = 15.px)
            .color(Colors.White)
            .textDecorationLine(com.varabyte.kobweb.compose.css.TextDecorationLine.None),
        path = href
    ) {
        icon()
    }
}
