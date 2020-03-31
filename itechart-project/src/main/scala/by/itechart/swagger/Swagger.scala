package by.itechart.swagger

import akka.http.scaladsl.server.Route
import by.itechart.conf.SwaggerConf
import by.itechart.service.SupervisorService
import com.github.swagger.akka.SwaggerHttpService

object Swagger extends SwaggerHttpService {
  override val apiClasses = Set(classOf[SupervisorService])
  override val host = SwaggerConf.configValues.swaggerHost

  override def routes: Route = super.routes ~ get {
    pathPrefix(SwaggerConf.configValues.swaggerPathPrefix) {
      pathEndOrSingleSlash {
        getFromResource(SwaggerConf.configValues.swaggerResource)
      }
    } ~
      getFromResourceDirectory(SwaggerConf.configValues.swaggerResourceDirectory)
  }
}
