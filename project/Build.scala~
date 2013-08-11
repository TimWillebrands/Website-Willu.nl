import sbt._
import Keys._
import play.Project._
import cloudbees.Plugin._



object ApplicationBuild extends Build {

  val appName         = "Website"
  val appVersion      = "0.1"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    // "mysql" % "mysql-connector-java" % "5.1.18"
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
	  cloudBeesSettings :_*).settings(
      CloudBees.applicationId := Some("rave/web"),
      CloudBees.username := Some("rave5887"),
      CloudBees.apiKey := Some("DD8E513F1DEEE470"),
      CloudBees.apiSecret := Some("ZBRQXZV/ED1OLC1GUF7R34LJJEDD0CNPRMMXZORAHZC="),
      CloudBees.host := "https://api-eu.cloudbees.com/api",
      CloudBees.deployParams := Map("runtime.java_version" -> "1.7"),
      CloudBees.openOnUpload := false)


}
