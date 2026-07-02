package com.example.easypeasyenglish.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.navigation.Screen
import com.example.easypeasyenglish.utils.Dimensions
import com.example.easypeasyenglish.utils.logout
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxHeight
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.onMouseEnter
import com.varabyte.kobweb.compose.ui.modifiers.onMouseLeave
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.translateX
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.silk.components.icons.fa.FaBars
import com.varabyte.kobweb.silk.components.icons.fa.FaFacebook
import com.varabyte.kobweb.silk.components.icons.fa.FaInstagram
import com.varabyte.kobweb.silk.components.icons.fa.FaTiktok
import com.varabyte.kobweb.silk.components.icons.fa.FaXmark
import com.varabyte.kobweb.silk.components.icons.fa.FaYoutube
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.CSSpxValue
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgba
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text


@OptIn(DelicateApi::class)
@Composable
fun SidePanel(onClick: () -> Unit) {
    val breakpoint = rememberBreakpoint()
    if (breakpoint > Breakpoint.MD) {
        SidePanelInternal()
    } else {
        CollapsedSidePanel(onClick)
    }
}

@Composable
private fun SidePanelInternal() {
    Column(
        modifier = Modifier
            .padding(leftRight = 24.px, topBottom = 50.px)
            .width(Dimensions.SIDE_PANEL_WIDTH.px)
            .backgroundColor(Color.red)
            .fillMaxHeight()
            .position(Position.Fixed)
            .zIndex(9)
    ) {
        H1(
            attrs = Modifier
                .color(Colors.White)
                .fontSize(24.px)
                .fontWeight(FontWeight.ExtraBold)
                .textAlign(TextAlign.Center)
                .margin(bottom = 60.px)
                .toAttrs()
        ) {
            Text("EPE ACADEMY")
        }
        NavigationItems()
    }
}

@Composable
fun NavigationItems() {
    val context = rememberPageContext()
    NavigationItem(
        title = "Home",
        iconPath = "home.svg",
        isSelected = context.route.path == Screen.AdminHome.route,
        onClick = {
            context.router.navigateTo(Screen.AdminHome.route)
        }
    )
    NavigationItem(
        title = "Create a post",
        iconPath = "add_post.svg",
        isSelected = context.route.path == Screen.AdminCreatePost.route,
        onClick = {
            context.router.navigateTo(Screen.AdminCreatePost.route)
        }
    )
    NavigationItem(
        title = "My posts",
        iconPath = "sent_posts.svg",
        isSelected = context.route.path == Screen.AdminMyPosts.route,
        onClick = {
            context.router.navigateTo(Screen.AdminMyPosts.route)
        }
    )
    NavigationItem(
        title = "Logout",
        iconPath = "logout.svg",
        onClick = {
            logout()
            context.router.navigateTo(Screen.AdminLogin.route)
        }
    )
}

@Composable
private fun CollapsedSidePanel(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .maxHeight(100.px)
            .padding(leftRight = 24.px)
            .backgroundColor(Colors.Red),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaBars(
            modifier = Modifier
                .margin(right = 24.px)
                .color(Color.white)
                .cursor(Cursor.Pointer)
                .onClick { onMenuClick() },
            size = IconSize.XL
        )
        H1(
            attrs = Modifier
                .color(Colors.White)
                .fontSize(24.px)
                .fontWeight(FontWeight.ExtraBold)
                .textAlign(TextAlign.Center)
                .toAttrs()
        ) {
            Text("EPE ACADEMY")
        }
    }
}

