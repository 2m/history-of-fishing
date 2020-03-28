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

import lt.dvim.hof.History.Entry
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.Sink
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.Await

class MergeSuite extends munit.FunSuite {
  val withActorSystem = new FunFixture[ActorSystem](
    setup = { _ => ActorSystem() },
    teardown = { sys => Await.ready(sys.terminate(), 5.seconds); () }
  )

  def checkMerge(
      name: String,
      obtained: List[List[Int]],
      expected: List[Int]
  )(implicit loc: munit.Location): Unit =
    withActorSystem.test(name) { implicit sys =>
      import sys.dispatcher
      def intToEntry(when: Int) = Entry("cmd1", when, List())
      val result = History
        .merge(obtained.map(_.map(intToEntry)).map(Source.apply))
        .runWith(Sink.seq)
        .map(seq =>
          assertEquals(
            seq,
            expected.map(intToEntry)
          )
        )
      Await.result(result, 5.seconds)
    }

  checkMerge("sorts entries", List(List(1, 3), List(2)), List(1, 2, 3))

  {
    val expected = (1 to 100).toList
    checkMerge("large number of streams", expected.map(List(_)), expected)
  }

  checkMerge("deduplicates entries", List(List(1, 2), List(1)), List(1, 2))
}
