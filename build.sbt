import uk.gov.hmrc.DefaultBuildSettings

val appName = "integration-catalogue-admin-api"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.5.1"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    routesImport                     += "uk.gov.hmrc.integrationcatalogueadmin.controllers.binders._",
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    Test / unmanagedSourceDirectories += baseDirectory(_ / "test-common").value
  )
  .settings(scoverageSettings)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(scalacOptions ++= Seq("-deprecation", "-feature"))
  .settings(scalacOptions += "-Wconf:src=routes/.*:s")
  .settings(scalacOptions += "-Wconf:msg=Flag.*repeatedly:s")

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := ",.*\\.domain\\.models\\..*,uk\\.gov\\.hmrc\\.BuildInfo,.*\\.utils\\..*,.*\\.config\\..*,.*\\.models\\..*,.*\\.Routes,.*\\.RoutesPrefix,,Module,GraphiteStartUp,.*\\.Reverse[^.]*",
    ScoverageKeys.coverageMinimumStmtTotal := 96,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    Test / parallelExecution  := false
  )
}

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.it)
  .settings(scalacOptions += "-Wconf:msg=Flag.*repeatedly:s")
