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
  case class CollectCPUtimeSeries
  case class CollectBatteryTimeSeries
  case class CollectMemoryTimeSeries
  case class MemberCount
  case class ThresholdValue(newValue: Int)
}

class DiscemoneMock extends Actor with ActorLogging {
  import context._
  import DiscemoneMock._
  import akka.util.Timeout
  import scala.concurrent.duration._
  
  val logger =  LoggerFactory.getLogger(getClass)
  
  def receive = {
     case CollectCPUtimeSeries => {
      sender ! "test {123}"
      logger.debug ("CollectCPUtimeSeries request delivered")
    } 
    case CollectMemoryTimeSeries => {
      sender ! "test {321}"
      logger.debug ("CollectMemoryTimeSeries request delivered")
    } 
    case MemberCount => {
      sender ! "test {1}"
      logger.debug ("MemberCount request delivered")      
    }
    case "Count" => {
      sender ! "Got that"
      logger.debug ("Count request delivered")
    }
    case _ => {
      logger.debug ("Received Unknown message")
    }
  }
}