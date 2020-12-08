package com.github.zalbia.silence

import java.time.Duration

import xs4s._
import xs4s.ziocompat._
import xs4s.syntax.zio._

import zio.blocking.Blocking
import zio.nio.core.file.Path
import zio.stream.{ Stream, ZStream }

// no smart constructor as we assume XML makes sense for this assignment
final case class Silence(from: Offset, until: Offset) {
  lazy val duration: Duration = until minus from
}

object Silence {
  def apply(from: CharSequence, until: CharSequence): Silence = Silence(Offset(from), Offset(until))

  val zero = Silence(Offset.ZERO, Offset.ZERO)

  def readFromXml(pathToXml: Path): ZStream[Blocking, Throwable, Silence] =
    Stream
      .fromFile(pathToXml.toFile.toPath)
      .via(byteStreamToXmlEventStream()(_))
      .via(XmlElementExtractor.filterElementsByName("silence").toZIOPipeThrowError(_))
      .map(silence => Silence(Duration.parse(silence \@ "from"), Duration.parse(silence \@ "until")))

}
