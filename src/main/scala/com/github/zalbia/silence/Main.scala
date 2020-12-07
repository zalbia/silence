package com.github.zalbia.silence

import com.github.plokhotnyuk.jsoniter_scala.core

import zio.{ App, ExitCode, URIO, ZIO }
import zio.blocking.Blocking
import zio.console._
import zio.stream.ZStream

object Main extends App {

  def run(args: List[String]): URIO[Console with Blocking, ExitCode] =
    app(args).exitCode

  private def app(args: List[String]) =
    (for {
      arguments <- Arguments.parse(args)
      chapters  = Chapters.readFromXml(arguments)
      segments  = toSegments(chapters)
      _         <- writeJson(segments)
    } yield ()).foldM(handleErrors, _ => ZIO.unit)

  private def toSegments(chapters: ZStream[zio.blocking.Blocking, Throwable, Chapter]) =
    chapters.zipWithIndex.map(a => Segment.fromChapter(a).toIterable).flattenIterables

  private def writeJson(segments: ZStream[Blocking, Throwable, Segment]) =
    segments.toIterator
      .map(_.map(_.getOrElse(???))) // ignore errors to keep things simple
      .use(segments => ZIO(core.writeToStream(segments, java.lang.System.out)))
      .mapError(e => List(e.getMessage))

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
