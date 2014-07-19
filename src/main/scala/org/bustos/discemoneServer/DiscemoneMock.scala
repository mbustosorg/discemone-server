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
  case class Member(name: String)
  case class MemberList(collection: List[Member])
  case class PatternCommand(name: String, intensity: Int, red: Int, green: Int, blue: Int, speed: Int)
}

class DiscemoneMock extends Actor with ActorLogging {
  import context._
  import DiscemoneMock._
  import akka.util.Timeout
  import scala.concurrent.duration._
  
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
      sender ! SensorList(List(Sensor("sensor_1", 100, 100)))
      logger.info ("SensorList request for delivered")            
    }
    case "MEMBER_COUNT" => {
      sender ! MetricValue(5.0)
      logger.info ("MemberCount request delivered")      
    }
    case "MEMBER_LIST_REQUEST" => {
      sender ! MemberList(List(Member("member_1")))
      logger.info ("MemberList request delivered")      
    }
    case Member(name) => {
      sender ! Member(name)
      logger.info ("Member request delivered")
    }
    // Put commands
    case Sensor(name, threshold, filterLength) => {
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