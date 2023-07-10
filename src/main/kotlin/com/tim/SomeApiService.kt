package com.tim

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Singleton
import reactor.core.publisher.Mono

@Singleton
class SomeApiService(
    @param: Client("sample") private val httpClient: HttpClient,
) {
    fun send(): Mono<Unit> {
        val request: HttpRequest<Any> = HttpRequest
            .create<Any>(HttpMethod.GET, "200")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)

        return Mono
            .from(httpClient.exchange(request))
            .doOnError {
                println(it)
            }
            .then(Mono.just(Unit))
    }
}
