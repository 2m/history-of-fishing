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

import java.nio.file.Path

import scala.collection.immutable.Iterable

import akka.NotUsed
import akka.stream.scaladsl.FileIO
import akka.stream.scaladsl.Framing
import akka.stream.scaladsl.Source
import akka.util.ByteString

import cats.Show
import monocle.macros.GenLens
import monocle.std.option._

object History {
  case class State(cmd: Option[String] = None, when: Option[Integer] = None, paths: List[String] = List.empty)
  case class Entry(cmd: String, when: Integer, paths: List[String])
  object Entry {
    def apply(s: State): Entry = Entry(s.cmd.get, s.when.get, s.paths)

    implicit val entryRepr: Show[Entry] = Show.show { e =>
      val lines = List(
        s"- cmd: ${e.cmd}",
        s"  when: ${e.when}"
      ) ++ e.paths.headOption.map(_ => "  paths:") ++ e.paths.map(p => s"    - $p")
      lines.mkString("", System.lineSeparator(), System.lineSeparator())
    }
  }

  val Padding = Entry("", 0, List())
  val EndOfFile = "end-of-file"

  val Cmd = some[State] composeLens GenLens[State](_.cmd)
  val When = some[State] composeLens GenLens[State](_.when)
  val Paths = (some[State] composeLens GenLens[State](_.paths)).asTraversal

  private def parser =
    () => {
      var state = Option.empty[State]

      def stateToEntry = state.map(Entry.apply).toIndexedSeq.toIterable

      (s: ByteString) =>
        s.utf8String match {
          case s"- cmd: $cmd" =>
            val entry = stateToEntry
            state = Some(State())
            state = Cmd.set(Some(cmd))(state)
            entry
          case s"  when: $when" =>
            state = When.set(Some(when.toInt))(state)
            Iterable.empty
          case s"    - $path" =>
            state = Paths.modify(_ :+ path)(state)
            Iterable.empty
          case EndOfFile => stateToEntry
          case _         => Iterable.empty
        }
    }

  def entries(path: Path): Source[Entry, NotUsed] = {
    val MaxLineLength = 1024 * 8

    FileIO
      .fromPath(path)
      .via(Framing.delimiter(ByteString(System.lineSeparator()), MaxLineLength))
      .concat(Source.single(ByteString(EndOfFile)))
      .statefulMapConcat(parser)
      .mapMaterializedValue(_ => NotUsed)
  }

  def merge(sources: List[Source[Entry, NotUsed]]): Source[Entry, NotUsed] =
    merge(Source(sources))

  def merge(sources: Source[Source[Entry, NotUsed], NotUsed]): Source[Entry, NotUsed] = {
    implicit val order: Ordering[Entry] = Ordering.by(e => (e.when, e.cmd))

    sources
      .fold(Source.single(Padding))((stream, source) => stream.mergeSorted(source))
      .flatMapConcat(identity)
      .sliding(n = 2, step = 1)
      .splitWhen { case prev +: current +: _ =>
        prev.when != current.when
      }
      .mapConcat(_.tail)
      .fold(Seq.empty[Entry])((group, entry) =>
        if (group.exists(_.cmd == entry.cmd)) group else (group :+ entry).sorted
      )
      .concatSubstreams
      .mapConcat(identity)
  }
}
