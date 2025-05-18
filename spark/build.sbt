ThisBuild / scalaVersion := "2.12.18"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / organization := "org.gsv-graphs"

lazy val sparkVersion = "3.5.0"

enablePlugins(AssemblyPlugin)

assembly / mainClass := None

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-core" % "2.20.0",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.20.0",
  "org.apache.spark" %% "spark-core"   % sparkVersion,
  "org.apache.spark" %% "spark-sql"    % sparkVersion,
  "org.apache.spark" %% "spark-graphx" % sparkVersion
)
