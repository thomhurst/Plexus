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

class PlexusRequest private constructor(val httpClient: HttpClient) : CoroutineScope {

    internal constructor(httpClient: HttpClient, httpMethod: HttpMethod) : this(httpClient) {
        this.httpMethod = httpMethod
    }

    internal constructor(httpMethod: HttpMethod) : this(PlexusClient.instance.httpClient, httpMethod)

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    var queryParams: Parameters = arrayListOf()
    var httpMethod = HttpMethod.GET
        private set
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

    suspend fun awaitResponseBytes(coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) = withContext(coroutineDispatcher) {
        var httpResponse: HttpResponse<ByteArray>? = null

        val time = measureTimeMillis {
            httpResponse = suspendingHttpResponseBytes()
        }

        PlexusResponse(httpResponse!!, time, this@PlexusRequest)
    }

    fun responseBytes(): PlexusResponse<ByteArray> {
        return runBlocking { awaitResponseBytes() }
    }

    private suspend fun suspendingHttpResponseBytes() = suspendCoroutine<HttpResponse<ByteArray>> { coroutineContinuation ->
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray()).thenAcceptAsync { httpResponse ->
            coroutineContinuation.resume(httpResponse )
        }
    }

    suspend fun awaitResponseString(coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) = withContext(coroutineDispatcher) {
        var httpResponse: HttpResponse<String>? = null

        val time = measureTimeMillis {
            httpResponse = suspendingHttpResponseString()
        }

        PlexusResponse(httpResponse!!, time, this@PlexusRequest)
    }

    fun responseString(): PlexusResponse<String> {
        return runBlocking { awaitResponseString() }
    }

    private suspend fun suspendingHttpResponseString() = suspendCoroutine<HttpResponse<String>> { coroutineContinuation ->
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenAcceptAsync { httpResponse ->
            coroutineContinuation.resume(httpResponse )
        }
    }
}
