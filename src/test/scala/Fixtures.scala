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

import scala.concurrent.Future

import akka.actor.ActorSystem

import lt.dvim.hof.History.Entry

trait Fixtures { self: munit.FunSuite =>
  val withActorSystem = FunFixture.async[ActorSystem](
    setup = { _ => Future.successful(ActorSystem()) },
    teardown = { sys => sys.terminate().map(_ => ())(munitExecutionContext) }
  )

  trait ToEntry[T] {
    def toEntry(t: T): Entry
  }

  implicit object IntToEntry extends ToEntry[Int] {
    def toEntry(t: Int): Entry = Entry("cmd", t, List())
  }

  implicit object StringToEntry extends ToEntry[String] {
    def toEntry(t: String): Entry =
      t match {
        case s"${when}_$cmd" => Entry(cmd, when.toInt, List())
      }
  }

  implicit class EntryOps[T: ToEntry](t: T) {
    def toEntry = implicitly[ToEntry[T]].toEntry(t)
  }
}
