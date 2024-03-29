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

package uk.gov.hmrc.integrationcatalogueadmin.utils

import scala.util.Try

import play.api.Logging
import play.api.libs.json.{JsValue, Json, Reads}

trait JsonUtils extends Logging {

  def validateAndExtractJsonString[T](body: String)(implicit reads: Reads[T]): Try[T] = {
    validateJsonAndExtract[T](body, body => Json.parse(body))
  }

  private def validateJsonAndExtract[T](body: String, f: String => JsValue)(implicit reads: Reads[T]): Try[T] = {
    Try[T] {
      f(body).as[T]
    }
  }
}
