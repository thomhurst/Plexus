package com.tomlonghurst.plexus

import java.net.http.HttpResponse

class PlexusResponse<T> internal constructor(private val httpResponse: HttpResponse<T>, val timeTaken: Long, val request: PlexusRequest) {

    init {
        PlexusCounters.incrementResponseCount()
    }

    val body: T = httpResponse.body()
    val headers = httpResponse.headers().map()
    val statusCode = httpResponse.statusCode()
    val isSuccessful = statusCode in 200..299
    val url = request.url

}