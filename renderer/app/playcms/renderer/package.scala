package playcms

import play.api.Play._
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import playcms.cache._
import playcms.events.StompEventBus
import playcms.renderer.services._
import playcms.repository._

package object renderer {
  implicit val pageFormat = models.Page.pageFormat
  implicit val siteFormat = models.Site.siteFormat
  implicit val templateFormat = models.Template.templateFormat
  implicit val routeEntryFormat = models.RouteEntry.routeEntryFormat
  implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
  implicit val actorRefFactory = play.api.libs.concurrent.Akka.system

  val config = current.configuration

  val PageCache = new PageCache
  val RouteEntryCache = new RouteEntryCache
  val SiteCache = new SiteCache
  val TemplateCache = new TemplateCache

  val PageRepository = new MongoPageRepository(db)
  val RouteEntryRepository = new MongoRouteEntryRepository(db)
  val SiteRepository = new MongoSiteRepository(db)
  val TemplateRepository = new MongoTemplateRepository(db)

  val EventBus = new StompEventBus(config.getString("stomp.broker.url").getOrElse("tcp://localhost:61613"))
  val PageService = new PageService(PageCache, PageRepository, EventBus)
  val RouteEntryService = new RouteEntryService(RouteEntryCache, RouteEntryRepository, EventBus)
  val SiteService = new SiteService(SiteCache, EventBus)
  val TemplateService = new TemplateService(TemplateCache, TemplateRepository, EventBus)

  val CmsRouter = new CmsRouter(RouteEntryService, PageService)
}
