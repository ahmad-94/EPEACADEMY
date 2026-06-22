package com.example.easypeasyenglish.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.utils.Id
import com.example.easypeasyenglish.utils.noBorder
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.onFocusIn
import com.varabyte.kobweb.compose.ui.modifiers.onFocusOut
import com.varabyte.kobweb.compose.ui.modifiers.onKeyDown
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.fa.FaMagnifyingGlass
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Input

@Composable
fun SearchBar(
    onEnterClick: () -> Unit,
    onSearchBarClick: (Boolean) -> Unit,
    fullWidth: Boolean = true,
    breakpoint: Breakpoint,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }

    LaunchedEffect(breakpoint) {
        if (breakpoint >= Breakpoint.SM) onSearchBarClick(false)
    }

    if (breakpoint >= Breakpoint.SM || fullWidth) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(leftRight = 20.px)
                .height(54.px)
                .backgroundColor(Colors.WhiteSmoke)
                .borderRadius(r = 100.px)
                .border(
                    width = 2.px,
                    style = LineStyle.Solid,
                    color = if (focused) Colors.SteelBlue else Colors.LightGray
                )
                .styleModifier {
                    property("transition", "border-color 0.3s ease-in-out, background-color 0.3s ease-in-out")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            FaMagnifyingGlass(
                modifier = Modifier
                    .color(if (focused) Colors.SteelBlue else Colors.DarkGray)
                    .styleModifier {
                        property("transition", "color 0.3s ease-in-out")
                    }
                    .margin(right = 14.px),
                size = IconSize.SM
            )
            Input(
                type = InputType.Text,
                attrs = Modifier
                    .id(Id.ADMIN_SEARCH_BAR)
                    .fillMaxSize()
                    .color(Colors.Black)
                    .backgroundColor(Colors.Transparent)
                    .noBorder()
                    .onFocusIn {
                        focused = true
                    }
                    .onFocusOut {
                        focused = false
                    }
                    .onKeyDown {
                        if (it.key == "Enter") {
                            onEnterClick()
                        }
                    }
                    .toAttrs{
                        attr("placeholder", "Search here...")
                        attr("autocomplete", "off")
                    }
            )
        }
    } else {
        FaMagnifyingGlass(
            modifier = Modifier
                .color(if (focused) Colors.SteelBlue else Colors.DarkGray)
                .onClick { onSearchBarClick(true) }
                .margin(right = 14.px),
            size = IconSize.SM
        )
    }
}