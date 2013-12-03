import sbt._
import Keys._
import play.Project._



object ApplicationBuild extends Build {

  val appName         = "WilluWebsite"
  val appVersion      = "0.4"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "securesocial" %% "securesocial" % "2.1.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(defaultJavaSettings:_*).settings(
  	  resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
	  )
}
