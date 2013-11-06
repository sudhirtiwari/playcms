import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin._
import playcms.cache._
import playcms.repository._
import playcms.services._
import playcms.services.events.StompEventBus

package object playcms {
  val config = current.configuration
  implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
  implicit val actorRefFactory = play.api.libs.concurrent.Akka.system

  val PageRepository = new MongoPageRepository(db)
  val SiteRepository = new MongoSiteRepository(db)
  val TemplateRepository = new MongoTemplateRepository(db)

  val PageCache = new PageCache
  val SiteCache = new SiteCache
  val TemplateCache = new TemplateCache

  val EventBus = new StompEventBus(config.getString("stomp.broker.url").getOrElse("tcp://localhost:61613"))
  val PageService = new PageService(PageRepository, PageCache, EventBus)
  val SiteService = new SiteService(SiteRepository, SiteCache, EventBus, PageService)
  val TemplateService = new TemplateService(TemplateRepository, TemplateCache, EventBus)
}
