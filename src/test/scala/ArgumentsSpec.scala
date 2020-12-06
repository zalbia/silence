package com.github.zalbia.silence

import zio.test._
import zio.test.Assertion._
import zio.ZIO

object ArgumentsSpec extends DefaultRunnableSpec {
  def spec = suite("ArgumentsSpec")(
    testM("valid arguments can be parsed") {
      assertM(
        Arguments.from(List("silence1.xml", "PT10S", "PT20M", "PT5S")).run
      )(succeeds(anything))
    },
    testM("fails with 4 errors given no args") {
      assertM(Arguments.from(Nil).run)(fails(hasSize(equalTo(4))))
    },
    testM("fails with 1 error on non-existent file") {
      assertM(Arguments.from(List("non-existent.xml", "PT10S", "PT20M", "PT5S")).run)(
        fails(hasSize(equalTo(1)))
      )
    },
    testM("fails with 2 errors given invalid periods") {
      assertM(Arguments.from(List("silence1.xml", "a", "b", "c")).run)(fails(hasSize(equalTo(2))))
    },
    testM("fails given part silence duration not less than chapter silence duration") {
      ZIO.mapN(
        assertM(Arguments.from(List("silence1.xml", "PT10S", "PT20M", "PT10S")).run)(
          fails(hasSize(equalTo(1)))
        ),
        assertM(Arguments.from(List("silence1.xml", "PT10S", "PT20M", "PT20S")).run)(
          fails(hasSize(equalTo(1)))
        )
      )(_ && _)
    }
  )
}
