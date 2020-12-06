package com.github.zalbia.silence

import zio.{ App, ExitCode, URIO, ZIO }
import zio.blocking.Blocking
import zio.console._

object Main extends App {

  def run(args: List[String]): URIO[Console with Blocking, ExitCode] =
    app(args).exitCode

  private def app(args: List[String]) =
    (for {
      arguments <- Arguments.from(args)
      _         <- putStrLn(arguments.toString)
    } yield ())
      .foldM(
        es =>
          for {
            _ <- putStrLnErr("Errors:")
            _ <- putStrLnErr(es.mkString("\n"))
            _ <- putStrLnErr("\nFormat:")
            _ <- putStrLnErr(
                  "<path-to-xml> <chapter-silence-duration>" +
                    " <partition-threshold-duration> <part-silence-duration>"
                )
            _ <- putStrLnErr("where duration is an ISO 8601 duration string")
          } yield (),
        _ => ZIO.unit
      )
}
