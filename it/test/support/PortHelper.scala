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

package support

import java.net.ServerSocket
import scala.annotation.tailrec
import scala.collection.immutable
import scala.language.postfixOps

import play.api.Logger

object PortHelper {
  val rnd                       = new scala.util.Random
  val range: immutable.Seq[Int] = 8000 to 39999
  val logger: Logger            = Logger(this.getClass)

  // scalastyle:off magic.number

  @tailrec
  def randomAvailable: Int = {
    range(rnd.nextInt(range length)) match {
      case 8080   => randomAvailable
      case 8090   => randomAvailable
      case p: Int =>
        if (available(p)) {
          logger.debug("Taking port : " + p)
          p
        } else {
          logger.debug(s"Port $p is in use, trying another")
          randomAvailable
        }
    }
  }

  private def available(p: Int): Boolean = {
    var socket: ServerSocket = null
    try {
      socket = new ServerSocket(p)
      socket.setReuseAddress(true)
      true
    } catch {
      case _: Throwable => false
    } finally {
      if (socket != null) socket.close()
    }
  }
  // scalastyle:on magic.number
}
