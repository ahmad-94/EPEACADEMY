package com.example.easypeasyenglish.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.web.events.SyntheticMouseEvent
import com.example.easypeasyenglish.components.AdminPageLayout
import com.example.easypeasyenglish.components.LinkPopup
import com.example.easypeasyenglish.components.MessagePopup
import com.example.easypeasyenglish.models.ApiResponse
import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.ControlStyle
import com.example.easypeasyenglish.models.ControlStyle.Link
import com.example.easypeasyenglish.models.EditorControl
import com.example.easypeasyenglish.models.Params.POST_ID_PARAM
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.navigation.Screen
import com.example.easypeasyenglish.styles.EditorKeyStyle
import com.example.easypeasyenglish.utils.Id
import com.example.easypeasyenglish.utils.addPost
import com.example.easypeasyenglish.utils.applyControlStyle
import com.example.easypeasyenglish.utils.applyStyle
import com.example.easypeasyenglish.utils.fetchSelectedPost
import com.example.easypeasyenglish.utils.getSelectedText
import com.example.easypeasyenglish.utils.isUserLoggedIn
import com.example.easypeasyenglish.utils.loadDataUrlFromDisk
import com.example.easypeasyenglish.utils.noBorder
import com.example.easypeasyenglish.utils.saveEditorSelection
import com.example.easypeasyenglish.utils.syncEditorPreviewVisibility
import com.example.easypeasyenglish.utils.updatePost
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.Resize
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.disabled
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.onMouseEnter
import com.varabyte.kobweb.compose.ui.modifiers.onMouseLeave
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.resize
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.visibility
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgba
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import kotlin.js.Date

data class CreatePageUiState(
    var id: String = "",
    var title: String = "",
    var subtitle: String? = null,
    var thumbnail: String = "",
    var content: String = "",
    var buttonText: String = "Create",
    var selectedCategory: Category = Category.Programming,
    var popularSwitch: Boolean = false,
    var mainSwitch: Boolean = false,
    var sponsoredSwitch: Boolean = false,
    var thumbnailInputDisabled: Boolean = true,
    var editorVisibility: Boolean = true,
    var messagePopup: Boolean = false,
    var linkPopup: Boolean = false,
    var imagePopup: Boolean = false,


) {
    fun reset() = this.copy(
        id = "",
        title = "",
        subtitle = null,
        thumbnail = "",
        content = "",
        buttonText = "Create",
        selectedCategory = Category.Programming,
        popularSwitch = false,
        mainSwitch = false,
        sponsoredSwitch = false,
        thumbnailInputDisabled = true,
        editorVisibility = true,
        messagePopup = false,
        linkPopup= false,
        imagePopup = false
    )
}


@Page("/admin/createpost")
@Composable
fun CreatePage() {
    isUserLoggedIn {
        CreateScreen()
    }
}

