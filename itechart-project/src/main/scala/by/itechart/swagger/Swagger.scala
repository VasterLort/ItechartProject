package by.itechart.swagger

import akka.http.scaladsl.server.Route
import by.itechart.service.SupervisorService
import com.github.swagger.akka.SwaggerHttpService

object Swagger extends SwaggerHttpService {
  override val apiClasses = Set(classOf[SupervisorService])
  override val host = "localhost:8080"

  override def routes: Route = super.routes ~ get {
    pathPrefix("") {
      pathEndOrSingleSlash {
        getFromResource("swagger-ui/index.html")
      }
    } ~
      getFromResourceDirectory("swagger-ui")
  }
}
