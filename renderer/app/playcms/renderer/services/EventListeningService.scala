package playcms.renderer.services

import playcms.util.IDisposable
import playcms.events.IEventSubscription

trait EventListeningService extends IDisposable {
  protected var subscriptions: List[IEventSubscription] = Nil

  def dispose(): Unit = {
    subscriptions foreach (_.unsubscribe())
    subscriptions = Nil
  }
}
