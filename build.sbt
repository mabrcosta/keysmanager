name := "keysmanager-scala"
version := "0.1"
scalaVersion := "2.12.4"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= {
  val slickV = "3.2.2"
  val akkaHttpV = "10.1.0"
  Seq(
    "com.typesafe.slick"            %%  "slick"                       % slickV,
    "com.typesafe.slick"            %%  "slick-hikaricp"              % slickV,
    "com.byteslounge"               %%  "slick-repo"                  % "1.4.3",
    "org.postgresql"                %   "postgresql"                  % "42.0.0",
    "org.flywaydb"                  %   "flyway-core"                 % "4.1.2",
    "com.typesafe"                  %   "config"                      % "1.3.1",
    "com.typesafe.scala-logging"    %%  "scala-logging"               % "3.5.0",
    "ch.qos.logback"                %   "logback-classic"             % "1.2.3",
    "net.codingwell"                %%  "scala-guice"                 % "4.1.1",
    "org.typelevel"                 %%  "cats-core"                   % "1.0.1",
    "org.atnos"                     %%  "eff"                         % "5.1.0",
    "com.typesafe.akka"             %%  "akka-http"                   % akkaHttpV,
    "com.typesafe.akka"             %%  "akka-http-spray-json"        % akkaHttpV,
    "com.typesafe.akka"             %%  "akka-stream"                 % "2.5.11"
  )
}

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")