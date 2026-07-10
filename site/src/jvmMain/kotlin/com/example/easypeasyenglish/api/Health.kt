package com.example.easypeasyenglish.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.setBodyText

@Api("health")
suspend fun healthCheck(context: ApiContext) {
    context.res.setBodyText("OK")
}
