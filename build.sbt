import AssemblyKeys._

assemblySettings

name := "pio-image-classifier"

organization := "org.apache.predictionio"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.predictionio"     %% "apache-predictionio-core" % "0.12.0-incubating" % "provided",
  "org.apache.spark"            %% "spark-core"               % "2.1.1" % "provided",
  "org.apache.spark"            %% "spark-mllib"              % "2.1.1" % "provided",
  "org.tensorflow"              % "tensorflow"                % "1.7.0",
  "com.github.fommil.netlib"    % "all"                       % "1.1.2" pomOnly(),
  "org.xerial.snappy"           % "snappy-java"               % "1.1.1.7",
  "net.java.dev.jna"            % "jna"                       % "4.2.2",
  "com.esotericsoftware"        % "kryo"                      % "4.0.1"
)
