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

package uk.gov.hmrc.integrationcatalogue.models

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import uk.gov.hmrc.integrationcatalogue.models.common._

import java.time.Instant

sealed trait IntegrationDetail {
  def id: IntegrationId
  def publisherReference: String
  def title: String
  def description: String
  def platform: PlatformType
  def lastUpdated: Instant
  def reviewedDate: Instant
  def maintainer: Maintainer
  def integrationType: IntegrationType
  def score: Option[Double]
}

case class Example(name: String, jsonBody: String)

case class StringAttributes(minLength: Option[Int], maxLength: Option[Int])

case class NumberAttributes(
    minimum: Option[BigDecimal],
    maximum: Option[BigDecimal],
    multipleOf: Option[BigDecimal],
    exclusiveMinimum: Option[Boolean],
    exclusiveMaximum: Option[Boolean]
  )

// types that could be T are: OffsetDateTime, byte[], UUID, Number, Date, Boolean, BigDecimal, String

sealed trait Schema {
  def name: Option[String]
  def not: Option[Schema]
  def `type`: Option[String]     // scalastyle:ignore
  def pattern: Option[String]
  def description: Option[String]
  def ref: Option[String]
  def properties: List[Schema]
  def `enum`: List[String]       // scalastyle:ignore
  def required: List[String]
  def minProperties: Option[Int] // scalastyle:ignore
  def maxProperties: Option[Int] // scalastyle:ignore
}

case class DefaultSchema(
    name: Option[String] = None,
    not: Option[Schema] = None,
    `type`: Option[String] = None,
    pattern: Option[String] = None,
    description: Option[String] = None,
    ref: Option[String] = None,
    properties: List[Schema] = List.empty,
    `enum`: List[String] = List.empty,
    required: List[String] = List.empty,
    stringAttributes: Option[StringAttributes] = None,
    numberAttributes: Option[NumberAttributes] = None,
    minProperties: Option[Int] = None,
    maxProperties: Option[Int] = None,
    format: Option[String] = None,
    default: Option[String] = None,
    example: Option[String] = None
  ) extends Schema

case class ComposedSchema(
    name: Option[String] = None,
    not: Option[Schema] = None,
    `type`: Option[String] = None,
    pattern: Option[String] = None,
    description: Option[String] = None,
    ref: Option[String] = None,
    properties: List[Schema] = List.empty,
    `enum`: List[String] = List.empty,
    required: List[String] = List.empty,
    minProperties: Option[Int] = None,
    maxProperties: Option[Int] = None,
    allOf: List[Schema],
    anyOf: List[Schema],
    oneOf: List[Schema]
  ) extends Schema

case class ArraySchema(
    name: Option[String] = None,
    not: Option[Schema] = None,
    `type`: Option[String] = None,
    pattern: Option[String] = None,
    description: Option[String] = None,
    ref: Option[String] = None,
    properties: List[Schema] = List.empty,
    `enum`: List[String] = List.empty,
    required: List[String] = List.empty,
    minProperties: Option[Int] = None,
    maxProperties: Option[Int] = None,
    minItems: Option[Int] = None,
    maxItems: Option[Int] = None,
    uniqueItems: Option[Boolean] = None,
    items: Option[Schema] = None
  ) extends Schema

case class Header(
    name: String,
    ref: Option[String] = None,
    description: Option[String] = None,
    required: Option[Boolean] = None,
    deprecated: Option[Boolean] = None,
    schema: Option[Schema] = None
  )

case class Parameter(
    name: Option[String],
    ref: Option[String] = None,
    in: Option[String] = None,
    description: Option[String] = None,
    required: Option[Boolean] = None,
    deprecated: Option[Boolean] = None,
    allowEmptyValue: Option[Boolean] = None,
    schema: Option[Schema] = None
  )

case class Request(description: Option[String], schema: Option[Schema], mediaType: Option[String], examples: List[Example] = List.empty)

//TODO response object needs fleshing out with headers, example errors, schema etc
case class Response(
    statusCode: String,
    description: Option[String],
    schema: Option[Schema],
    mediaType: Option[String],
    examples: List[Example] = List.empty,
    headers: List[Header] = List.empty
  )
case class Endpoint(path: String, methods: List[EndpointMethod])

case class EndpointMethod(
    httpMethod: String,
    summary: Option[String],
    description: Option[String]
  )

sealed trait ApiStatus extends EnumEntry

object ApiStatus extends Enum[ApiStatus] with PlayJsonEnum[ApiStatus] {

  val values: IndexedSeq[ApiStatus] = findValues

  case object ALPHA      extends ApiStatus
  case object BETA       extends ApiStatus
  case object LIVE       extends ApiStatus
  case object DEPRECATED extends ApiStatus
}

case class ApiDetail(
    id: IntegrationId,
    publisherReference: String,
    title: String,
    description: String,
    platform: PlatformType,
    hods: List[String] = List.empty,
    lastUpdated: Instant,
    reviewedDate: Instant,
    maintainer: Maintainer,
    score: Option[Double] = None,
    version: String,
    specificationType: SpecificationType,
    endpoints: List[Endpoint],
    shortDescription: Option[String],
    openApiSpecification: String,
    apiStatus: ApiStatus
  ) extends IntegrationDetail {
  override val integrationType: IntegrationType = IntegrationType.API
}

case class FileTransferDetail(
    id: IntegrationId,      // Ignore
    fileTransferSpecificationVersion: String,
    publisherReference: String,
    title: String,
    description: String,
    platform: PlatformType, // Split this to Platform and type
    lastUpdated: Instant,
    reviewedDate: Instant,
    maintainer: Maintainer,
    score: Option[Double] = None,
    sourceSystem: List[String],
    targetSystem: List[String],
    transports: List[String],
    fileTransferPattern: String
  ) extends IntegrationDetail {
  override val integrationType: IntegrationType = IntegrationType.FILE_TRANSFER
}
