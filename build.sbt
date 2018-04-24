import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.5",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies += scalaTest % Test
  )

  scalacOptions in ThisBuild ++= Seq(
    "-language:_",
    "-Ypartial-unification",
    "-Xfatal-warnings"
  )

  libraryDependencies ++= Seq(
    "com.github.mpilquist" %% "simulacrum"     % "0.12.0",
    "com.chuusai"          %% "shapeless"      % "2.3.3" ,
    "org.scalaz"           %% "scalaz-core"    % "7.2.21"
  )

  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
  addCompilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
  )
