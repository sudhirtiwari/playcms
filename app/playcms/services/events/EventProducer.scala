package playcms.services.events

import akka.actor.{Props, ActorRef, ActorRefFactory}
import akka.camel.{Oneway, Producer}
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConversions._

class EventProducer(endpoint: String, brokerUrl: String) extends Producer with Oneway {
  def endpointUri: String = s"$endpoint?brokerURL=$brokerUrl"
}

object EventProducer {
  private val eventProducers = new ConcurrentHashMap[String, ActorRef]()

  def apply(endpoint: String, brokerUrl: String)(implicit factory: ActorRefFactory): ActorRef =
    eventProducers.getOrElseUpdate(endpoint, factory.actorOf(Props(classOf[EventProducer], endpoint, brokerUrl)))
}
