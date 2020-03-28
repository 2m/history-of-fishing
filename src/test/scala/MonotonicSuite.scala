/*
 * Copyright 2020 History of Fishing
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

package lt.dvim.hof

import scala.concurrent.Await
import scala.concurrent.duration._

import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source

import lt.dvim.hof.History.Entry

class MonotonicSuite extends munit.FunSuite with Fixtures {
  implicit def intToEntry(when: Int) = Entry("cmd", when, List())

  def checkMonotonic(
      name: String,
      expected: Boolean,
      entries: Entry*
  )(implicit loc: munit.Location): Unit =
    withActorSystem.test(name) { implicit sys =>
      import sys.dispatcher
      val result = History
        .monotonic(Source(entries))
        .runWith(Sink.head)
        .map {
          case (monotonic, _) =>
            assertEquals(
              monotonic,
              expected
            )
        }
      Await.result(result, 5.seconds)
    }

  checkMonotonic("allow strictly monotonic", true, 1, 2, 3)
  checkMonotonic("fail on monotonic", false, 1, 1, 2)
  checkMonotonic("fail on non monotonic", false, 1, 1, 2)
}
