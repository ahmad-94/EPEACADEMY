package com.example.easypeasyenglish.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.components.CategoryMenuItems
import com.example.easypeasyenglish.components.SearchBar
import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.navigation.Screen
import com.example.easypeasyenglish.utils.Dimensions.HEADER_HEIGHT
import com.example.easypeasyenglish.utils.Id
import com.example.easypeasyenglish.utils.fetchSelectedPost
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.fa.FaBars
import com.varabyte.kobweb.silk.components.icons.fa.FaXmark
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import kotlinx.browser.document
import org.jetbrains.compose.web.css.SelectorsScope
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement

@Composable
fun HeaderSection(
    breakpoint: Breakpoint,
    selectedCategory: Category? = null,
    onMenuOpen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(Colors.Red),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(Colors.Red),
            contentAlignment = Alignment.Center

        ) {
            Header(breakpoint, selectedCategory = selectedCategory, onMenuOpen = onMenuOpen)
        }
    }
}

@Composable
fun Header(
    breakpoint: Breakpoint,
    selectedCategory: Category?,
    onMenuOpen: () -> Unit,
) {
    var fullSearchBarOpened by remember { mutableStateOf(false) }
    val context = rememberPageContext()
    Row(
        modifier = Modifier
            .fillMaxWidth(if (breakpoint > Breakpoint.MD) 80.percent else 90.percent)
            .height(HEADER_HEIGHT.px)
            .padding(leftRight = 24.px),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (fullSearchBarOpened) {
            FaXmark(
                modifier = Modifier
                    .margin(right = 24.px)
                    .size(24.px)
                    .color(Colors.White)
                    .cursor(Cursor.Pointer)
                    .onClick { fullSearchBarOpened = false },
                size = IconSize.XL
            )
        }

        if (!fullSearchBarOpened) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                H1(
                    attrs = Modifier
                        .color(Colors.White)
                        .fontSize(24.px)
                        .fontWeight(FontWeight.ExtraBold)
                        .margin(right = 32.px)
                        .toAttrs()
                ) {
                    if (breakpoint <= Breakpoint.MD) {
                        FaBars(
                            modifier = Modifier
                                .margin(right = 24.px)
                                .size(24.px)
                                .color(Colors.White)
                                .cursor(Cursor.Pointer)
                                .onClick {
                                    onMenuOpen()

                                }
                                .cursor(Cursor.Pointer),
                            size = IconSize.XL
                        )
                    }
                    SpanText(
                        "EPE ACADEMY",
                        modifier = Modifier
                            .cursor(Cursor.Pointer)
                            .onClick {
                                context.router.navigateTo(Screen.HomePage.route)
                            }
                    )
                }

//                if (breakpoint >= Breakpoint.LG) {
//                    CategoryMenuItems(selectedCategory = selectedCategory)
//                }
            }
        }

        Box(
            modifier = Modifier.flexGrow(1)
        )


        Box(
            modifier =
            if (fullSearchBarOpened) {
                Modifier.fillMaxWidth()
            } else {
                Modifier.width(260.px)
            },
            contentAlignment = Alignment.CenterEnd
        ) {
            SearchBar(
                fullWidth = fullSearchBarOpened,
                onEnterClick = {
                    val input = (document.getElementById(Id.ADMIN_SEARCH_BAR) as HTMLInputElement)
                    val query = input.value
                    input.blur()
                    context.router.navigateTo(Screen.SearchPageByTitle.searchPostsByTitle(query = query))
                },
                onSearchBarClick = { fullSearchBarOpened = it},
                breakpoint = breakpoint
            )
        }
    }
}

