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

import cats.data.NonEmptyList
import com.monovore.decline.Command
import com.monovore.decline.Opts

object Commands {
  sealed trait Command

  final case class Monotonic(historyFile: String) extends Command
  final case class Merge(files: NonEmptyList[String]) extends Command

  val commands = {
    val monotonic =
      Command(
        name = "monotonic",
        header = "verifies if the timestamps in the given history file increase monotonically"
      )(Opts.argument[String]("history-file").map(Monotonic))

    val merge =
      Command(
        name = "merge",
        header = "merges given history files to one file with ordered entries"
      )(Opts.arguments[String]("history-files").map(Merge))

    Opts
      .subcommand(monotonic)
      .orElse(Opts.subcommand(merge))
  }
}
