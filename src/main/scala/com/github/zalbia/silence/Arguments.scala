package com.github.zalbia.silence

import zio.nio.core.file.Path
import java.time.Duration
import zio.prelude.Validation

final case class Arguments private (
  path: Path,
  chapterSilence: Duration,
  segmentThreshold: Duration,
  partSilence: Duration
)

object Arguments {
  def from(args: List[String]): Validation[String, Arguments] = ???
}
