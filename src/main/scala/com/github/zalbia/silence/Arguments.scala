package com.github.zalbia.silence

import java.time.Duration

import zio.ZIO
import zio.blocking.Blocking
import zio.nio.core.file.Path
import zio.nio.file.Files
import zio.prelude.Validation

final case class Arguments private[silence] (
  pathToXml: Path,
  durations: Durations
)

final case class Durations private[silence] (
  chapterSilence: Duration,
  partitionThreshold: Duration,
  partSilence: Duration
) {
  def isChapterSilence(duration: Duration): Boolean =
    (duration compareTo chapterSilence) >= 0

  def isPartSilence(duration: Duration): Boolean =
    !isChapterSilence(duration) && ((duration compareTo partSilence) >= 0)

  def isRelevant(duration: Duration): Boolean =
    (duration compareTo partSilence) >= 0
}

object Arguments {
  def parse(args: List[String]): ZIO[Blocking, Iterable[String], Arguments] = {
    implicit val argsLifted = args.lift
    val checkArgs = ZIO.tupled(
      check(pathToXml),
      check(chapterSilence),
      check(partitionThreshold),
      check(partSilenceExists)
    )
    for {
      partialArgs <- checkArgs.flatMap {
                      case (a, b, c, d) =>
                        Validation.tupledPar(a, b, c, d).toZIO.mapError(_.toIterable)
                    }
      (pathToXml, chapterSilence, partitionThreshold, partSilenceStr) = partialArgs
      // parsing the part silence duration can only happen after parsing chapter silence
      partSilence <- partSilence(chapterSilence, partSilenceStr).mapError(List(_))
    } yield Arguments(pathToXml, Durations(chapterSilence, partitionThreshold, partSilence))
  }

  private def pathToXml(implicit args: Int => Option[String]) =
    args(0) match {
      case None =>
        ZIO.fail("Missing path to silence XML file")
      case Some(pathStr) =>
        val path = Path(pathStr)
        Files
          .exists(path)
          .flatMap(fileExists =>
            if (fileExists) ZIO.succeed(path)
            else ZIO.fail(s"""Path \"$pathStr\" doesn't exist""")
          )
    }

  private def chapterSilence(implicit args: Int => Option[String]) =
    args(1) match {
      case None =>
        ZIO.fail("Missing duration for silence between chapters")
      case Some(durationStr) =>
        ZIO
          .effect(Duration.parse(durationStr))
          .mapError(_ => s"""Text \"$durationStr\" cannot be parsed to a Duration""")
    }

  private def partitionThreshold(implicit args: Int => Option[String]) =
    args(2) match {
      case None =>
        ZIO.fail("Missing segment threshold duration")
      case Some(durationStr) =>
        ZIO
          .effect(Duration.parse(durationStr))
          .mapError(_ => s"""Text \"$durationStr\" cannot be parsed to a Duration""")
    }

  private def partSilenceExists(implicit args: Int => Option[String]) =
    ZIO.fromOption(args(3)).mapError(_ => "Missing duration for silence between parts")

  private def partSilence(chapterSilence: Duration, partSilenceStr: String) =
    ZIO
      .effect(Duration.parse(partSilenceStr))
      .mapError(_ => s"""Text \"$partSilenceStr\" cannot be parsed to a Duration""")
      .flatMap { partSilence =>
        if (partSilence.compareTo(chapterSilence) < 0) ZIO.succeed(partSilence)
        else
          ZIO.fail(
            s"Part silence duration ${partSilence} should be less " +
              s"than chapter silence duration ${chapterSilence}"
          )
      }

  private def check[R, A](arg: ZIO[R, String, A]) =
    arg.either.map(Validation.fromEither)
}
