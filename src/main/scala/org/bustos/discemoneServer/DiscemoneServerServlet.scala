package org.bustos.discemoneServer

import org.scalatra._
import org.scalatra.json._
import org.scalatra.{Accepted, AsyncResult, FutureSupport, ScalatraServlet}

import scalate.ScalateSupport

import org.json4s.{DefaultFormats, Formats}

import _root_.akka.actor.{ActorRef, Actor, ActorSystem}
import _root_.akka.util.Timeout
import _root_.akka.pattern.ask
import _root_.akka.actor.Status.{ Success, Failure }

import scala.concurrent.{ExecutionContext, Future, Promise, Await}

/** Servlet main class for serving Discemone data
 * 
 *  @constructor create a new servlet
 *  @param system the discemone actor system
 *  @param discemoneActor the controller actor
 */
class DiscemoneServerServlet(system: ActorSystem, discemoneActor: ActorRef) extends DiscemoneServerStack 
																					with FutureSupport 
																					with NativeJsonSupport 
																					with CorsSupport {
  protected implicit def executor: ExecutionContext = system.dispatcher
  protected implicit val jsonFormats: Formats = DefaultFormats

  import scala.concurrent.duration._
  
  import org.bustos.discemone.Discemone._
  
  implicit val defaultTimeout = Timeout(100)
  
  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }
  
  get("/") {
	  contentType = "text/html"
    <html>
	  <head>
	    <link rel="stylesheet" type="text/css" href="css/styles.css" />
	  </head>
      <body>
        <h1>Hello, world!</h1>
        <p>Say <a href="hello-scalate">hello to Scalate</a>.</p>
      </body>
    </html>
  }
  
  get("/hello-scalate") {
	  contentType = "text/html"
    <html>
      <body>
        <h1>Hello, dudes!</h1>
        Say <a href="/">hello to Scalate</a>.
      </body>
    </html>
  }
  
  get("/actor_hello") {    
	  	contentType = "text/html"
        val countQuery = discemoneActor ? "Count"
    	val result = Await.result (countQuery, 1 second)
    	// Add async logic here
        <html>
    			<body>
    			<h1>Hello, max!</h1>
    			{result}
    			</body>
    	</html>
  }
  
  get("/cpuTimeSeries") {    
	  	contentType = formats("json")
        val cpuQuery = discemoneActor ? CollectCPUtimeSeries
        Await.result (cpuQuery, 1 second)
  }
  
  get("/sparkline") {
	  contentType = "text/html"
	  layoutTemplate("sparkline.scaml")
  }
  
}
