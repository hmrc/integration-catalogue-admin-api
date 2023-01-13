/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcatalogueadmin.services

import java.util.UUID
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.mockito.scalatest.MockitoSugar
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcatalogueadmin.connectors.IntegrationCatalogueConnector

class PublishServiceSpec extends AnyWordSpec with should.Matchers with GuiceOneAppPerSuite with MockitoSugar {

  val mockIntegrationCatalogueConnector: IntegrationCatalogueConnector = mock[IntegrationCatalogueConnector]
  private implicit val hc: HeaderCarrier                               = HeaderCarrier()

  trait SetUp {
    val objInTest                            = new PublishService(mockIntegrationCatalogueConnector)
    val apiPublishRequest: ApiPublishRequest = ApiPublishRequest(Some("publisherRef"), PlatformType.CORE_IF, SpecificationType.OAS_V3, "contents")

    val expectedApiPublishResult: PublishResult =
      PublishResult(isSuccess = true, Some(PublishDetails(isUpdate = true, IntegrationId(UUID.randomUUID()), "publisherReference", PlatformType.CORE_IF)))

    val expectedFileTransferPublishResult: PublishResult =
      PublishResult(isSuccess = true, Some(PublishDetails(isUpdate = true, IntegrationId(UUID.randomUUID()), "BVD-DPS-PCPMonthly-pull", PlatformType.CORE_IF)))

    val dateValue: DateTime    = DateTime.parse("04/11/2020 20:27:05", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"))
    val reviewedDate: DateTime = DateTime.parse("24/11/2020 20:27:05", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"))

    val fileTransferPublishRequest: FileTransferPublishRequest = FileTransferPublishRequest(
      fileTransferSpecificationVersion = "1.0",
      publisherReference = "XXX-YYY-ZZZMonthly-pull",
      title = "XXX-YYY-ZZZMonthly-pull",
      description = "A file transfer",
      platformType = PlatformType.CORE_IF,
      lastUpdated = dateValue,
      reviewedDate = reviewedDate,
      contact = ContactInformation(Some("Core IF Team"), Some("example@gmail.com")),
      sourceSystem = List("XXX"),
      targetSystem = List("YYY"),
      transports = List.empty,
      fileTransferPattern = "Pattern1"
    )
  }

  "publishApi" should {
    "return value from connector" in new SetUp {
      when(mockIntegrationCatalogueConnector.publishApis(eqTo(apiPublishRequest))(*)).thenReturn(Future.successful(Right(expectedApiPublishResult)))

      val result: Either[Throwable, PublishResult] =
        Await.result(objInTest.publishApi(Some("publisherRef"), PlatformType.CORE_IF, SpecificationType.OAS_V3, "contents"), 500 millis)

      result match {
        case Left(_)                             => fail()
        case Right(publishResult: PublishResult) => publishResult shouldBe expectedApiPublishResult
      }

      verify(mockIntegrationCatalogueConnector).publishApis(eqTo(apiPublishRequest))(eqTo(hc))
    }
  }

  "publishFileTransfer" should {
    "return value from connector" in new SetUp {
      when(mockIntegrationCatalogueConnector.publishFileTransfer(eqTo(fileTransferPublishRequest))(*))
        .thenReturn(Future.successful(Right(expectedFileTransferPublishResult)))

      val result: Either[Throwable, PublishResult] =
        Await.result(objInTest.publishFileTransfer(fileTransferPublishRequest), 500 millis)

      result match {
        case Left(_)                             => fail()
        case Right(publishResult: PublishResult) => publishResult shouldBe expectedFileTransferPublishResult
      }

      verify(mockIntegrationCatalogueConnector).publishFileTransfer(eqTo(fileTransferPublishRequest))(eqTo(hc))
    }
  }

}
