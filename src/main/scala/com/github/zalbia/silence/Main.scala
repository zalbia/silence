package com.github.zalbia.silence

import com.github.plokhotnyuk.jsoniter_scala.core

import zio.{ App, ExitCode, URIO, ZIO }
import zio.blocking.Blocking
import zio.console._

object Main extends App {

  def run(args: List[String]): URIO[Console with Blocking, ExitCode] =
    app(args).exitCode

  private def app(args: List[String]) =
    (for {
      arguments <- Arguments.parse(args)
      segments  = Chapter.readFromXml(arguments).map(Segment.fromChapter).flattenIterables
      _ <- segments.toIterator
            .map(_.map(_.getOrElse(???))) // ignore errors to keep things simple
            .use(writeJson(_))
            .mapError(e => List(e.getMessage))
    } yield ()).foldM(handleErrors, _ => ZIO.unit)

  private def writeJson(segments: Iterator[Segment]) =
    ZIO(core.writeToStream(segments, java.lang.System.out))

  private def handleErrors(errors: Iterable[String]) =
    for {
      _ <- putStrLnErr("Errors:")
      _ <- putStrLnErr(errors.mkString("\n"))
      _ <- putStrLnErr("\nFormat:")
      _ <- putStrLnErr(
            "<path-to-xml> <chapter-silence-duration>" +
              " <partition-threshold-duration> <part-silence-duration>"
          )
      _ <- putStrLnErr("where duration is an ISO 8601 duration string")
    } yield ()
}
