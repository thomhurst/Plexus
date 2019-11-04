package com.tomlonghurst.plexus
import com.tomlonghurst.plexus.extensions.await
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.net.URI
import kotlin.coroutines.CoroutineContext

class PlexusRequest private constructor(private val httpClient: OkHttpClient) : CoroutineScope {

    internal constructor(httpClient: OkHttpClient, httpMethod: HttpMethod) : this(httpClient) {
        this.httpMethod = httpMethod
    }

    internal constructor(httpMethod: HttpMethod) : this(PlexusClient.instance.httpClient, httpMethod)

    lateinit var uri: URI
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    var queryParams: Parameters = arrayListOf()
    var httpMethod = HttpMethod.GET
        private set
    var headers = arrayListOf<Pair<String, String>>()
    var url = ""
    var body = ""

    private val requestBody: RequestBody?
        get() = if (body.isBlank()) {
            null
        } else {
            body.toRequestBody("application/json".toMediaTypeOrNull())
        }

    private val httpRequest by lazy { build() }

    private fun build(): Request {
        val httpRequestBuilder = Request.Builder()

        uri = URI.create(url)

        val queryParameters = (uri.query ?: "").replace("?", "")
            .split("&")
            .filter { queryParameter ->
                queryParameter.contains("=")
            }
            .filter { queryParameter ->
                queryParameter.split("=").filter { word -> word.isNotEmpty() }.size == 2
            }
            .map { queryParameter ->
                val pair = queryParameter.split("=")
                pair[0] to pair[1]
            }

        val existingQueryParams = mutableListOf<Pair<String, String>>()
        existingQueryParams.addAll(queryParameters)
        existingQueryParams.addAll(queryParams)

        val queryString =
            if (existingQueryParams.isNotEmpty()) {
                "&${existingQueryParams
                    .map { p ->
//                        URLEncoder.encode(p.first, Charsets.UTF_8) + "=" + URLEncoder.encode(
//                            p.second,
//                            Charsets.UTF_8
//                        )
                        "${p.first}=${p.second}"
                    }
                    .reduce { p1, p2 -> "$p1&$p2" }
                }"
            } else {
                ""
            }

        uri = URI(uri.scheme, uri.authority, uri.path, queryString, uri.fragment ?: "")
        url = uri.toURL().toString()

        httpRequestBuilder
            .url(url)
            .method(httpMethod.toString(), requestBody)

        for (header in headers) {
            httpRequestBuilder.header(header.first, header.second)
        }

        return httpRequestBuilder.build()
    }

    fun header(header: Pair<String, String>) : PlexusRequest {
        headers.add(header)
        return this
    }

    suspend fun awaitResponseString(coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) = withContext(coroutineDispatcher) {
        getAndTimeResponse {
            suspendingHttpResponse()
        }
    }

    fun responseString(): PlexusResponse {
        return runBlocking { awaitResponseString() }
    }

    private suspend fun suspendingHttpResponse() = httpClient.newCall(httpRequest).await()

    private inline fun getAndTimeResponse(action: () -> Response): PlexusResponse {
        val start = System.currentTimeMillis()
        val response = action()
        val time = System.currentTimeMillis() - start

        return PlexusResponse(response, time, this)
    }
}
