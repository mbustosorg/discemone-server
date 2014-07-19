package org.bustos.discemoneServer

import org.scalatra.test.specs2._

// -- New for discemone
import _root_.akka.actor.{ActorSystem, Props}
import org.bustos.discemone._
// --

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class DiscemoneServerServletSpec extends MutableScalatraSpec {

  // -- New for discemone
  val system = ActorSystem("discemone")
  val a = system.actorOf(Props[DiscemoneMock], "Discemone")    

  addServlet(new DiscemoneServerServlet(system, a), "/*")
  // --
   
  "GET / on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/") {
        status must_== 200
      }
    }
  }

  "GET /metrics/cpu on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/metrics/cpu") {
        status must_== 200
      }
    }
  }

  "GET /metrics/battery on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/metrics/battery") {
        status must_== 200
      }
    }
  }

  "GET /metrics/memory on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/metrics/memory") {
        status must_== 200
      }
    }
  }

  "GET /metrics/sensors/sensor_1/activityLevel on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/metrics/sensors/1/activityLevel") {
        status must_== 200
      }
    }
  }

  "GET /metrics/sensors on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/metrics/sensors") {
        status must_== 200
      }
    }
  }

  "GET /memberCount on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/memberCount") {
        status must_== 200
      }
    }
  }

  "GET /members on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/members") {
        status must_== 200
      }
    }
  }

  "GET /members/member_1 on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/members/member_1") {
        status must_== 200
      }
    }
  }
/*
  "PUT /parameters/sensor/sensor_1?threshold=100&filterLength=100 on DiscemoneServerServlet" should {
    "return status 200" in {
      get("parameters/sensor/sensor_1?threshold=100&filterLength=100") {
        status must_== 200
      }
    }
  }
  
  "PUT /shutdown on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/shutdown") {
        status must_== 200
      }
    }
  }

  "PUT /startup on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/startup") {
        status must_== 200
      }
    }
  }

  "PUT /restart on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/restart") {
        status must_== 200
      }
    }
  }

  "PUT /pattern/colorWheel?intensity=100&red=100&green=100&blue=100&speed=100 on DiscemoneServerServlet" should {
    "return status 200" in {
      get("/pattern/colorWheel?intensity=100&red=100&green=100&blue=100&speed=100") {
        status must_== 200
      }
    }
  }
*/
}
