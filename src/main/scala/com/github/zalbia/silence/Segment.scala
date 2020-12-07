package com.github.zalbia.silence

import java.time.Duration

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

final case class Segment(title: String, offset: Duration)
final case class Segments(segments: Iterator[Segment])

object Segment {
  def fromChapter(chapter: Chapter): List[Segment] = ???

  implicit val segmentCodec: JsonValueCodec[Segment] = JsonCodecMaker.make
  implicit val segmentsCodec: JsonValueCodec[Iterator[Segment]] = new JsonValueCodec[Iterator[Segment]] {

    def decodeValue(in: JsonReader, segments: Iterator[Segment]): Iterator[Segment] = ??? // don't need this

    def encodeValue(segments: Iterator[Segment], out: JsonWriter): Unit = {
      out.writeObjectStart()
      out.writeKey("segments")
      out.writeArrayStart()
      segments.foreach(segment => segmentCodec.encodeValue(segment, out))
      out.writeArrayEnd()
      out.writeObjectEnd()
    }

    def nullValue: Iterator[Segment] = Iterator.empty

  }
}
