import org.bustos.discemoneServer._
import org.scalatra._
import javax.servlet.ServletContext
import _root_.akka.actor.{ActorSystem, Props}
import _root_.akka.routing.SmallestMailboxRouter
// *** Comment out for Heroku deployment 
//import org.bustos.discemone._
import org.slf4j.LoggerFactory

/** Bootstrap class for scalatra servlet
 * 
 * Sets up actor system to communicate with discemone
 */
class ScalatraBootstrap extends LifeCycle {

  val system = ActorSystem("discemone")
  val discemoneActor = system.actorOf(Props[DiscemoneMock], "DiscemoneActor")    
  // *** Comment out for Heroku deployment 
  //val discemoneActor = system.actorOf(Props[Discemone], "DiscemoneActor")    
  val logger = LoggerFactory.getLogger(getClass)
  
  override def init(context: ServletContext) {
    logger.info("Starting Discemone ScalatraBootstrap with actor setup")
    context.initParameters("org.scalatra.cors.allowedMethods") = "GET"
    context.mount(new DiscemoneServerServlet(system, discemoneActor), "/*")
  }

  override def destroy(context:ServletContext) {
    system.shutdown()
  }
}
