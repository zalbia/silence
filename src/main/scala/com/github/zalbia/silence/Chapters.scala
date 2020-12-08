package com.github.zalbia.silence

import java.time.Duration

import zio.blocking.Blocking
import zio.prelude.{ NonEmptyList => NEL }
import zio.stream.ZStream

final case class Chapter private[silence] (parts: NEL[Part]) {
  lazy val offset: Offset = parts.head.offset

  def duration(end: Offset): Duration = end minus offset

  lazy val durationFromParts: Option[Duration] =
    parts.lastOption.map(part => part.offset minus offset)
}

final case class Part private[silence] (offset: Offset)
object Part {
  def apply(s: CharSequence): Part = Part(Offset(s))
}

object Chapter {
  val oneParts = NEL(Part(Offset.ZERO))
  val one      = Chapter(oneParts)

  def apply(part: Part, parts: Part*): Chapter = Chapter(NEL(part, parts: _*))

  def readFromXml(args: Arguments): ZStream[Blocking, Throwable, Chapter] = {
    val silences = Silence.readFromXml(args.pathToXml)
    Chapter.fromSilences(args.durations, silences)
  }

  private[silence] def fromSilences[R, E](durations: Durations, silences: ZStream[R, E, Silence]) = {
    import durations._
    (ZStream(Silence.zero) ++ silences
      .filter(silence => isRelevant(silence.duration))).zipWithNext
      .mapAccum(Chapter.oneParts) {
        case (list, (_, None)) =>
          (list, Some(list))
        case (list, (_, Some(next))) if isChapterSilence(next.duration) =>
          (NEL(Part(next.until)), Some(list))
        case (list, (_, Some(next))) =>
          (NEL.cons(Part(next.until), list), None)
      }
      .collect { case Some(l) => Chapter(l.reverse) }
      .zipWithNext
      .map {
        case (chapter, None) =>
          chapter.durationFromParts.fold(chapter) { duration =>
            if ((duration compareTo partitionThreshold) >= 0) chapter
            else Chapter(chapter.parts.head)
          }
        case (chapter, Some(next)) =>
          if ((chapter.duration(next.offset) compareTo partitionThreshold) >= 0) chapter
          else Chapter(chapter.parts.head)
      }
  }
}
