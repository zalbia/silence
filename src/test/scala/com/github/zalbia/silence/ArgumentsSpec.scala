package com.github.zalbia.silence

import zio.test._
import zio.test.Assertion._
import zio.ZIO

object ArgumentsSpec extends DefaultRunnableSpec {
  def spec = suite("parsing arguments")(
    testM("works with valid args") {
      assertM(
        Arguments.parse(List("silence1.xml", "PT10S", "PT20M", "PT5S")).run
      )(succeeds(anything))
    },
    testM("fails with 4 errors given no args") {
      assertM(Arguments.parse(Nil).run)(fails(hasSize(equalTo(4))))
    },
    testM("fails with 1 error given path to non-existent file") {
      assertM(Arguments.parse(List("non-existent.xml", "PT10S", "PT20M", "PT5S")).run)(
        fails(hasSize(equalTo(1)))
      )
    },
    testM("fails with 2 errors given invalid periods") {
      assertM(Arguments.parse(List("silence1.xml", "a", "b", "c")).run)(fails(hasSize(equalTo(2))))
    },
    testM("fails given part silence duration not less than chapter silence duration") {
      ZIO.mapN(
        assertM(Arguments.parse(List("silence1.xml", "PT10S", "PT20M", "PT10S")).run)(
          fails(hasSize(equalTo(1)))
        ),
        assertM(Arguments.parse(List("silence1.xml", "PT10S", "PT20M", "PT20S")).run)(
          fails(hasSize(equalTo(1)))
        )
      )(_ && _)
    }
  )
}
