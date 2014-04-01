import org.bustos.discemoneServer._
import org.scalatra._
import javax.servlet.ServletContext

// -- New for discemone
import _root_.akka.actor.{ActorSystem, Props}
import org.bustos.discemone._
import org.slf4j.{Logger, LoggerFactory}
// --

class ScalatraBootstrap extends LifeCycle {

  // -- New for discemone
  val system = ActorSystem("discemone")
  val a = system.actorOf(Props[Discemone], "Discemone")    
  val logger = LoggerFactory.getLogger(getClass)
  // --
  
  override def init(context: ServletContext) {
    // Added for argument for Actor access
    logger.info("Starting ScalatraBootstrap with actor setup")
    context.mount(new DiscemoneServerServlet(system, a), "/*")
  }

  // Make sure you shut down
  override def destroy(context:ServletContext) {
    system.shutdown()
  }
}
