package com.example.androidapp.data

import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.models.PostWithoutDetails
import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.PrimaryKey

class LocalPost : RealmObject {
    @PrimaryKey
    var postId: String = ""
    var author: String = ""
    var date: Long = 0L
    var title: String = ""
    var subtitle: String? = null
    var thumbnail: String = ""
    var content: String = ""
    var category: String = ""
    var popular: Boolean = false
    var main: Boolean = false
    var sponsored: Boolean = false
}

fun Post.toLocal(): LocalPost {
    val post = LocalPost()
    post.postId = this.postId
    post.author = this.author
    post.date = this.date
    post.title = this.title
    post.subtitle = this.subtitle
    post.thumbnail = this.thumbnail
    post.content = this.content
    post.category = this.category.name
    post.popular = this.popular
    post.main = this.main
    post.sponsored = this.sponsored
    return post
}

fun PostWithoutDetails.toLocal(): LocalPost {
    val post = LocalPost()
    post.postId = this.postId
    post.author = this.author
    post.date = this.date
    post.title = this.title
    post.subtitle = this.subtitle
    post.thumbnail = this.thumbnail
    post.content = this.content ?: ""
    post.category = this.category.name
    post.popular = this.popular
    post.main = this.main
    post.sponsored = this.sponsored
    return post
}

fun LocalPost.toPost(): Post {
    return Post(
        postId = this.postId,
        author = this.author,
        date = this.date,
        title = this.title,
        subtitle = this.subtitle,
        thumbnail = this.thumbnail,
        content = this.content,
        category = try { Category.valueOf(this.category) } catch (e: Exception) { Category.Programming },
        popular = this.popular,
        main = this.main,
        sponsored = this.sponsored
    )
}

fun LocalPost.toPostWithoutDetails(): PostWithoutDetails {
    return PostWithoutDetails(
        postId = this.postId,
        author = this.author,
        date = this.date,
        title = this.title,
        subtitle = this.subtitle,
        thumbnail = this.thumbnail,
        content = this.content,
        category = try { Category.valueOf(this.category) } catch (e: Exception) { Category.Programming },
        popular = this.popular,
        main = this.main,
        sponsored = this.sponsored
    )
}
