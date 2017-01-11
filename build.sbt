organization := "tv.cntt"
name         := "xitrum-ko"
version      := "1.8.0-SNAPSHOT"

crossScalaVersions := Seq("2.12.1", "2.11.8")
scalaVersion := "2.12.1"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// Xitrum requires Java 8
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

//------------------------------------------------------------------------------

libraryDependencies += "tv.cntt" %% "xitrum" % "3.28.2" % "provided"

libraryDependencies += "org.webjars.bower" % "knockoutjs" % "3.4.0"
libraryDependencies += "org.webjars.bower" % "knockout-mapping" % "2.4.1"

//------------------------------------------------------------------------------

// Skip API doc generation to speedup "publish-local" while developing.
// Comment out this line when publishing to Sonatype.
publishArtifact in (Compile, packageDoc) := false
