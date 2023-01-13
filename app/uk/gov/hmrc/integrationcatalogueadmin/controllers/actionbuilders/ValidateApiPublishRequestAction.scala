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

package uk.gov.hmrc.integrationcatalogueadmin.controllers.actionbuilders

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import cats.data.Validated.{Invalid, Valid}

import play.api.libs.json.Json
import play.api.mvc.Results.BadRequest
import play.api.mvc.{ActionRefiner, Request, Result}
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.integrationcatalogue.models.ErrorResponse
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogueadmin.models.{ExtractedHeaders, ValidatedApiPublishRequest}

@Singleton
class ValidateApiPublishRequestAction @Inject() (headerValidator: PublishHeaderValidator)(implicit ec: ExecutionContext)
    extends ActionRefiner[Request, ValidatedApiPublishRequest] with HttpErrorFunctions {
  actionName =>
  override def executionContext: ExecutionContext = ec

  override def refine[A](request: Request[A]): Future[Either[Result, ValidatedApiPublishRequest[A]]] = Future.successful {
    headerValidator.validateHeaders(request) match {
      case Valid(a: ExtractedHeaders) =>
        Right(ValidatedApiPublishRequest(a.publisherReference, a.platformType, a.specificationType, request))
      case Invalid(e)                 => Left(BadRequest(Json.toJson(ErrorResponse(e.toList))))
    }

  }

}
