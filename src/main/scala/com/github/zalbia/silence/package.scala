package com.github.zalbia

import java.time.Duration

package object silence {
  type Offset = Duration

  object Offset {
    def apply(s: CharSequence): Offset = parse(s)
    def parse(s: CharSequence): Offset = Duration.parse(s)
    val ZERO: Offset                   = Duration.ZERO
  }
}
