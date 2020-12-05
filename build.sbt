// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {

    object Version {
      val zio = "1.0.3"
      val zioConfig = "1.0.0-RC31"
      val zioNio = "1.0.0-RC10"
      val zioPrelude = "1.0.0-RC1"
    }

    val zio = "dev.zio" %% "zio" % Version.zio
    val zioTest = "dev.zio" %% "zio-test" % Version.zio
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % Version.zio

    val zioConfig = "dev.zio" %% "zio-config" % Version.zioConfig
    val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % Version.zioConfig
    val zioNio = "dev.zio" %% "zio-nio" % Version.zioNio
    val zioPrelude = "dev.zio" %% "zio-prelude" % Version.zioPrelude
  }

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val root =
  project
    .in(file("."))
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.zio,
        library.zioConfig,
        library.zioConfigMagnolia,
        library.zioNio,
        library.zioPrelude,
        library.zioTest % Test,
        library.zioTestSbt % Test
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
    commandAliases

lazy val commonSettings =
  Seq(
    name := "beat-tech-silence",
    scalaVersion := "2.13.4",
    organization := "com.example"
  )

lazy val commandAliases =
  addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt") ++
    addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")


// *****************************************************************************
// Scalac
// *****************************************************************************

lazy val stdOptions = Seq(
  "-encoding",
  "UTF-8",
  "-explaintypes",
  "-Yrangepos",
  "-feature",
  "-language:higherKinds",
  "-language:existentials",
  "-Xlint:_,-type-parameter-shadow",
  "-Xsource:2.13",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-unchecked",
  "-deprecation",
  "-Xfatal-warnings"
)

lazy val stdOpts213 = Seq(
  "-Wunused:imports",
  "-Wvalue-discard",
  "-Wunused:patvars",
  "-Wunused:privates",
  "-Wunused:params",
  "-Wvalue-discard"
)

scalacOptions := stdOptions ++ stdOpts213