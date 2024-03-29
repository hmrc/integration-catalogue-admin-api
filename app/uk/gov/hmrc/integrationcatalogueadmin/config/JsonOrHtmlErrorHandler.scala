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

package uk.gov.hmrc.integrationcatalogueadmin.config

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

import play.api.Environment
import play.api.http.JsonHttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Result, Results}
import play.mvc.Http.Status._

import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogue.models.{ErrorResponse, ErrorResponseMessage}

@Singleton
class CustomJsonErrorHandler @Inject() (environment: Environment)()
    extends JsonHttpErrorHandler(environment, None) {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val errorMessage = statusCode match {
      case NOT_FOUND => s"Path or Http method may be wrong. $message"
      case _         => message
    }
    Future.successful(Results.Status(statusCode)(Json.toJson(ErrorResponse(List(ErrorResponseMessage(errorMessage))))))
  }

}
