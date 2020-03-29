package by.itechart.swagger

import akka.http.scaladsl.server.Route
import by.itechart.config.AppConfig
import by.itechart.service.SupervisorService
import com.github.swagger.akka.SwaggerHttpService

object Swagger extends SwaggerHttpService {
  override val apiClasses = Set(classOf[SupervisorService])
  override val host = AppConfig.configValues.swaggerHost

  override def routes: Route = super.routes ~ get {
    pathPrefix(AppConfig.configValues.swaggerPathPrefix) {
      pathEndOrSingleSlash {
        getFromResource(AppConfig.configValues.swaggerResource)
      }
    } ~
      getFromResourceDirectory(AppConfig.configValues.swaggerResourceDirectory)
  }
}
