package org.bustos.discemoneServer

import org.scalatra._
import scalate.ScalateSupport

import _root_.org.bustos.discemone._
import _root_.akka.actor.{ActorRef, Actor, ActorSystem}
import _root_.akka.util.Timeout
import org.scalatra.{Accepted, AsyncResult, FutureSupport, ScalatraServlet}
import scala.concurrent.{ExecutionContext, Future, Promise, Await}

/** Servlet main class for serving Discemone data
 * 
 */
class DiscemoneServerServlet(system: ActorSystem, discemoneActor: ActorRef) extends DiscemoneServerStack with FutureSupport {
  protected implicit def executor: ExecutionContext = system.dispatcher
  import _root_.akka.pattern.ask
  import _root_.akka.actor.Status.{ Success, Failure }
  import scala.concurrent.duration._
  implicit val defaultTimeout = Timeout(100)
  
  get("/") {
    <html>
	  <head>
	    <link rel="stylesheet" type="text/css" href="./css/styles.css" />
	  </head>
      <body>
        <h1>Hello, world!</h1>
        <p>Say <a href="hello-scalate">hello to Scalate</a>.</p>
      </body>
    </html>
  }
  
  get("/hello-scalate") {
    <html>
      <body>
        <h1>Hello, dudes!</h1>
        Say <a href="/">hello to Scalate</a>.
      </body>
    </html>
  }
  
  // -- New for discemone support
  get("/hello-actor") {    
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
  // --
  
}
