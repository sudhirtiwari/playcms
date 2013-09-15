package playcms.services

import play.api.Play.current
import play.api.libs.iteratee.Iteratee
import play.modules.reactivemongo._
import reactivemongo.api.gridfs._
import reactivemongo.bson._
import com.github.jknack.handlebars.TemplateLoader
import concurrent.{Await, Future}
import concurrent.duration._
import concurrent.ExecutionContext.Implicits.global
import java.io.{ByteArrayInputStream, InputStreamReader, FileNotFoundException}

//class MongoTemplateLoader
//  extends TemplateLoader {
//
//  setPrefix("")
//  setSuffix("")
//
//  val fold = Iteratee.fold[Array[Byte], Array[Byte]](Array[Byte]())((a,b) => a ++ b)
//
//  def read(location: String) = {
//    val gridFs = new GridFS(ReactiveMongoPlugin.db, "cms_templates")
//    val cursor = gridFs.find(BSONDocument("filename" -> BSONString(location)))
//
//    val futureReader = for {
//      fileReader <- cursor.headOption().filter(_.isDefined)
//      bytes <- fileReader match {
//        case Some(file) => gridFs.enumerate(file).run(fold)
//        case None => throw new FileNotFoundException(s"Could not find template at location $location")
//      }
//    } yield {
//      val inputStream = new ByteArrayInputStream(bytes)
//      new InputStreamReader(inputStream, "UTF-8")
//    }
//
//    Await.result(futureReader, 10 seconds)
//  }
//}
