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
  case class TimeSeriesRequestCPU
  case class TimeSeriesRequestMemory
  case class TimeSeriesRequestBattery
  case class ListRequestSensor
  case class ListRequestMember
  case class MemberCount
  case class ThresholdValue(newValue: Int)
  case class MetricHistory(history: List[Double])
  case class MetricValue(value: Double)
  case class SensorActivityLevel(id: String)
  case class SensorDetail(name: String, threshold: Int, filterLength: Int)
  case class SensorList(collection: List[SensorDetail])
  case class MemberDetail(name: String, 
		  				  xbee: String,      // Lower 32 bit XBee address
		  				  pattern: Int,   // Pattern id
		  				  lat: Float,         // Latitude in decimal degrees
		  				  lon: Float,         // Longitude in decimal degrees
		  				  alt: Float,         // Altitude in feet
		  				  battery: Float)      // Battery voltage
  case class MemberList(collection: List[MemberDetail])
  case class PatternCommand(name: String, intensity: Int, red: Int, green: Int, blue: Int, speed: Int, modDelay: Int)
  case class PatternNames
  case class CurrentPattern
  case class SetTime(seconds: Int)
}

class DiscemoneMock extends Actor with ActorLogging {
  import context._
  import DiscemoneMock._
  import akka.util.Timeout
  import scala.concurrent.duration._
  
  implicit val defaultTimeout = Timeout(2000)  

  val mockMembers: Map[String, MemberDetail] = Map(("member_1" -> MemberDetail("member_1", "", 1, 40.123321f, -119.123321f, 1.0f, 6.4f)), 
		  										   ("member_2" -> MemberDetail("member_2", "", 1, 40.123321f, -120.123321f, 1.0f, 7.4f)), 
		  										   ("member_3" -> MemberDetail("member_3", "", 1, 40.123321f, -120.123321f, 1.0f, 7.4f)), 
		  										   ("member_4" -> MemberDetail("member_4", "", 1, 40.123321f, -120.123321f, 1.0f, 7.4f)), 
		  										   ("member_5" -> MemberDetail("member_5", "", 1, 39.123321f, -120.123321f, 1.0f, 7.0f)))
  var mockSensors: Map[String, SensorDetail] = Map(("sensor_1" -> SensorDetail("sensor_1", 100, 100)), 
		  										   ("sensor_2" -> SensorDetail("sensor_2", 150, 150)), 
		  										   ("sensor_3" -> SensorDetail("sensor_3", 200, 200)))
  val logger =  LoggerFactory.getLogger(getClass)
  
  def receive = {
    case TimeSeriesRequestCPU => {
      sender ! MetricHistory(List(1.0, 2.0, 3.0, 4.0))
      logger.info ("CPU_TIME_SERIES_REQUEST request delivered")
    } 
    case TimeSeriesRequestMemory => {
      sender ! MetricHistory(List(4.0, 3.0, 2.0, 1.0))
      logger.info ("MEM_TIME_SERIES_REQUEST request delivered")
    } 
    case TimeSeriesRequestBattery => {
      sender ! MetricHistory(List(4.0, 4.0, 1.0, 1.0))
      logger.info ("BAT_TIME_SERIES_REQUEST request delivered")
    } 
    case ListRequestSensor => {
      sender ! SensorList(mockSensors.values.toList)
      logger.info ("SensorList request for delivered")            
    }
    case SensorActivityLevel(name) => {
      sender ! MetricHistory(List(1.0, 1.0, 1.0, 1.0))
      logger.info ("SensorActivityLevel request for " + name + " delivered")      
    }
    case "MEMBER_COUNT" => {
      sender ! MetricValue(5.0)
      logger.info ("MemberCount request delivered")      
    }
    case ListRequestMember => {
      sender ! MemberList(mockMembers.values.toList)
      logger.info ("MemberList request delivered")      
    }
    case MemberDetail(name, "", 0, 0, 0, 0, 0) => {
      if (mockMembers.contains(name)) sender ! mockMembers(name)
      else sender ! MemberDetail("unknown", "", 1, 40.0f, -119.0f, 1.0f, 6.4f)
      logger.info ("Member request delivered")
    }
    case PatternNames => {
      sender ! Map ("1" -> "Pattern 1", "2" -> "Pattern 2", "3" -> "Pattern 3")
      logger.info ("Pattern names request delivered")
    }
    case CurrentPattern => {
      sender ! PatternCommand ("34", 120, 0, 255, 0, 255, 0)
      logger.info ("Current pattern request delivered")
    }
    // Put commands
    case SensorDetail(name, threshold, filterLength) => {
      mockSensors += (name -> SensorDetail(name, threshold, filterLength))
      logger.info ("Sensor command processed")      
    }
    case PatternCommand => {
      sender ! "OK"
      logger.info ("Pattern command processed")      
    }
    case SetTime => {
      logger.info ("Set time command processed")            
    }
    case _ => {
      logger.info ("Received Unknown message")
    }
  }
}