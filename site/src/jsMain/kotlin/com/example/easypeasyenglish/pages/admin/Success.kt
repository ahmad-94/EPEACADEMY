package com.example.easypeasyenglish.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.easypeasyenglish.navigation.Screen
import com.example.easypeasyenglish.utils.Id.UPDATED_PARAM
import com.example.easypeasyenglish.utils.Res
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgba

@Page("/admin/success")
@Composable
fun Success() {
    val context = rememberPageContext()
    val postUpdated =  context.route.params.containsKey(UPDATED_PARAM)

    LaunchedEffect(Unit) {
        delay(5000)
        context.router.navigateTo(Screen.AdminCreatePost.route)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(158.px)
                .margin(bottom = 24.px),
            src = Res.Icon.CHECKMARK,
            description = "Checkmark Icon"
        )
        SpanText(
            modifier = Modifier
                .fontSize(24.px),
            text = if (postUpdated) "Post Successfully Updated!" else "Post Successfully Created!"
        )
        SpanText(
            modifier = Modifier
                .color(rgba(0,0,0,0.5))
                .fontSize(18.px),
            text = "Redirecting you back..."
        )
    }
}