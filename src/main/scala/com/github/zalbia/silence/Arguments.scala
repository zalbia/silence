package com.github.zalbia.silence

import java.time.Duration

import zio.ZIO
import zio.blocking.Blocking
import zio.nio.core.file.Path
import zio.nio.file.Files
import zio.prelude.Validation

final case class Arguments private (
  pathToXml: Path,
  chapterSilence: Duration,
  partitionThreshold: Duration,
  partSilence: Duration
)

object Arguments {
  def parse(args: List[String]): ZIO[Blocking, Iterable[String], Arguments] = {
    implicit val argsLifted = args.lift
    for {
      tuples <- ZIO
                 .tupled(
                   toValidation(checkPath),
                   toValidation(checkChapterSilence),
                   toValidation(checkPartitionThreshold),
                   toValidation(checkPartSilenceExists)
                 )
                 .flatMap {
                   case (a, b, c, d) =>
                     Validation.tupledPar(a, b, c, d).toZIO.mapError(_.toIterable)
                 }
      (pathToXml, chapterSilence, partitionThreshold, partSilenceStr) = tuples
      // parsing the part silence duration can only happen after parsing chapter silence
      partSilence <- checkPartSilence(chapterSilence, partSilenceStr).mapError(List(_))
    } yield Arguments(pathToXml, chapterSilence, partitionThreshold, partSilence)
  }

  private def checkPath(implicit args: Int => Option[String]) =
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

  private def checkChapterSilence(implicit args: Int => Option[String]) =
    args(1) match {
      case None =>
        ZIO.fail("Missing duration for silence between chapters")
      case Some(durationStr) =>
        ZIO
          .effect(Duration.parse(durationStr))
          .mapError(_ => s"""Text \"$durationStr\" cannot be parsed to a Duration""")
    }

  private def checkPartitionThreshold(implicit args: Int => Option[String]) =
    args(2) match {
      case None =>
        ZIO.fail("Missing segment threshold duration")
      case Some(durationStr) =>
        ZIO
          .effect(Duration.parse(durationStr))
          .mapError(_ => s"""Text \"$durationStr\" cannot be parsed to a Duration""")
    }

  private def checkPartSilenceExists(implicit args: Int => Option[String]) =
    ZIO.fromOption(args(3)).mapError(_ => "Missing duration for silence between parts")

  private def checkPartSilence(chapterSilence: Duration, partSilenceStr: String) =
    ZIO
      .effect(Duration.parse(partSilenceStr))
      .mapError(_ => s"""Text \"$partSilenceStr\" cannot be parsed to a Duration""")
      .flatMap { partSilence =>
        if (partSilence.compareTo(chapterSilence) < 0) ZIO.succeed(partSilence)
        else
          ZIO.fail(
            s"Part silence duration ${partSilence.toString} should be less " +
              s"than chapter silence duration ${chapterSilence.toString}"
          )
      }

  private def toValidation[R, A](check: ZIO[R, String, A]) =
    check.either.map(Validation.fromEither)
}
