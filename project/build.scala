import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._
import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._

object DiscemoneServerBuild extends Build {
  val Organization = "org.bustos"
  val Name = "DiscemoneServer"
  val Version = "1.0"
  val ScalaVersion = "2.10.2"
  val ScalatraVersion = "2.2.2"

  //lazy val discemone = RootProject(file("../../scala/discemone"))
  //lazy val rxtx_akka_io = RootProject(uri("git://github.com/msiegenthaler/rxtx-akka-io.git"))

  lazy val discemoneServerRoot = Project (
    "discemoneServer",
    file("."),
    settings = seq(com.typesafe.sbt.SbtStartScript.startScriptForClassesSettings: _*) ++ Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += Classpaths.typesafeResolver,
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      libraryDependencies ++= Seq( 
	"org.slf4j" % "slf4j-api" % "1.7.6",
    	"org.slf4j" % "slf4j-simple" % "1.7.6",
	"org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
	"org.scalatra" %% "scalatra-json" % "2.2.2", 
  	"org.json4s"   %% "json4s-native" % "3.2.6",
	//"ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
	"ch.inventsoft.akka" %% "rxtx-akka-io" % "1.0.2",
        //"org.rxtx" % "rxtx" % "2.1.7",
	"com.typesafe.akka" %% "akka-actor" % "2.2.3",
	"net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
	"org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container;compile",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;compile;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    ) ++ assemblySettings) settings (
	  mainClass := Some("org.bustos.discemoneServer.JettyLauncher")) settings (
	  mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => 
      	  	{ 
            	  case x if x startsWith ".classpath" => MergeStrategy.concat 
            	  case x if x startsWith ".project" => MergeStrategy.concat 
            	  case x if x startsWith "log4j.properties" => MergeStrategy.concat 
            	  case x if x startsWith "library.properties" => MergeStrategy.concat 
            	  case x => old(x) 
      	  	} 
	  },
	  // copy web resources to /webapp folder
  	  resourceGenerators in Compile <+= (resourceManaged, baseDirectory) map {
    	      (managedBase, base) =>
      	      val webappBase = base / "src" / "main" / "webapp"
      	      for {
              	  (from, to) <- webappBase ** "*" x rebase(webappBase, managedBase / "main" / "webapp")
      	      } yield {
                  Sync.copy(from, to)
          	  to
      	      }
  	  }
    ) settings (net.virtualvoid.sbt.graph.Plugin.graphSettings: _*) //dependsOn (rxtx_akka_io) dependsOn (discemone)

}
