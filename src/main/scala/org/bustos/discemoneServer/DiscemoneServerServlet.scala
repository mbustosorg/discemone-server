package org.bustos.discemoneServer

import org.scalatra._
import org.scalatra.json._
import org.scalatra.{Accepted, AsyncResult, FutureSupport, ScalatraServlet}

import scalate.ScalateSupport

import org.json4s.{DefaultFormats, Formats}
import org.slf4j.{Logger, LoggerFactory}

import _root_.akka.actor.{ActorRef, Actor, ActorSystem}
import _root_.akka.util.Timeout
import _root_.akka.pattern.ask
import _root_.akka.actor.Status.{ Success, Failure }

import scala.concurrent.{ExecutionContext, Future, Promise, Await}
import scala.concurrent.duration._
  
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
  
  val logger = LoggerFactory.getLogger(getClass)
  
  import org.bustos.discemone.Discemone._
  //import org.bustos.discemoneServer.DiscemoneMock._
  
  implicit val defaultTimeout = Timeout(1000)
  
  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
	//response.setHeader("Allow", "PUT, POST, GET")
  }
  
  options("/shutdown") {
    response.setHeader("Allow", "PUT")
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
  
  get("/metrics/cpu") {    
	  	contentType = formats("json")
        val query = discemoneActor ? TimeSeriesRequestCPU
        Await.result (query, 1 second)
  }
  
  get("/metrics/battery") {
	  	contentType = formats("json")
        val query = discemoneActor ? TimeSeriesRequestBattery
        Await.result (query, 1 second)    
  }
  
  get("/metrics/memory") {
	  	contentType = formats("json")
        val query = discemoneActor ? TimeSeriesRequestMemory
        Await.result (query, 1 second)    
  }
  
  get("/metrics/sensors") {
	  	contentType = formats("json")
        val query = discemoneActor ? ListRequestSensor
        Await.result (query, 1 second)    
  }
  
  get("/metrics/sensors/:id/activityLevel") {
	  	contentType = formats("json")
        val query = discemoneActor ? SensorActivityLevel(params("id"))
        Await.result (query, 1 second)    
  }
  
  get("/memberCount") {
	  	contentType = formats("json")
        val query = discemoneActor ? "MEMBER_COUNT"
        Await.result (query, 1 second).toString
  }
  
  get("/members") {
	  	contentType = formats("json")
        val query = discemoneActor ? ListRequestMember
        Await.result (query, 1 second)
  }
  
  get("/members/:id") {
	  	contentType = formats("json")
        val query = discemoneActor ? MemberDetail(params("id"), 0, 0, 0.0f, 0.0f, 0.0f, 0.0f)
        Await.result (query, 1 second)    
  }
  
  put("/parameters/sensor/:id") {
    val threshold: Int = params.getOrElse("threshold", "-1").toInt
    val filterLength: Int = params.getOrElse("filterLength", "-1").toInt
    discemoneActor ! SensorDetail(params("id"), threshold, filterLength)
    status = 204
  }
  
  put("/shutdown") {
    logger.info ("shutdown request received")
  }
  
  put("/startup") {
    logger.info ("startup request received")
    
  }
  
  put("/restart") {
    logger.info ("restart request received")    
  }
  
  put("/pattern/:id/") {
    params("intensity")
    params("red")
    params("green")
    params("blue")
    params("speed")
  }
  
}
