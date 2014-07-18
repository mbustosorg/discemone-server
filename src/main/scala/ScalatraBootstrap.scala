import org.bustos.discemoneServer._
import org.scalatra._
import javax.servlet.ServletContext
import _root_.akka.actor.{ActorSystem, Props}
import org.bustos.discemone._
import org.slf4j.LoggerFactory

/** Bootstrap class for scalatra servlet
 * 
 * Sets up actor system to communicate with discemone
 */
class ScalatraBootstrap extends LifeCycle {

  val system = ActorSystem("discemone")
  //val a = system.actorOf(Props[Discemone], "Discemone")    
  val a = system.actorOf(Props[DiscemoneMock], "Discemone")    
  val logger = LoggerFactory.getLogger(getClass)
  
  override def init(context: ServletContext) {
    logger.info("Starting Discemone ScalatraBootstrap with actor setup")
    context.mount(new DiscemoneServerServlet(system, a), "/*")
  }

  override def destroy(context:ServletContext) {
    system.shutdown()
  }
}
