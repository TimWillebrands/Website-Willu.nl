import sbt._
import Keys._
import play.Project._
import cloudbees.Plugin._



object ApplicationBuild extends Build {

  val appName         = "Willu Website"
  val appVersion      = "0.2"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    //"mysql" % "mysql-connector-java" % "5.1.18"
    //"postgresql" % "postgresql" % "9.1-901-1.jdbc4"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
	  cloudBeesSettings :_*).settings(
      CloudBees.applicationId := Some("willu/web"),
      CloudBees.username := Some("willu"),
      CloudBees.apiKey := Some("17C8AE233DCE0056"),
      CloudBees.apiSecret := Some("YKW1LI1SHLZRQ7VJYQQAKXT/THLEKMORQSQKR1HX5TC="),
      CloudBees.host := "https://api-eu.cloudbees.com/api",
      CloudBees.deployParams := Map("runtime.java_version" -> "1.7"),
      CloudBees.openOnUpload := false)


}
