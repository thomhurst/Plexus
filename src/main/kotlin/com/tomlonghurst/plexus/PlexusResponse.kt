package com.tomlonghurst.plexus

import java.net.http.HttpResponse

class PlexusResponse(private val httpResponse: HttpResponse<String>) {

    val body = httpResponse.body()
    val headers = httpResponse.headers().map()
    val statusCode = httpResponse.statusCode()
    val isSuccessful = statusCode in 200..299

}