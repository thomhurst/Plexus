package com.tomlonghurst.plexus
import okhttp3.OkHttpClient

class PlexusClient {

    companion object {
        var httpClient = OkHttpClient()
    }

    val defaultHeaders = arrayListOf<Pair<String, String>>()

    fun get(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(httpClient, HttpMethod.GET).apply {
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.headers.addAll(defaultHeaders)
        }
    }

    fun post(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(httpClient, HttpMethod.POST).apply {
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.headers.addAll(defaultHeaders)
        }
    }

    fun put(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(httpClient, HttpMethod.PUT).apply {
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.headers.addAll(defaultHeaders)
        }
    }

    fun patch(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(httpClient, HttpMethod.PATCH).apply {
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.headers.addAll(defaultHeaders)
        }
    }

    fun delete(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(httpClient, HttpMethod.DELETE).apply {
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.headers.addAll(defaultHeaders)
        }
    }

    fun head(path: String, parameters: Parameters? = null): PlexusRequest {
        return PlexusRequest(httpClient, HttpMethod.HEAD).apply {
            this.url = path
            parameters?.let { queryParams -> this.queryParams = queryParams }
            this.headers.addAll(defaultHeaders)
        }
    }
}