// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {

    object Version {
      val jsoniter = "2.6.2"
      val xs4sZio = "0.8.2"
      val zio = "1.0.3"
      val zioConfig = "1.0.0-RC31"
      val zioNio = "1.0.0-RC10"
      val zioPrelude = "1.0.0-RC1"
    }

    val jsoniterCore = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % Version.jsoniter
    val jsoniterMacros = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % Version.jsoniter
    val xs4sZio = "com.scalawilliam" %% "xs4s-zio" % Version.xs4sZio

    val zio = "dev.zio" %% "zio" % Version.zio
    val zioConfig = "dev.zio" %% "zio-config" % Version.zioConfig
    val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % Version.zioConfig
    val zioNio = "dev.zio" %% "zio-nio" % Version.zioNio
    val zioPrelude = "dev.zio" %% "zio-prelude" % Version.zioPrelude
    val zioTest = "dev.zio" %% "zio-test" % Version.zio
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % Version.zio
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
        library.jsoniterCore,
        library.jsoniterMacros,
        library.xs4sZio,
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
    organization := "com.github.zalbia"
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
  "-deprecation"
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