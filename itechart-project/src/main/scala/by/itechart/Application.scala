package by.itechart

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import by.itechart.actor.SupervisorActor
import by.itechart.config.AppConfig
import by.itechart.service.SupervisorService
import by.itechart.swagger.Swagger
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors

import scala.io.StdIn

object Application extends App with RouteConcatenation {
  implicit val system = ActorSystem("actor-system")
  sys.addShutdownHook(system.terminate())

  implicit val executionContext = system.dispatcher

  val supervisor = system.actorOf(Props[SupervisorActor])

  val routes =
    cors()(
      new SupervisorService(supervisor).route ~
        Swagger.routes)

  val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", AppConfig.configValues.port)
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
