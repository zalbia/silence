package com.github.zalbia.silence

import java.time.Duration
import zio.blocking.Blocking
import zio.prelude.NonEmptyList
import zio.stream.ZStream

final case class Chapter(parts: NonEmptyList[Part])
final case class Part(offset: Duration)

object Chapter {

  def readFromXml(args: Arguments): ZStream[Blocking, Throwable, Chapter] = {
    Silence.read(args.pathToXml)
    ???
  }
}
