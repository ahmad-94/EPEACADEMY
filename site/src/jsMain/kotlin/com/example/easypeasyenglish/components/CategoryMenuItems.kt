package com.example.easypeasyenglish.components

import CategoryItemStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.navigation.Screen
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.onMouseEnter
import com.varabyte.kobweb.compose.ui.modifiers.onMouseLeave
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.px

@Composable
fun CategoryMenuItems(
    selectedCategory: Category? = null,
    vertical: Boolean = false
) {
    val context = rememberPageContext()
    Category.entries.forEach { category ->
        var isHovered by remember { mutableStateOf(false) }

        Link(
            modifier = CategoryItemStyle.toModifier()
                .fontSize(16.px)
                .color(if (isHovered) Colors.Black else Colors.White)
                .onMouseEnter { isHovered = true }
                .onMouseLeave { isHovered = false }
                .onClick {
                    console.log("Clicked Category:", category)
                    context.router.navigateTo(
                        Screen.SearchPage.searchPostsByCategory(category = category)
                    )
                }
                .textDecorationLine(TextDecorationLine.None)
                .fontWeight(FontWeight.Medium)
                .thenIf(
                    condition = vertical,
                    other = Modifier.margin(bottom = 24.px)
                )
                .thenIf(
                    condition = !vertical,
                    other = Modifier.margin(right = 24.px)
                )
                .thenIf(
                    condition = selectedCategory == category,
                    other = Modifier.color(Colors.Black)
                )

              ,
            path = "",
            text = category.name
        )
    }
}