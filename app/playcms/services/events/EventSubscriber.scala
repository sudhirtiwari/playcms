package playcms.services.events

import akka.actor.{ActorRef, Props, ActorRefFactory}
import akka.camel.{CamelMessage, Consumer}
import play.api.libs.json.{Reads, Json}
import playcms.util.Logging

class EventSubscriber[A <: CmsEvent](val endpointUri: String, reads: Reads[A], onReceive: IEventBus#OnReceive[A])
  extends Consumer with Logging {

  def receive = {
    case CamelMessage(body, headers) =>
      Json.fromJson(Json.parse(body.toString))(reads).asEither.fold({ errors =>
        errors foreach {
          case (path, validations) =>
            validations.foreach { err =>
              warn(s"Validation exception at path: ${path.toJsonString}\r\n${err.message.format(err.args: _*)}")
            }
        }
      }, onReceive)
  }
}

object EventSubscriber {
  def apply[A <: CmsEvent](destination: String, brokerUrl: String)
                          (onReceive: IEventBus#OnReceive[A])
                          (implicit reads: Reads[A], factory: ActorRefFactory): ActorRef =
    factory.actorOf(Props(classOf[EventSubscriber[A]], s"$destination?brokerURL=$brokerUrl", reads, onReceive))
}
