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

import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source

import munit.FunSuite
import munit.TestOptions

class MakeMonotonicSuite extends FunSuite with Fixtures {
  def checkMakeMonotonic[T: ToEntry](
      name: TestOptions,
      expected: List[T],
      entries: List[T]
  )(implicit loc: munit.Location): Unit =
    withActorSystem.test(name) { implicit sys =>
      import sys.dispatcher
      History
        .makeStrictlyMonotonic(Source(entries.map(_.toEntry)))
        .runWith(Sink.seq)
        .map { seq =>
          assertEquals(
            seq,
            expected.map(_.toEntry)
          )
        }
    }

  checkMakeMonotonic(
    "when two in a row have the same timestamp",
    List("1_a", "2_b", "3_c"),
    List("1_a", "1_b", "2_c")
  )

  checkMakeMonotonic(
    "when all have the same timestamp",
    List("1_a", "2_b", "3_c", "4_d", "5_e"),
    List("1_a", "1_b", "1_c", "1_d", "1_e")
  )

  checkMakeMonotonic(
    "when increasing after a couple equal ones",
    List("1_a", "2_b", "3_c", "4_d", "5_e"),
    List("1_a", "1_b", "2_c", "3_d", "4_e")
  )
}
