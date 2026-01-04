package buildsrc.convention

import com.diffplug.spotless.LineEnding

plugins {
  id("com.diffplug.spotless")
}

spotless {
  encoding("UTF-8")
  lineEndings = LineEnding.UNIX
  kotlinGradle {
    target("**/*.gradle.kts")
    ktfmt()
    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlin {
    target("**/src/**/*.kt")
    ktfmt()
    trimTrailingWhitespace()
    endWithNewline()
  }
  format("misc") {
    target("**/*.md", "**/*.yml", "**/*.yaml", "**/*.properties")
    trimTrailingWhitespace()
    endWithNewline()
  }
}