package org.bustos.discemoneServer

import org.scalatra.test.specs2._

// -- New for discemone
import _root_.akka.actor.{ActorSystem, Props}
import org.bustos.discemone._
// --

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class DiscemoneServerServletSpec extends ScalatraSpec { def is =
  "GET / on scalatraTestServlet"                     ^
    "should return status 200"                  ! root200^
                                                end

  // -- New for discemone
  val system = ActorSystem("discemone")
  val a = system.actorOf(Props[Discemone], "Discemone")

  addServlet(new DiscemoneServerServlet(system, a), "/*")
  // --
  
  def root200 = get("/") {
    status must_== 200
  }
}
