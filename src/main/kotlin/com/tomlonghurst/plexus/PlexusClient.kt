package com.tomlonghurst.plexus
import java.net.Authenticator
import java.net.CookieHandler
import java.net.ProxySelector
import java.net.http.HttpClient
import java.time.Duration
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLParameters

class PlexusClient {

    companion object {
        var instance = PlexusClient()
    }

    var proxySelector: ProxySelector? = null
    var redirectPolicy: HttpClient.Redirect = HttpClient.Redirect.NORMAL
    var authenticator: Authenticator? = null
    var cookieHandler: CookieHandler? = null
    val defaultHeaders = arrayListOf<Pair<String, String>>()
    var httpVersion = HttpClient.Version.HTTP_2
    var timeout = Duration.ofSeconds(30)
    var sslContext: SSLContext? = null
    var sslParameters: SSLParameters? = null

    fun get(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(build()).apply {
            this.httpMethod = HttpMethod.GET
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.httpVersion = this@PlexusClient.httpVersion
            this.headers.addAll(defaultHeaders)
        }
    }

    fun post(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(build()).apply {
            this.httpMethod = HttpMethod.POST
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.httpVersion = this@PlexusClient.httpVersion
            this.headers.addAll(defaultHeaders)
        }
    }

    fun put(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(build()).apply {
            this.httpMethod = HttpMethod.PUT
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.httpVersion = this@PlexusClient.httpVersion
            this.headers.addAll(defaultHeaders)
        }
    }

    fun patch(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(build()).apply {
            this.httpMethod = HttpMethod.PATCH
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.httpVersion = this@PlexusClient.httpVersion
            this.headers.addAll(defaultHeaders)
        }
    }

    fun delete(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(build()).apply {
            this.httpMethod = HttpMethod.DELETE
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.httpVersion = this@PlexusClient.httpVersion
            this.headers.addAll(defaultHeaders)
        }
    }

    fun head(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(build()).apply {
            this.httpMethod = HttpMethod.HEAD
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.httpVersion = this@PlexusClient.httpVersion
            this.headers.addAll(defaultHeaders)
        }
    }

    private fun build(): HttpClient {
        val httpClientBuilder = HttpClient.newBuilder()
        httpClientBuilder.apply {
            timeout?.let { timeout -> connectTimeout(timeout) }
            cookieHandler?.let { cookieHandler -> cookieHandler(cookieHandler) }
            authenticator?.let { authenticator -> authenticator(authenticator) }
            proxySelector?.let { proxySelector -> proxy(proxySelector) }
            sslContext?.let { sslContext -> sslContext(sslContext) }
            sslParameters?.let { sslParameters -> sslParameters(sslParameters) }
        }

        return httpClientBuilder.build()
    }
}