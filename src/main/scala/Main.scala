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

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.ActorSystem
import akka.stream.scaladsl.FileIO
import akka.stream.scaladsl.Keep
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.StreamConverters
import akka.util.ByteString

import cats.Show
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.monovore.decline.Command
import fansi.Color.Green
import fansi.Color.Red
import fansi.Underlined.{On => Underline}

import lt.dvim.hof.History.Entry

object Main extends IOApp {
  final val BackupDatetimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")

  def run(args: List[String]): IO[ExitCode] = {

    val command: Command[Commands.Command] = Command(
      name = "hof",
      header = "History of Fishing: tools for working with fish shell history files",
      helpFlag = true
    )(Commands.commands)

    val program = command.parse(args) match {
      case Right(cmd) =>
        implicit val sys = ActorSystem()
        onCommand(cmd).guaranteeCase { _ =>
          import sys.dispatcher
          IO.fromFuture(IO(sys.terminate().map(_ => ())))
        }
      case Left(e) =>
        IO(println(e.toString())) *> IO(ExitCode.Error)
    }

    program.handleErrorWith {
      case ex: NoSuchFileException =>
        IO {
          println(s"""|${Red("File not found:")}
                      |${Underline(ex.getFile())}""".stripMargin)
          ExitCode.Error
        }
      case ex =>
        IO {
          println(s"Unexpected error: ${ex.getMessage()}")
          ExitCode.Error
        }
    }
  }

  private def onCommand(c: Commands.Command)(implicit sys: ActorSystem): IO[ExitCode] =
    c match {

      case Commands.Merge(files) =>
        val merged = History
          .merge(files.toList.map(History.entries))
          .toMat(Sink.foreach(e => print(implicitly[Show[Entry]].show(e))))(Keep.right)

        IoAdapter.fromRunnableGraph(merged).map(_ => ExitCode.Success)

      case Commands.ResolveConflicts(directory) =>
        val OriginalHistory = directory.resolve("fish_history")
        val HistoryBackup =
          directory.resolve(s"fish_history.bak-${LocalDateTime.now().format(BackupDatetimeFormat)}")

        println(s"Backing up current history as ${Green(HistoryBackup.toString)}")
        Files.copy(OriginalHistory, HistoryBackup)

        val conflictFiles = StreamConverters.fromJavaStream(() =>
          Files.find(directory, 1, (path, _) => path.toString.contains("fish_history.sync-conflict-"))
        )

        val resolver = History
          .merge(
            Source
              .single(HistoryBackup)
              .concat(
                conflictFiles.alsoTo(Sink.foreach(f => println(s"Resolving from ${Green(f.toString)}")))
              )
              .map(History.entries)
          )
          .map(implicitly[Show[Entry]].show(_))
          .map(ByteString.apply)
          .toMat(FileIO.toPath(OriginalHistory))(Keep.right)

        IoAdapter.fromRunnableGraph(resolver) *>
          IO.pure(ExitCode.Success)

      case Commands.Version =>
        IO(println(BuildInfo.version)) *>
          IO.pure(ExitCode.Success)
    }
}
