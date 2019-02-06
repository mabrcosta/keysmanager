name := "keysmanager-scala"
version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= {
  val playSlickV = "4.0.0"
  Seq(
    guice,
    evolutions,
    "com.typesafe.play"             %%  "play-slick"                  % playSlickV,
    "com.typesafe.play"             %%  "play-slick-evolutions"       % playSlickV,
    "com.byteslounge"               %%  "slick-repo"                  % "1.5.2",
    "org.postgresql"                %   "postgresql"                  % "42.2.5",
    "com.h2database"                %   "h2"                          % "1.4.197",
    "org.flywaydb"                  %   "flyway-core"                 % "5.0.7",
    "com.typesafe"                  %   "config"                      % "1.3.3",
    "com.typesafe.scala-logging"    %%  "scala-logging"               % "3.8.0",
    "ch.qos.logback"                %   "logback-classic"             % "1.2.3",
    "net.codingwell"                %%  "scala-guice"                 % "4.1.1",
    "org.typelevel"                 %%  "cats-core"                   % "1.6.0",
    "org.atnos"                     %%  "eff"                         % "5.4.1",
    "org.scalatest"                 %%  "scalatest"                   % "3.0.5"     % Test,
    "org.mockito"                   %   "mockito-core"                % "2.16.0"    % Test
  )
}

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")
