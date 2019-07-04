package com.tomlonghurst.plexus

import java.time.OffsetDateTime
import kotlin.concurrent.fixedRateTimer

object PlexusCounters {

    private val requestTimer = fixedRateTimer("Clear Requests", true, 0, 5000) {
        internalRequestsLastFiveSeconds.removeAll { time -> time.toEpochSecond() <= OffsetDateTime.now().toEpochSecond() - 5 }
        println("Requests per sec = ${requestsLastFiveSeconds.size / 5}")
    }

    private val internalRequestsLastFiveSeconds by lazy { mutableListOf<OffsetDateTime>() }
        @Synchronized get

    val requestsLastFiveSeconds: List<OffsetDateTime>
        @Synchronized get() {
            val nowSeconds = OffsetDateTime.now().toEpochSecond()
            internalRequestsLastFiveSeconds.removeAll { it.toEpochSecond() < nowSeconds }
            return internalRequestsLastFiveSeconds
        }

    internal fun incrementRequestCount() {
        internalRequestsLastFiveSeconds.add(OffsetDateTime.now())
    }

    private val responseTimer = fixedRateTimer("Clear Responses", true, 0, 5000) {
        internalResponseLastFiveSeconds.removeAll { time -> time.toEpochSecond() <= OffsetDateTime.now().toEpochSecond() - 5 }
        println("Responses per sec = ${responseLastFiveSeconds.size / 5}")
    }

    private val internalResponseLastFiveSeconds by lazy { mutableListOf<OffsetDateTime>() }
        @Synchronized get

    val responseLastFiveSeconds: List<OffsetDateTime>
        @Synchronized get() {
            val nowSeconds = OffsetDateTime.now().toEpochSecond()
            internalResponseLastFiveSeconds.removeAll { it.toEpochSecond() < nowSeconds }
            return internalResponseLastFiveSeconds
        }

    internal fun incrementResponseCount() {
        internalResponseLastFiveSeconds.add(OffsetDateTime.now())
    }

}