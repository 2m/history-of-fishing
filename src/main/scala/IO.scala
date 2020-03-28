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
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorSystem
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source

import cats.effect.IO

object IoAdapter {
  def fromSourceHead[T](source: Source[T, _])(implicit sys: ActorSystem) =
    IO.async[T] { complete =>
      import sys.dispatcher
      source.runWith(Sink.head).onComplete {
        case Success(value)  => complete(Right(value))
        case Failure(reason) => complete(Left(reason))
      }
    }

  def fromRunnableGraph(graph: RunnableGraph[Future[_]])(implicit sys: ActorSystem) =
    IO.async[Unit] { complete =>
      import sys.dispatcher
      graph.run().onComplete {
        case Success(_)      => complete(Right(()))
        case Failure(reason) => complete(Left(reason))
      }
    }
}
