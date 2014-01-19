package playcms

import play.api.Play._
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import playcms.repository._
import playcms.admin.services._
import playcms.events.StompEventBus

package object admin {
  implicit val pageFormat = playcms.models.Page.pageFormat
  implicit val siteFormat = playcms.models.Site.siteFormat
  implicit val templateFormat = playcms.models.Template.templateFormat
  implicit val routeEntryFormat = playcms.models.RouteEntry.routeEntryFormat
  implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
  implicit val actorRefFactory = play.api.libs.concurrent.Akka.system

  val config = current.configuration

  val PageRepository = new MongoPageRepository(db)
  val SiteRepository = new MongoSiteRepository(db)
  val TemplateRepository = new MongoTemplateRepository(db)
  val RouteEntryRepository = new MongoRouteEntryRepository(db)

  val EventBus = new StompEventBus(config.getString("stomp.broker.url").getOrElse("tcp://localhost:61613"))
  val PageService = new PageService(PageRepository, EventBus)
  val SiteService = new SiteService(SiteRepository, EventBus, PageService)
  val TemplateService = new TemplateService(TemplateRepository, EventBus)
}
