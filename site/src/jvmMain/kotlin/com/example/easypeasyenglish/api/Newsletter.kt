package com.example.easypeasyenglish.api

import com.example.easypeasyenglish.data.MongoRepository
import com.example.easypeasyenglish.models.Newsletter
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue

@Api("subscribe")
suspend fun subscribeToNewsletter(context: ApiContext) {
    try {
        val newsletter = context.req.getBody<Newsletter>()
        context.res.setBody(
            newsletter?.let {
                context.data.getValue<MongoRepository>().subscribe(newsletter = it)
            }
        )
    } catch (e: Exception) {
        context.res.setBody(e.message)
    }
}