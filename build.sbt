name := "keysmanager-scala"
version := "0.1"
scalaVersion := "2.12.4"

libraryDependencies ++= {
  val slickV = "3.2.2"
  Seq(
    "com.typesafe.slick"            %%  "slick"                       % slickV,
    "com.typesafe.slick"            %%  "slick-hikaricp"              % slickV,
    "com.typesafe"                  %   "config"                      % "1.3.1",
    "org.postgresql"                %   "postgresql"                  % "42.0.0",
    "com.typesafe.scala-logging"    %%  "scala-logging"               % "3.5.0",
    "ch.qos.logback"                %   "logback-classic"             % "1.2.3",
    "org.flywaydb"                  %   "flyway-core"                 % "4.1.2",
    "net.codingwell"                %%  "scala-guice"                 % "4.1.1",
    "com.trueaccord.scalapb"        %%  "scalapb-json4s"              % "0.1.6",
    "org.reflections"               %   "reflections"                 % "0.9.11",
    "io.spray"                      %%  "spray-json"                  % "1.3.3",
    "org.typelevel"                 %%  "cats-core"                   % "1.0.1",
    "com.byteslounge"               %%  "slick-repo"               % "1.4.3"
  )
}