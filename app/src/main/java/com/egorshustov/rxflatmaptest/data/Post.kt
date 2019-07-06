package com.egorshustov.rxflatmaptest.data

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
) {
    var comments: List<Comment>? = null
}