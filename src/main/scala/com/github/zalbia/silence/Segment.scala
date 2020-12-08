package com.github.zalbia.silence

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import zio.prelude.NonEmptyList

// DTO object
final case class Segment(title: String, offset: Offset)

object Segment {
  def fromChapter(chapterWithindex: (Chapter, Long)): NonEmptyList[Segment] =
    chapterWithindex match {
      case (chapter, i) =>
        if (chapter.parts.length == 1) {
          NonEmptyList(Segment(s"Chapter ${i + 1}", Offset.parse(chapter.parts.head.offset.toString)))
        } else {
          chapter.parts.zipWithIndex.map {
            case (part, j) =>
              Segment(s"Chapter ${i + 1}, part ${j + 1}", Offset.parse(part.offset.toString))
          }
        }
    }

  implicit val segmentCodec: JsonValueCodec[Segment] = JsonCodecMaker.make
  implicit val segmentsCodec: JsonValueCodec[Iterator[Segment]] = new JsonValueCodec[Iterator[Segment]] {
    // not needed
    def decodeValue(in: JsonReader, segments: Iterator[Segment]): Iterator[Segment] = ???

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
