package playcms.events

import akka.actor.{PoisonPill, ActorRef, ActorRefFactory}
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

trait IEventSubscription {
  def unsubscribe(): Unit
}

case class EventSubscription(ref: ActorRef) extends IEventSubscription {
  def unsubscribe() = ref ! PoisonPill
}

trait IEventBus {
  type OnReceive[A <: CmsEvent] = PartialFunction[A, Unit]
  def publish[A <: CmsEvent](event: A)(implicit writes: Writes[A#Body])
  def subscribe[A <: CmsEvent](companion: CmsEventCompanion[A])(receive: OnReceive[A]): IEventSubscription
}

class StompEventBus(brokerUrl: String)(implicit factory: ActorRefFactory) extends IEventBus {
  def publish[A <: CmsEvent](event: A)(implicit writes: Writes[A#Body]) {
    EventProducer(event.companion.destination, brokerUrl) ! CamelMessage(
      body = Json.stringify(Json.toJson(event.body)),
      headers = Map("content-type" -> "application/json"))
  }

  def subscribe[A <: CmsEvent](companion: CmsEventCompanion[A])(receive: OnReceive[A]) = {
    implicit val reader = companion.reads
    val ref = EventSubscriber(companion.destination, brokerUrl)(receive)
    EventSubscription(ref)
  }
}
