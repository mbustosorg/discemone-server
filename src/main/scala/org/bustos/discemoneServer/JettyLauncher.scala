package org.bustos.discemoneServer

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/** Jetty servlet main entry point
 *  
 *  Sets up port number and static file resource locations
 */
object JettyLauncher { 
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    //val resourceBase = getClass.getClassLoader.getResource("webapp").toExternalForm
    val resourceBase = "resource_managed/webapp"
    context.setResourceBase(resourceBase)
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")

    server.setHandler(context)

    server.start
    server.join
  }
}