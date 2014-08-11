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
  protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal
  
  val logger = LoggerFactory.getLogger(getClass)
  
  //import org.bustos.discemone.Discemone._
  // *** Uncomment for Heroku deployment 
  import org.bustos.discemoneServer.DiscemoneMock._
  
  implicit val defaultTimeout = Timeout(1000)
  
  options("*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }
  
  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
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
        val future = discemoneActor ? TimeSeriesRequestCPU
        Await.result (future, 1 second)
  }
  
  get("/metrics/battery") {
        val future = discemoneActor ? TimeSeriesRequestBattery
        Await.result (future, 1 second)    
  }
  
  get("/metrics/memory") {
        val future = discemoneActor ? TimeSeriesRequestMemory
        Await.result (future, 1 second)    
  }
  
  get("/metrics/sensors") {
        val future = discemoneActor ? ListRequestSensor
        Await.result (future, 1 second)    
  }
  
  get("/metrics/sensors/:id/activityLevel") {
        val future = discemoneActor ? SensorActivityLevel(params("id"))
        Await.result (future, 1 second)    
  }
  
  get("/memberCount") {
        val future = discemoneActor ? "MEMBER_COUNT"
        Await.result (future, 1 second).toString
  }
  
  get("/members") {
        val future = discemoneActor ? ListRequestMember
        Await.result (future, 1 second)
  }
  
  get("/members/:id") {
	  val future = discemoneActor ? MemberDetail(params("id"), "", 0, 0.0f, 0.0f, 0.0f, 0.0f)
	  Await.result (future, 1 second)    
  }
  
  get("/pattern/names") {
	  val future = discemoneActor ? PatternNames
	  Await.result (future, 1 second)
  }
  
  get("/pattern/current") {
	  val future = discemoneActor ? CurrentPattern
	  Await.result (future, 1 second)    
  }
  
  put("/sensor/:name") {
    // http://192.168.1.101:8080/parameters/sensor/sensor_1?threshold=100&filterLength=100
    val threshold: Int = params.getOrElse("threshold", "-1").toInt
    val filterLength: Int = params.getOrElse("filterLength", "-1").toInt
    discemoneActor ! SensorDetail(params("name"), threshold, filterLength)
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
  
  put("/pattern/:name") {
    logger.info ("pattern command received:" + params("name") + ":" + params("intensity") + ":" + params("red") + ":" + params("green") + ":" + params("blue") + ":" + params("speed") + ":" + params("modDelay"))
    discemoneActor ! PatternCommand(params("name"), params("intensity").toInt, params("red").toInt, params("green").toInt, params("blue").toInt, params("speed").toInt, params("modDelay").toInt)
  }
  
  put("/time") {
    logger.info ("time command received: " + params("seconds"))
    discemoneActor ! SetTime(params("seconds").toInt)
  }
  
}
