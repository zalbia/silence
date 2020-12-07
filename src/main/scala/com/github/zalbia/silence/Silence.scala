package com.github.zalbia.silence

import java.time.Duration

import xs4s._
import xs4s.ziocompat._
import xs4s.syntax.zio._

import zio.blocking.Blocking
import zio.nio.core.file.Path
import zio.stream.{ Stream, ZStream }

final case class Silence(from: Duration, until: Duration)

object Silence {

  def read(pathToXml: Path): ZStream[Blocking, Throwable, Silence] =
    Stream
      .fromFile(pathToXml.toFile.toPath)
      .via(byteStreamToXmlEventStream()(_))
      .via(XmlElementExtractor.filterElementsByName("silence").toZIOPipeThrowError(_))
      // no smart constructor as we assume XML makes sense for this assignment
      .map(silence => Silence(Duration.parse(silence \@ "from"), Duration.parse(silence \@ "until")))
}
