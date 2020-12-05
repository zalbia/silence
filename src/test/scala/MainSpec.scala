package com.github.zalbia.silence

import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import Main._

object MainSpec extends DefaultRunnableSpec {
  def spec = suite("HelloWorldSpec")(
    testM("sayHello correctly displays output") {
      for {
        _      <- sayHello
        output <- TestConsole.output
      } yield assert(output)(equalTo(Vector("Hello, World!\n")))
    }
  )
}