@OptIn(DelicateApi::class)
@Composable
fun CreateScreen() {

    val breakpoint = rememberBreakpoint()
    val context = rememberPageContext()
    val scope = rememberCoroutineScope()
    var uiState by remember { mutableStateOf(CreatePageUiState()) }
    var savedEditorSelection by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val hasPostIdParam = remember(context.route) { context.route.params.containsKey(POST_ID_PARAM) }

    LaunchedEffect(hasPostIdParam) {
        if (hasPostIdParam) {

            val postId = context.route.params[POST_ID_PARAM] ?: ""
            val response = fetchSelectedPost(id = postId)
            if (response is ApiResponse.Success) {
                (document.getElementById(Id.EDITOR) as HTMLTextAreaElement).value = response.data.content
                uiState = uiState.copy(
                    id = response.data.postId,
                    title = response.data.title,
                    subtitle = response.data.subtitle,
                    buttonText = "Update",
                    thumbnail = response.data.thumbnail,
                    content = response.data.content,
                    selectedCategory = response.data.category,
                    popularSwitch = response.data.popular,
                    mainSwitch = response.data.main,
                    sponsoredSwitch = response.data.sponsored
                )
            }
        } else {
            (document.getElementById(Id.EDITOR) as HTMLTextAreaElement).value = ""
            uiState = uiState.reset()
        }
    }
    AdminPageLayout {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .margin(topBottom = 50.px)
                .padding(
                    left = if (breakpoint > Breakpoint.MD) 250.px else 0.px
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .maxWidth(700.px),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SimpleGrid(numColumns = numColumns(1, 3)) {
                    Row(
                        modifier = Modifier
                            .margin(
                                right = if (breakpoint < Breakpoint.SM) 0.px else 48.px,
                                bottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked = uiState.popularSwitch,
                            onCheckedChange = { uiState = uiState.copy(popularSwitch = it) },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .size(14.px)
                                .color(rgba(0,0,0, 0.5)),
                            text = "Popular",
                        )
                    }
                    Row(
                        modifier = Modifier
                            .margin(
                                right = if (breakpoint < Breakpoint.MD) 0.px else 48.px,
                                bottom = if (breakpoint < Breakpoint.MD) 12.px else 0.px
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked =  uiState.mainSwitch,
                            onCheckedChange = {
                                uiState =  uiState.copy(mainSwitch = it)
                            },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .size(14.px)
                                .color(rgba(0,0,0, 0.5)),
                            text = "Main"
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked = uiState.sponsoredSwitch,
                            onCheckedChange = {
                                uiState = uiState.copy(sponsoredSwitch = it)
                            },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .size(14.px)
                                .color(rgba(0,0,0, 0.5)),
                            text = "Sponsored"
                        )
                    }

                }
                Input(
                     attrs = Modifier
                         .id(Id.TITLE_INPUT)
                        .fillMaxWidth()
                        .fontSize(16.px)
                        .height(54.px)
                        .borderRadius(r = 4.px)
                        .margin(topBottom = 12.px)
                        .padding(leftRight = 20.px)
                        .backgroundColor(rgba(211,211,211, 0.2))
                         .noBorder()
                        .toAttrs {
                            attr("placeholder", "Title")
                            attr("value", uiState.title)
                        },
                     type = InputType.Text

                )
                Input(

                    attrs = Modifier
                         .id(Id.SUBTITLE_INPUT)
                        .fillMaxWidth()
                        .height(54.px)
                        .fontSize(16.px)
                        .margin(bottom = 12.px)
                        .borderRadius(r = 4.px)
                        .padding(leftRight = 20.px)
                        .backgroundColor(rgba(211,211,211, 0.2))
                         .noBorder()
                         .toAttrs {
                             attr("placeholder", "Subtitle")
                             attr("value", uiState.subtitle ?: "")
                         },
                     type = InputType.Text,



                )
//                CategoryDropDown(
//                    selectedCategory = uiState.selectedCategory,
//                    onCategorySelect = {
//                        uiState = uiState.copy(selectedCategory = it)
//                    }
//                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(topBottom = 12.px),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        modifier = Modifier.margin(right = 8.px)
                        ,
                        checked = !uiState.thumbnailInputDisabled,
                        onCheckedChange = { uiState = uiState.copy(thumbnailInputDisabled = !it) },
                        size = SwitchSize.MD
                    )
                    SpanText(
                        modifier = Modifier
                            .color(rgba(0,0,0, 0.5)),
                        text = "Paste an image URL instead",
                    )
                }
                ThumbnailUploader(
                    thumbnail = uiState.thumbnail,
                    thumbnailInputDisabled = uiState.thumbnailInputDisabled,
                    onThumbnailSelect = { dataUrl, fileName ->
                        (document.getElementById(Id.THUMBNAIL_INPUT)
                                as HTMLInputElement).value = fileName
                        uiState = uiState.copy(thumbnail = dataUrl)
                    }
                )
                EditorControls(
                    breakpoint,
                    editorVisibility = uiState.editorVisibility,
                    onEditorVisibilityChange = {
                        uiState = uiState.copy(editorVisibility = !uiState.editorVisibility)
                    },
                    onLinkClick = {
                        savedEditorSelection = saveEditorSelection()
                        uiState = uiState.copy(linkPopup = true)
                    },
                    onImageClick = {
                        savedEditorSelection = saveEditorSelection()
                        uiState = uiState.copy(imagePopup = true)
                    }
                )
                Editor(
                    editorVisibility = uiState.editorVisibility,
                    thumbnail = uiState.thumbnail,
                )

                createButton(
                    text = uiState.buttonText,
                    onClick = {

                        if (!uiState.thumbnailInputDisabled) {
                            (document.getElementById(Id.THUMBNAIL_INPUT) as HTMLInputElement).value
                        }
                        println("EDITOR tag = " + document.getElementById(Id.EDITOR)?.tagName)

                        val thumbnail = if (!uiState.thumbnailInputDisabled) {
                            (document.getElementById(Id.THUMBNAIL_INPUT) as HTMLInputElement).value
                        } else {
                            uiState.thumbnail
                        }

                        uiState = uiState.copy(
                            title = (document.getElementById(Id.TITLE_INPUT) as HTMLInputElement).value,
                            subtitle = (document.getElementById(Id.SUBTITLE_INPUT) as HTMLInputElement).value,
                            content = (document.getElementById(Id.EDITOR) as HTMLTextAreaElement).value,
                            thumbnail = thumbnail
                        )



                        if (
                            uiState.title.isNotEmpty() &&
                            uiState.thumbnail.isNotEmpty() &&
                            uiState.content.isNotEmpty()
                        ) {
                            scope.launch {
                                if (hasPostIdParam) {
                                    val result = updatePost(
                                        post = Post(
                                            postId = uiState.id,
                                            title = uiState.title,
                                            subtitle = uiState.subtitle,
                                            thumbnail = uiState.thumbnail,
                                            content = uiState.content,
                                            category = uiState.selectedCategory,
                                            popular = uiState.popularSwitch,
                                            main = uiState.mainSwitch,
                                            sponsored = uiState.sponsoredSwitch
                                        )
                                    )
                                    if (result) {
                                        context.router.navigateTo(Screen.AdminSuccess.updatePost(result))
                                    }
                                } else {
                                    val result = addPost(
                                        post = Post(
                                            author = localStorage.getItem("username").toString(),
                                            date = Date.now().toLong(),
                                            title = uiState.title,
                                            subtitle = uiState.subtitle,
                                            thumbnail = uiState.thumbnail,
                                            content = uiState.content,
                                            category = uiState.selectedCategory,
                                            popular = uiState.popularSwitch,
                                            main = uiState.mainSwitch,
                                            sponsored = uiState.sponsoredSwitch
                                        )
                                    )
                                    if (result) {
                                        context.router.navigateTo(Screen.AdminSuccess.route)
                                    }
                                }
                            }

                        } else {
                            uiState = uiState.copy(messagePopup = true)
                            scope.launch {
                                delay(2000)
                                uiState = uiState.copy(messagePopup = false)
                            }
                        }
                    }
                )

            }
        }
        if (uiState.messagePopup) {

            MessagePopup(
                message = "Pleas fill out the fields",
                onDialogDismiss = {
                    uiState = uiState.copy(messagePopup = false)
                }
            )
        }
        if (uiState.linkPopup) {

            LinkPopup(
                editorControl = EditorControl.Link,
                onLinkClickAdd = { href, title->
                    applyStyle(
                        controlStyle = Link(
                            selectedText = getSelectedText(savedEditorSelection),
                            href = href,
                            title = title
                        ),
                        selection = savedEditorSelection,
                    )
                },
                onDialogDismiss = {
                    uiState = uiState.copy(linkPopup = false)
                }
            )
        }
        if (uiState.imagePopup) {
            LinkPopup(
                editorControl = EditorControl.Image,
                onLinkClickAdd = { imageUrl, description ->
                    applyStyle(
                        controlStyle = ControlStyle.Image(
                            selectedText = getSelectedText(savedEditorSelection),
                            imageUrl = imageUrl,
                            desc = description
                        ),
                        selection = savedEditorSelection,
                    )
                },
                onDialogDismiss = {
                    uiState = uiState.copy(imagePopup = false)
                }
            )
        }
    }

}

