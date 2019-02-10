name := "keys-manager"
version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= {
  val playSlickV = "4.0.0"
  Seq(
    guice,
    evolutions,
    ws,
    "com.typesafe.play"             %%  "play-slick"                  % playSlickV,
    "com.typesafe.play"             %%  "play-slick-evolutions"       % playSlickV,
    "com.byteslounge"               %%  "slick-repo"                  % "1.5.2",
    "org.postgresql"                %   "postgresql"                  % "42.2.5",
    "org.typelevel"                 %%  "cats-core"                   % "1.6.0",
    "org.atnos"                     %%  "eff"                         % "5.4.1",
    "org.scalatest"                 %%  "scalatest"                   % "3.0.5"     % Test,
    "org.mockito"                   %   "mockito-core"                % "2.16.0"    % Test,
    "com.h2database"                %   "h2"                          % "1.4.197"   % Test
  )
}

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")

packageName in Docker := "keys-manager"

dockerBaseImage := "anapsix/alpine-java:8"
version in Docker := version.value+"-"+git.gitCurrentBranch.value.replaceAll("/", "_")
dockerEntrypoint ++= Seq(
  "-Dconfig.file=/data/production.conf",
  "-Duser.timezone=Europe/Lisbon",
  "-Dlogger.file=/data/logback.xml"
)
