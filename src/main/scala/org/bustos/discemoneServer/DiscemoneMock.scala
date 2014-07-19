package org.bustos.discemoneServer

import akka.actor.{ActorSystem, ActorRef, Actor, Props}
import akka.actor.ActorLogging
import akka.pattern.ask

import scala.concurrent.Await

import org.slf4j.{Logger, LoggerFactory}

/** Mock Data Source for the Discemone Server
 *  
 * The pod sensors and XBee access are managed by this object.
 * This also provides system summary access for rendering
 * by external viewers.  
 */

object DiscemoneMock {
  case class MemberCount
  case class ThresholdValue(newValue: Int)
  case class MetricHistory(history: List[Double])
  case class MetricValue(value: Double)
  case class SensorActivityLevel(id: String)
  case class Sensor(name: String, threshold: Int, filterLength: Int)
  case class SensorList(collection: List[Sensor])
  case class Member(name: String, 
		  			xbee: Int,      // Lower 32 bit XBee address
		  			pattern: Int,   // Pattern id
		  			lat: Float,         // Latitude in decimal degrees
		  			lon: Float,         // Longitude in decimal degrees
		  			alt: Float,         // Altitude in feet
		  			battery: Float)      // Battery voltage

  case class MemberList(collection: List[Member])
  case class PatternCommand(name: String, intensity: Int, red: Int, green: Int, blue: Int, speed: Int)
}

class DiscemoneMock extends Actor with ActorLogging {
  import context._
  import DiscemoneMock._
  import akka.util.Timeout
  import scala.concurrent.duration._
  
  val mockMembers: Map[String, Member] = Map(("member_1" -> Member("member_1", 1, 1, 40.0f, -119.0f, 1.0f, 6.4f)), ("member_2" -> Member("member_2", 1, 1, 40.0f, -120.0f, 1.0f, 7.4f)), ("member_3" -> Member("member_3", 1, 1, 39.0f, -120.0f, 1.0f, 7.0f)))
  var mockSensors: Map[String, Sensor] = Map(("sensor_1" -> Sensor("sensor_1", 100, 100)), ("sensor_2" -> Sensor("sensor_2", 200, 200)), ("sensor_3" -> Sensor("sensor_3", 300, 300)))
  val logger =  LoggerFactory.getLogger(getClass)
  
  def receive = {
    case "CPU_TIME_SERIES_REQUEST" => {
      sender ! MetricHistory(List(1.0, 2.0, 3.0, 4.0))
      logger.info ("CPU_TIME_SERIES_REQUEST request delivered")
    } 
    case "MEM_TIME_SERIES_REQUEST" => {
      sender ! MetricHistory(List(4.0, 3.0, 2.0, 1.0))
      logger.info ("MEM_TIME_SERIES_REQUEST request delivered")
    } 
    case "BAT_TIME_SERIES_REQUEST" => {
      sender ! MetricHistory(List(4.0, 4.0, 1.0, 1.0))
      logger.info ("BAT_TIME_SERIES_REQUEST request delivered")
    } 
    case SensorActivityLevel(name) => {
      sender ! MetricHistory(List(1.0, 1.0, 1.0, 1.0))
      logger.info ("SensorActivityLevel request for " + name + " delivered")      
    }
    case "SENSOR_LIST_REQUEST" => {
      sender ! SensorList(mockSensors.values.toList)
      logger.info ("SensorList request for delivered")            
    }
    case "MEMBER_COUNT" => {
      sender ! MetricValue(5.0)
      logger.info ("MemberCount request delivered")      
    }
    case "MEMBER_LIST_REQUEST" => {
      sender ! MemberList(mockMembers.values.toList)
      logger.info ("MemberList request delivered")      
    }
    case Member(name, 0, 0, 0, 0, 0, 0) => {
      if (mockMembers.contains(name)) sender ! mockMembers(name)
      else sender ! Member("unknown", 1, 1, 40.0f, -119.0f, 1.0f, 6.4f)
      logger.info ("Member request delivered")
    }
    // Put commands
    case Sensor(name, threshold, filterLength) => {
      mockSensors += (name -> Sensor(name, threshold, filterLength))
      logger.info ("Sensor command processed")      
    }
    case PatternCommand(name, intensity, red, green, blue, speed) => {
      sender ! "OK"
      logger.info ("Pattern command processed")      
    }
    case _ => {
      logger.info ("Received Unknown message")
    }
  }
}