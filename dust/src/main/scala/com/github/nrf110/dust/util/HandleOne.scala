package com.github.nrf110.dust.util

import akka.actor.Actor.Receive
import akka.actor.ActorDSL._
import akka.actor.{ActorRef, ActorRefFactory}

object HandleOne {
  def apply(handle: Receive)(implicit actorRefFactory: ActorRefFactory): ActorRef =
    actor(new Act {
      become(handle andThen { _ =>
        context.stop(self) })})
}
