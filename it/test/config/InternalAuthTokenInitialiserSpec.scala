/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.github.tomakehurst.wiremock.client.WireMock._
import com.google.inject.CreationException
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.http.Status.{CREATED, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.http.test.WireMockSupport
import uk.gov.hmrc.integrationcatalogueadmin.config.InternalAuthTokenInitialiser.{actions, resourceLocation, resourceType}

class InternalAuthTokenInitialiserSpec extends AnyFreeSpec with Matchers with WireMockSupport {

  "when configured to run" - {

    "must initialise the internal-auth token if it is not already initialised" in {

      val authToken = "authToken1"
      val appName = "appName1"

      val expectedRequest = Json.obj(
        "token" -> authToken,
        "principal" -> appName,
        "permissions" -> Seq(
          Json.obj(
            "resourceType" -> resourceType,
            "resourceLocation" -> resourceLocation,
            "actions" -> actions
          )
        )
      )

      stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      stubFor(
        post(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(CREATED))
      )

      GuiceApplicationBuilder()
        .configure(
          "microservice.services.internal-auth.port" -> wireMockPort,
          "appName" -> appName,
          "create-internal-auth-token-on-start" -> true,
          "internal-auth.token" -> authToken
        )
        .build()

      verify(1,
        getRequestedFor(urlMatching("/test-only/token"))
          .withHeader(AUTHORIZATION, equalTo(authToken))
      )

      verify(1,
        postRequestedFor(urlMatching("/test-only/token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(expectedRequest))))
      )

    }

    "must return an exception if the internal auth service responds to create with a different status" in {

      val authToken = "authToken2"
      val appName = "appName2"

      val expectedRequest = Json.obj(
        "token" -> authToken,
        "principal" -> appName,
        "permissions" -> Seq(
          Json.obj(
            "resourceType" -> resourceType,
            "resourceLocation" -> resourceLocation,
            "actions" -> actions
          )
        )
      )

      stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      stubFor(
        post(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      a[CreationException] mustBe thrownBy {
        GuiceApplicationBuilder()
          .configure(
            "microservice.services.internal-auth.port" -> wireMockPort,
            "appName" -> appName,
            "create-internal-auth-token-on-start" -> true,
            "internal-auth.token" -> authToken
          )
          .build()
      }

      verify(1,
        getRequestedFor(urlMatching("/test-only/token"))
          .withHeader(AUTHORIZATION, equalTo(authToken))
      )

      verify(1,
        postRequestedFor(urlMatching("/test-only/token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(expectedRequest))))
      )
    }

    "must not initialise the internal-auth token if it is already initialised" in {

      val authToken = "authToken"
      val appName = "appName"

      stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(OK))
      )

      stubFor(
        post(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(CREATED))
      )

      GuiceApplicationBuilder()
        .configure(
          "microservice.services.internal-auth.port" -> wireMockPort,
          "appName" -> appName,
          "create-internal-auth-token-on-start" -> true,
          "internal-auth.token" -> authToken
        )
        .build()

      verify(1,
        getRequestedFor(urlMatching("/test-only/token"))
          .withHeader(AUTHORIZATION, equalTo(authToken))
      )

      verify(0,
        postRequestedFor(urlMatching("/test-only/token"))
      )
    }
  }

  "when not configured to run" - {

    "must not make the relevant calls to internal-auth" in {

      val authToken = "authToken"
      val appName = "appName"

      stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(OK))
      )

      stubFor(
        post(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(CREATED))
      )

      GuiceApplicationBuilder()
        .configure(
          "microservice.services.internal-auth.port" -> wireMockPort,
          "appName" -> appName,
          "create-internal-auth-token-on-start" -> false,
          "internal-auth.token" -> authToken
        )
        .build()

      verify(0,
        getRequestedFor(urlMatching("/test-only/token"))
      )

      verify(0,
        postRequestedFor(urlMatching("/test-only/token"))
      )
    }
  }

}
