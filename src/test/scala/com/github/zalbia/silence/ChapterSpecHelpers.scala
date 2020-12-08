package com.github.zalbia.silence

import java.time.Duration

import zio.Chunk
import zio.stream.ZStream
import zio.test.Assertion._
import zio.test._

trait ChapterSpecHelpers {
  val dummyDurations = Durations(Duration.ZERO, Duration.ZERO, Duration.ZERO)

  val durations = Durations(
    chapterSilence = Duration.ofSeconds(5),
    partitionThreshold = Duration.ofMinutes(30),
    partSilence = Duration.ofSeconds(3)
  )

  def assertChaptersFromSilences(silences: Silence*)(chapters: Chapter*) =
    assertM(
      Chapter
        .fromSilences(
          durations,
          ZStream(silences: _*)
        )
        .runCollect
    )(
      equalTo(
        Chunk(
          chapters: _*
        )
      )
    )
}
