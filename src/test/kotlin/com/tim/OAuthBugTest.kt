package com.tim

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

@MicronautTest
class OAuthBugTest(private val service: SomeApiService) {

    @Test
    fun testItWorks() {
        val result = service.send()

        // First request should be attempted and then fail with ReadTimeoutException
        StepVerifier.create(result)
            .verifyError()

        val result2 = service.send()

        /*
        Second request SHOULD be attempted and then fail with ReadTimeoutException
        ACTUALLY fails instantly due to cached error being automatically replayed to the client
         */
        StepVerifier.create(result2)
            .verifyError()
    }

    /*
    Example logoutput:
    15:08:25.735 [Test worker] INFO  i.m.context.env.DefaultEnvironment - Established active environments: [test]
15:08:25.935 [Test worker] DEBUG i.m.s.o.routes.OauthRouteBuilder - No Oauth controllers found. Skipping registration of routes
15:08:26.063 [Test worker] TRACE i.m.h.server.netty.NettyHttpServer - Binding ouathbug server to *:-1
15:08:26.286 [Test worker] TRACE i.m.s.o.e.t.r.DefaultTokenEndpointClient - Sending request to token endpoint [https://mplanchant.eu.authz.cloudentity.io/mplanchant/default/oauth2/token]
15:08:26.286 [Test worker] TRACE i.m.s.o.e.t.r.DefaultTokenEndpointClient - The token endpoint supports [[client_secret_post]] authentication methods
15:08:26.286 [Test worker] TRACE i.m.s.o.e.t.r.DefaultTokenEndpointClient - Using client_secret_post authentication. The client_id and client_secret will be present in the body
15:08:26.299 [Test worker] TRACE i.m.s.o.c.c.p.ClientCredentialsHttpClientFilter - Did not find any OAuth 2.0 client which should decorate the request with an access token received from client credentials request
15:08:26.368 [default-nioEventLoopGroup-1-2] DEBUG i.m.h.client.netty.DefaultHttpClient - Sending HTTP POST to https://mplanchant.eu.authz.cloudentity.io/mplanchant/default/oauth2/token
15:08:26.369 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - Accept: application/json
15:08:26.369 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - content-type: application/x-www-form-urlencoded
15:08:26.369 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - host: mplanchant.eu.authz.cloudentity.io
15:08:26.370 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - connection: close
15:08:26.370 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - content-length: 130
15:08:26.370 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - Request Body
15:08:26.370 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - ----
15:08:26.370 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - grant_type=client_credentials&client_secret=<redacted>&client_id=<redacted>
15:08:26.370 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - ----
15:08:26.402 [default-nioEventLoopGroup-1-2] TRACE i.m.h.client.netty.DefaultHttpClient - HTTP Client exception (ReadTimeoutException) occurred for request : POST https://mplanchant.eu.authz.cloudentity.io/mplanchant/default/oauth2/token
io.micronaut.http.client.exceptions.ReadTimeoutException: Read Timeout
io.micronaut.http.client.exceptions.ReadTimeoutException: Read Timeout
     */

    /*
    NOTE that second exception occurs without any attempted request
     */
}
