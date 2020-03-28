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
import java.nio.file.Paths

import cats.data.NonEmptyList
import com.monovore.decline.Command
import com.monovore.decline.Opts

object Commands {
  final val HomePath = Paths.get(System.getProperty("user.home"), ".local", "share", "fish")

  sealed trait Command

  final case class Monotonic(historyFile: Path) extends Command
  final case class Merge(files: NonEmptyList[Path]) extends Command
  final case class ResolveConflicts(path: Path) extends Command
  final case object Version extends Command

  val commands = {
    val monotonic =
      Command(
        name = "monotonic",
        header = "Verifies if the timestamps in the given history file increase monotonically."
      )(Opts.argument[Path]("history-file").map(Monotonic))

    val merge =
      Command(
        name = "merge",
        header = "Merges given history files to one file with ordered entries."
      )(Opts.arguments[Path]("history-files").map(Merge))

    val resolveConflicts =
      Command(
        name = "resolve-conflicts",
        header = s"""|Resolves conflicts by merging files like 'fish_history.sync-conflict-*' into the
                     |'fish_history' file in the given directory.
                     |
                     |If no directory is given, '$HomePath' is used instead.""".stripMargin
      )(
        Opts
          .argument[Path]("directory")
          .orElse(Opts(HomePath))
          .map(ResolveConflicts)
      )

    val version =
      Command(
        name = "version",
        header = "prints current version"
      )(Opts.unit.map(_ => Version))

    Opts
      .subcommand(monotonic)
      .orElse(Opts.subcommand(merge))
      .orElse(Opts.subcommand(resolveConflicts))
      .orElse(Opts.subcommand(version))
  }
}
