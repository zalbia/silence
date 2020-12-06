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
        es => putStrLnErr("Errors:\n") *> putStrLnErr(es.mkString("\n")),
        _ => ZIO.unit
      )
}
