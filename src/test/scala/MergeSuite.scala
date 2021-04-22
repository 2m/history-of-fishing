/*
 * Copyright 2020 github.com/2m/yabai-scala/history-of-fishing
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

class MergeSuite extends FunSuite with Fixtures {
  def checkMerge[T: ToEntry](
      name: TestOptions,
      expected: List[T],
      entries: List[T]*
  )(implicit loc: munit.Location): Unit =
    withActorSystem.test(name) { implicit sys =>
      import sys.dispatcher
      History
        .merge(entries.toList.map(_.map(_.toEntry)).map(Source.apply))
        .runWith(Sink.seq)
        .map(seq =>
          assertEquals(
            seq,
            expected.map(_.toEntry)
          )
        )
    }

  checkMerge("sorts entries", List(1, 2, 3), List(1, 3), List(2))

  {
    val expected = (1 to 100).toList
    checkMerge("handles large number of streams", expected, expected.map(List(_)): _*)
  }

  checkMerge("deduplicates entries by timestamp", List(1, 2), List(1, 2), List(1))

  checkMerge(
    "deduplicates entries by command",
    List("1_a", "1_b", "1_c"),
    List("1_c", "1_b", "1_a"),
    List("1_c", "1_b", "1_a")
  )
}