@OptIn(DelicateApi::class)
@Composable
fun OverflowSidePanel(
    onMenuClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    var translateX by remember { mutableStateOf((-100).percent) }
    var opacity by remember { mutableStateOf(0.percent) }
    val context = rememberPageContext()

    LaunchedEffect(key1 = breakpoint) {
        translateX = 0.percent
        opacity = 100.percent
        if (breakpoint > Breakpoint.MD) {
            coroutineScope.launch {
                translateX = (-100).percent
                opacity = 0.percent
                delay(400)
                onMenuClose()
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .styleModifier {
                property("background-color", "rgba(0,0,0,0.5)")
                property("opacity", opacity.toString())
                property("transition", "opacity 400ms ease-in-out")
            }
            .position(Position.Fixed)
            .zIndex(9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .translateX(translateX)
                .width(if (breakpoint < Breakpoint.MD) 70.percent else 30.percent)
                .overflow(Overflow.Auto)
                .scrollBehavior(ScrollBehavior.Smooth)
                .styleModifier {
                    property("transform", "translateX(${translateX.value}%)")
                    property("opacity", opacity.toString())
                    property("transition", "transform 600ms ease-in-out, opacity 600ms ease-in-out")
                }
                .backgroundColor(Colors.White)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.px)
                    .backgroundColor(Colors.Red),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(leftRight = 16.px),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FaXmark(
                            modifier = Modifier
                                .color(Colors.White)
                                .cursor(Cursor.Pointer)
                                .onClick {
                                    coroutineScope.launch {
                                        translateX = (-100).percent
                                        opacity = 0.percent
                                        delay(600)
                                        onMenuClose()
                                    }
                                },
                            size = IconSize.LG
                        )
                    }
                    SpanText(
                        text = "EPE ACADEMY",
                        modifier = Modifier
                            .color(Colors.White)
                            .fontSize(28.px)
                            .fontWeight(FontWeight.Bold)
                            .cursor(Cursor.Pointer)
                            .onClick { context.router.navigateTo(Screen.HomePage.route) }
                    )
                }
            }
            SocialMediaLinks(onLinkClick = {
                coroutineScope.launch {
                    translateX = (-100).percent
                    opacity = 0.percent
                    delay(600)
                    onMenuClose()
                }
            })
            content()
        }
    }
}

@Composable
private fun NavigationItem(
    title: String,
    iconPath: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    val active = isSelected || isHovered
    val itemColor = if (active) Color.white else rgba(255,255,255,0.7)

    Div(
        attrs = Modifier
            .cursor(Cursor.Pointer)
            .onClick { onClick() }
            .onMouseEnter { isHovered = true }
            .onMouseLeave { isHovered = false }
            .margin(32.px)
            .toAttrs()
    ) {
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            VectorIconMasked(
                iconPath = iconPath,
                color = itemColor,
                modifier = Modifier.margin(right = 12.px)
            )
            SpanText(
                text = title,
                modifier = Modifier
                    .fontSize(16.px)
                    .color(itemColor)
            )
        }
    }
}

@Composable
fun SocialMediaLinks(onLinkClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(topBottom = 24.px),
        verticalArrangement = Arrangement.Center
    ) {
        SocialNavigationItem(
            label = "YouTube",
            icon = { FaYoutube(modifier = Modifier.color(Colors.Red), size = IconSize.LG) },
            onClick = { 
                window.open("https://www.youtube.com/channel/UCJJ8KFpya16lfWYar7vGB1A?sub_confirmation=1", "_blank")
                onLinkClick()
            }
        )
        SocialNavigationItem(
            label = "Facebook",
            icon = { FaFacebook(modifier = Modifier.color(Color("#1877F2")), size = IconSize.LG) },
            onClick = { 
                window.open("https://www.facebook.com/ahmad.shiravand.12", "_blank")
                onLinkClick()
            },
            modifier = Modifier.margin(top = 16.px)
        )
        SocialNavigationItem(
            label = "Instagram",
            icon = { FaInstagram(modifier = Modifier.color(Color("#E4405F")), size = IconSize.LG) },
            onClick = { 
                window.open("https://www.instagram.com/epeacademy?igsh=aXlubDJ5bXU3MGE=", "_blank")
                onLinkClick()
            },
            modifier = Modifier.margin(top = 16.px)
        )
        SocialNavigationItem(
            label = "TikTok",
            icon = { FaTiktok(modifier = Modifier.color(Colors.Black), size = IconSize.LG) },
            onClick = { 
                window.open("https://tiktok.com/@epeacademy", "_blank")
                onLinkClick()
            },
            modifier = Modifier.margin(top = 16.px)
        )
    }
}

@Composable
fun SocialNavigationItem(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(leftRight = 24.px, topBottom = 12.px)
            .cursor(Cursor.Pointer)
            .onClick { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(40.px)) {
            icon()
        }
        SpanText(
            text = label,
            modifier = Modifier
                .color(Colors.Black)
                .fontSize(18.px)
                .fontWeight(FontWeight.Bold)
        )
    }
}

@Composable
fun VectorIconMasked(
    iconPath: String,
    color: CSSColorValue,
    size: CSSpxValue = 24.px,
    modifier: Modifier = Modifier
) {
    Div(
        attrs = modifier
            .size(size)
            .toAttrs {
                style {
                    property("-webkit-mask", "url('/icons/$iconPath') no-repeat center / contain")
                    property("mask", "url('/icons/$iconPath') no-repeat center / contain")
                    property("background-color", color.toString())
                }
            }
    )
}