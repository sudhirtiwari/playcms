package playcms.services

import com.github.jknack.handlebars.TemplateLoader
import concurrent.Await
import concurrent.duration._
import java.io.{StringReader, FileNotFoundException}

//class PlayCMSTemplateLoader(templateService: ITemplateService, readTimeout: Duration)
//  extends TemplateLoader {
//
//  setPrefix("")
//  setSuffix("")
//
//  def read(name: String) =
//    Await.result(templateService.get(name), 10 seconds) match {
//      case Some(template) => new StringReader(template.templateText)
//      case None           => throw new FileNotFoundException(s"Could not find template: $name")
//    }
//}