//@Composable
//fun CategoryDropDown(
//    selectedCategory: Category,
//    onCategorySelect: (Category) -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .classNames("dropdown")
//            .fillMaxWidth()
//            .height(54.px)
//            .backgroundColor(rgba(211,211,211, 0.2))
//            .cursor(Cursor.Pointer)
//            .attrsModifier {
//                attr("data-bs-toggle", "dropdown")
//            }
//
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(leftRight = 20.px)
//            ,
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            SpanText(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fontSize(16.px),
//                text = selectedCategory.name
//            )
//            Box(modifier = Modifier.classNames("dropdown-toggle"))
//        }
//        Ul(
//            attrs = Modifier
//                .fillMaxWidth()
//                .classNames("dropdown-menu")
//                .toAttrs()
//        ) {
//            Category.entries.forEach {category ->
//                var hovered by remember(category) { mutableStateOf(false) }
//                Li(
//                    attrs = Modifier
//                        .padding(topBottom = 8.px, leftRight = 8.px)
//                        .toAttrs()
//                ) {
//                    A (
//                        attrs = Modifier
//                            .classNames("dropdown-item")
//                            .fontSize(16.px)
//                            .color(Colors.Black)
//                            .onMouseEnter {
//                                _:SyntheticMouseEvent -> hovered = true
//                            }
//                            .onMouseLeave {
//                                    _:SyntheticMouseEvent -> hovered = false
//                            }
//                            .backgroundColor(if (hovered) Colors.Blue else Colors.Transparent)
//                            .onClick {
//                                onCategorySelect(category)
//                            }
//                            .toAttrs()
//                    ) {
//                        Text(category.name)
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun ThumbnailUploader(
    thumbnail: String,
    thumbnailInputDisabled: Boolean,
    onThumbnailSelect: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .margin(bottom = 20.px)
            .height(84.px)
    ) {
        Input(
            type = InputType.Text,
            attrs = Modifier
                .id(Id.THUMBNAIL_INPUT)
                .margin(right = 12.px)
                .fillMaxSize()
                .padding(leftRight = 20.px)
                .backgroundColor(rgba(211,211,211, 0.2))
                .noBorder()
                .thenIf(
                    condition = thumbnailInputDisabled,
                    other = Modifier.disabled()
                )
                .toAttrs {
                    attr("placeholder", "Thumbnail")
                    attr("value", thumbnail)
                }

        )
        Button(
            attrs = Modifier
                .fillMaxHeight()
                .padding(leftRight = 24.px)
                .backgroundColor(if (!thumbnailInputDisabled) rgba(211,211,211, 0.2) else Colors.Blue)
                .color(if (!thumbnailInputDisabled) Colors.DarkGray else Colors.White)
                .onClick {
                    loadDataUrlFromDisk { dataUrl, fileName ->
                        onThumbnailSelect(dataUrl, fileName)
                    }
                }
                .noBorder()
                .thenIf(
                    condition = !thumbnailInputDisabled,
                    other = Modifier.disabled()
                )
                .toAttrs()
        ) {
            SpanText("Upload")
        }
    }
}

