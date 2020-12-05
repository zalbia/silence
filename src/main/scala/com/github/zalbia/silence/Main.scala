package com.github.zalbia.silence

import zio.App
import zio.console._
import zio.ZIO

object Main extends App {

  def run(args: List[String]) =
    app(args).exitCode

  def app(args: List[String]) =
    for {
      _    <- ZIO.succeed(Arguments.from(args))
      name <- getStrLn
      _    <- putStrLn(s"Hello, $name, welcome to ZIO!")
    } yield ()

  def sayHello: ZIO[Console, Nothing, Unit] =
    putStrLn("Hello, World!")
}
