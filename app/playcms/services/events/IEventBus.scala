package playcms.services.events

import akka.actor.ActorRefFactory
import akka.camel.CamelMessage
import play.api.libs.json.{Reads, Writes, Json}

trait CmsEventCompanion[A <: CmsEvent] {
  val destination: String
  val reads: Reads[A]
}

trait CmsEvent {
  type Body
  def companion: CmsEventCompanion[_ <: CmsEvent]
  def body: Body
}

trait IEventBus {
  type OnReceive[A <: CmsEvent] = PartialFunction[A, Unit]
  def publish[A <: CmsEvent](event: A)(implicit writes: Writes[A#Body])
  def subscribe[A <: CmsEvent](companion: CmsEventCompanion[A])(receive: OnReceive[A])
}

class StompEventBus(brokerUrl: String)(implicit factory: ActorRefFactory) extends IEventBus {
  def publish[A <: CmsEvent](event: A)(implicit writes: Writes[A#Body]) {
    EventProducer(event.companion.destination, brokerUrl) ! CamelMessage(
      body = Json.stringify(Json.toJson(event.body)),
      headers = Map("content-type" -> "application/json")
    )
  }

  def subscribe[A <: CmsEvent](companion: CmsEventCompanion[A])(receive: OnReceive[A]) {
    implicit val reader = companion.reads
    EventSubscriber(companion.destination, brokerUrl)(receive)
  }
}
