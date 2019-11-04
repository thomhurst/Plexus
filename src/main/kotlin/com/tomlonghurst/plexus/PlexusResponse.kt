package com.tomlonghurst.plexus

import okhttp3.Response

class PlexusResponse internal constructor(private val httpResponse: Response, val timeTaken: Long, val request: PlexusRequest) {

    val body = httpResponse.body
    val headers = httpResponse.headers
    val statusCode = httpResponse.code
    val isSuccessful = statusCode in 200..299
    val url = request.url

}