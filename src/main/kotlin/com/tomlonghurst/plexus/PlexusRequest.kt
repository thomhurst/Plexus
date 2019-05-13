package com.tomlonghurst.plexus
import kotlinx.coroutines.*
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

class PlexusRequest internal constructor(val httpClient: HttpClient) : CoroutineScope {

    internal constructor(httpClient: HttpClient, httpMethod: HttpMethod) : this(httpClient) {
        this.httpMethod = httpMethod
    }

    constructor(httpMethod: HttpMethod) : this(PlexusClient.instance.httpClient) {
        this.httpMethod = httpMethod
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    var queryParams: Parameters = arrayListOf()
    private var httpMethod = HttpMethod.GET
    var headers = arrayListOf<Pair<String, String>>()
    var url = ""
    var body = ""
    var timeout = httpClient.connectTimeout().orElse(Duration.ofSeconds(30))
    var httpVersion = httpClient.version()

    private val bodyPublisher: HttpRequest.BodyPublisher
        get() = if (body.isBlank()) {
            HttpRequest.BodyPublishers.noBody()
        } else {
            HttpRequest.BodyPublishers.ofString(body)
        }

    private val httpRequest by lazy { build() }

    private fun build(): HttpRequest? {
        val httpRequestBuilder =  HttpRequest.newBuilder()

        val queryString =
            if(queryParams.isNotEmpty()) {
                queryParams
                    .map { p ->
                        URLEncoder.encode(p.first, Charsets.UTF_8) + "=" + URLEncoder.encode(
                            p.second,
                            Charsets.UTF_8
                        )
                    }
                    .reduce { p1, p2 -> "$p1&$p2" }
            } else {
                ""
            }

        httpRequestBuilder
            .uri(URI.create(url + queryString))
            .method(httpMethod.toString(), bodyPublisher)
            .timeout(timeout)
            .version(httpVersion)

        for (header in headers) {
            httpRequestBuilder.header(header.first, header.second)
        }

        return httpRequestBuilder.build()
    }

    fun header(header: Pair<String, String>) : PlexusRequest {
        headers.add(header)
        return this
    }

    suspend fun awaitResponse(coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) = withContext(coroutineDispatcher) {
        var httpResponse: HttpResponse<String>? = null

        val time = measureTimeMillis {
            httpResponse = suspendingHttpResponse()
        }

        PlexusResponse(httpResponse!!, time)
    }

    fun response(): PlexusResponse {
        return runBlocking { awaitResponse() }
    }

    private suspend fun suspendingHttpResponse() = suspendCoroutine<HttpResponse<String>> { coroutineContinuation ->
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenAcceptAsync { httpResponse ->
            coroutineContinuation.resume(httpResponse )
        }
    }
}