@Composable
fun EditorControls(
    breakpoint: Breakpoint,
    editorVisibility: Boolean,
    onEditorVisibilityChange: () -> Unit,
    onLinkClick: () -> Unit,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        SimpleGrid(
            numColumns = numColumns(1, 2),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .backgroundColor(rgba(211,211,211, 0.2))
                    .borderRadius(r = 4.px)
                    .height(54.px)
            ) {
                EditorControl.entries.forEach {
                    EditorControlView(it, onClick = {
                        applyControlStyle(
                            editorControl = it,
                            onLinkClick = onLinkClick,
                            onImageClick = onImageClick
                        )
                    })
                }
            }
            Box(
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    attrs = Modifier
                        .height(54.px)
                        .padding(leftRight = (24.px))
                        .borderRadius(r = 4.px)
                        .margin(topBottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px)
                        .backgroundColor(
                            if (editorVisibility) rgba(211, 211, 211, 0.2)
                            else Colors.Blue
                        )
                        .color(if (editorVisibility) Colors.Gray else Colors.White)
                        .onClick { onEditorVisibilityChange() }
                        .noBorder()
                        .thenIf(
                            condition = breakpoint < Breakpoint.SM,
                            other = Modifier.fillMaxWidth()
                        )
                        .toAttrs()
                ) {
                    SpanText(
                        "Preview",
                        modifier = Modifier.fontSize(16.px)
                    )
                }
            }
        }
    }
}

@Composable
fun EditorControlView(
    control: EditorControl,
    onClick: () -> Unit
) {
    Box(
        modifier = EditorKeyStyle.toModifier()
            .onClick { onClick() }
            .fillMaxHeight()
            .padding(leftRight = 12.px)
            .borderRadius(r = 4.px)
            .cursor(Cursor.Pointer),
        contentAlignment = Alignment.Center
    ) {
        Image(
            src = control.icon,
            description = "${control.name} Icon",
            modifier = Modifier.size(if (control.name == "Subtitle") 16.px else 24.px)
        )
    }
}

@Composable
fun Editor(
    editorVisibility: Boolean,
    thumbnail: String = "",
) {
    LaunchedEffect(editorVisibility, thumbnail) {
        syncEditorPreviewVisibility(showEditor = editorVisibility, thumbnail = thumbnail)
        if (!editorVisibility) {
            delay(100)
            try {
                js("hljs.highlightAll()")
            } catch (e: Exception) {
                println("Highlight.js error: ${e.message}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.px)
            .margin(top = 8.px)
            .position(Position.Relative)
    ) {
        TextArea(
            attrs = Modifier
                .id(Id.EDITOR)
                .fillMaxSize()
                .position(Position.Absolute)
                .resize(Resize.None)
                .padding(24.px)
                .backgroundColor(rgba(211, 211, 211, 0.2))
                .borderRadius(r = 4.px)
                .noBorder()
                .toAttrs {
                    attr("placeholder", "Type here...")
                }
        )
        Div(
            attrs = Modifier
                .id(Id.EDITOR_PREV)
                .fillMaxSize()
                .position(Position.Absolute)
                .padding(24.px)
                .backgroundColor(rgba(211, 211, 211, 0.2))
                .overflow(Overflow.Auto)
                .scrollBehavior(ScrollBehavior.Smooth)
                .noBorder()
                .toAttrs()
        )
    }
}

@Composable
fun createButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        attrs = Modifier
            .onClick { onClick() }
            .fillMaxWidth()
            .height(54.px)
            .margin(top = 24.px)
            .backgroundColor(Colors.Blue)
            .color(Colors.White)
            .borderRadius(r = 4.px)
            .noBorder()
            .fontSize(16.px)
            .toAttrs()
    ) {
        SpanText(text)
    }
}