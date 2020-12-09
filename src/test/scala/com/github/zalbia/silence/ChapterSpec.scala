package com.github.zalbia.silence

import zio.stream.ZStream
import zio.test.Assertion._
import zio.test._

object ChapterSpec extends DefaultRunnableSpec with ChapterSpecHelpers {
  def spec =
    suite("chapters")(
      testM("there's always at least one") {
        assertM(
          Chapter.fromSilences(dummyDurations, ZStream.empty).runCollect
        )(hasSize(equalTo(1)))
      },
      testM("are split by chapter silence duration") {
        assertChaptersFromSilences(
          Silence("PT15M", "PT15M5S")
        )(
          Chapter(Part("PT0S")),
          Chapter(Part("PT15M5S"))
        )
      },
      testM("are split into parts when long enough") {
        assertChaptersFromSilences(
          Silence("PT15M", "PT15M3S"),
          Silence("PT30M", "PT30M5S")
        )(
          Chapter(Part("PT0S"), Part("PT15M3S")),
          Chapter(Part("PT30M5S"))
        )
      },
      testM("can be split up if the duration we know from parts is long enough") {
        assertChaptersFromSilences(
          Silence("PT30M", "PT30M3S")
        )(
          Chapter(Part("PT0S"), Part("PT30M3S"))
        )
      },
      testM("aren't split to parts when not long enough") {
        assertChaptersFromSilences(
          Silence("PT15M", "PT15M3S"),
          Silence("PT20M", "PT20M5S")
        )(
          Chapter(Part("PT0S")),
          Chapter(Part("PT20M5S"))
        )
      },
      testM("can be split by multiple chapter silences") {
        assertChaptersFromSilences(
          Silence("PT10M", "PT10M5S"),
          Silence("PT20M", "PT20M5S"),
          Silence("PT30M", "PT30M5S")
        )(
          Chapter(Part("PT0S")),
          Chapter(Part("PT10M5S")),
          Chapter(Part("PT20M5S")),
          Chapter(Part("PT30M5S"))
        )
      },
      testM("aren't indicated by silences that are too short") {
        assertChaptersFromSilences(
          Silence("PT10M", "PT10M1S")
        )(
          Chapter(Part("PT0S"))
        )
      }
    )
}
