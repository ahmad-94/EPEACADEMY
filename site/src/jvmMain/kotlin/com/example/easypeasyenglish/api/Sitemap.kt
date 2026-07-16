package com.example.easypeasyenglish.api

import com.example.easypeasyenglish.data.MongoRepository
import com.example.easypeasyenglish.models.Params.POST_ID_PARAM
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Api("sitemap")
suspend fun getSitemap(context: ApiContext) {
    try {
        val repository = context.data.getValue<MongoRepository>()
        val posts = repository.readAllPosts(skip = 0)
        val baseUrl = "https://epeacademy.onrender.com"
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val sitemap = buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n")

            // Home Page
            append("  <url>\n")
            append("    <loc>$baseUrl/</loc>\n")
            append("    <lastmod>$currentDate</lastmod>\n")
            append("    <changefreq>daily</changefreq>\n")
            append("    <priority>1.0</priority>\n")
            append("  </url>\n")

            // Search Page
            append("  <url>\n")
            append("    <loc>$baseUrl/search/query</loc>\n")
            append("    <lastmod>$currentDate</lastmod>\n")
            append("    <changefreq>weekly</changefreq>\n")
            append("    <priority>0.8</priority>\n")
            append("  </url>\n")

            // Dynamic Posts
            posts.forEach { post ->
                append("  <url>\n")
                append("    <loc>$baseUrl/posts/post?$POST_ID_PARAM=${post.postId}</loc>\n")
                append("    <lastmod>${post.date.toSitemapDate()}</lastmod>\n")
                append("    <changefreq>monthly</changefreq>\n")
                append("    <priority>0.6</priority>\n")
                append("  </url>\n")
            }

            append("</urlset>")
        }

        context.res.setBodyText(sitemap)
        context.res.contentType = "application/xml"
    } catch (e: Exception) {
        context.logger.error("Error generating sitemap: ${e.message}")
        context.res.status = 500
        context.res.setBodyText("Error generating sitemap")
    }
}

// Helper to ensure date is in yyyy-MM-dd format
private fun Long.toSitemapDate(): String {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(this))
    } catch (e: Exception) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
