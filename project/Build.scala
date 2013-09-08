import sbt._
import Keys._
import play.Project._
import cloudbees.Plugin._



object ApplicationBuild extends Build {

  val appName         = "Website"
  val appVersion      = "0.2"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "securesocial" %% "securesocial" % "master-SNAPSHOT"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(defaultJavaSettings:_*).settings(
	  cloudBeesSettings :_*).settings(
      CloudBees.applicationId := Some("willu/web"),
      CloudBees.username := Some("willu"),
      CloudBees.apiKey := Some("DD8E513F1DEEE470"),
      CloudBees.apiSecret := Some("ZBRQXZV/ED1OLC1GUF7R34LJJEDD0CNPRMMXZORAHZC="),
      CloudBees.host := "https://api-eu.cloudbees.com/api",
      CloudBees.deployParams := Map("runtime.java_version" -> "1.7"),
      CloudBees.openOnUpload := false).settings(
  	  resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
	  )
}
