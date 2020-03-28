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

class MergeSuite extends munit.FunSuite with Fixtures {
  def whenToEntry(when: Int) = Entry(s"cmd_$when", when, List())

  def checkMergeByWhen(
      name: String,
      obtained: List[List[Int]],
      expected: List[Int]
  )(implicit loc: munit.Location): Unit =
    checkMergeEntries(name, obtained.map(_.map(whenToEntry)), expected.map(whenToEntry))

  def checkMergeByWhenCmd(
      name: String,
      obtained: List[List[String]],
      expected: List[String]
  )(implicit loc: munit.Location): Unit = {
    def whenCmdToEntry(cmd: String) = cmd match {
      case s"${when}_$cmd" => Entry(cmd, when.toInt, List())
    }
    checkMergeEntries(name, obtained.map(_.map(whenCmdToEntry)), expected.map(whenCmdToEntry))
  }

  def checkMergeEntries(
      name: String,
      obtained: List[List[Entry]],
      expected: List[Entry]
  )(implicit loc: munit.Location): Unit =
    withActorSystem.test(name) { implicit sys =>
      import sys.dispatcher
      val result = History
        .merge(obtained.map(Source.apply))
        .runWith(Sink.seq)
        .map(seq =>
          assertEquals(
            seq,
            expected
          )
        )
      Await.result(result, 5.seconds)
    }

  checkMergeByWhen("sorts entries", List(List(1, 3), List(2)), List(1, 2, 3))

  {
    val expected = (1 to 100).toList
    checkMergeByWhen("handles large number of streams", expected.map(List(_)), expected)
  }

  checkMergeByWhen("deduplicates entries by timestamp", List(List(1, 2), List(1)), List(1, 2))

  checkMergeByWhenCmd(
    "deduplicates entries by command",
    List(List("1_c", "1_b", "1_a"), List("1_c", "1_b", "1_a")),
    List("1_a", "1_b", "1_c")
  )
}
