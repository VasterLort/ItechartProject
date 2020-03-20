package by.itechart.service

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import by.itechart.action.{InitLoadState, _}
import by.itechart.enums.StateId
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import javax.ws.rs.{Consumes, POST, Path}
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val initializationStartState = jsonFormat1(InitStartState)
  implicit val initializationRetrievalState = jsonFormat1(InitRetrievalState)
  implicit val initializationTransformationState = jsonFormat1(InitTransformationState)
  implicit val initializationNormalizationState = jsonFormat1(InitNormalizationState)
  implicit val initializationValidationState = jsonFormat1(InitValidationState)
  implicit val initializationLoadState = jsonFormat1(InitLoadState)
}

class SupervisorService(supervisor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with JsonSupport {
  implicit val timeout = Timeout(10.seconds)

  val route = initFlow

  @POST
  @Consumes(Array("application/json"))
  @Path("flows/{flowId}/states/{stateId}")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
      new Parameter(name = "stateId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[Int]))
    ),
  )
  def initFlow =
    pathPrefix("flows" / Segment) { flowId =>
      pathPrefix("states" / Segment) { stateId =>
        pathEndOrSingleSlash {
          post {
            val res: Future[Notice] = stateId.toInt match {
              case id if id == StateId.startId.id => (supervisor ? InitStartState(flowId)).mapTo[Notice]
              case id if id == StateId.retrievalId.id => (supervisor ? InitRetrievalState(flowId)).mapTo[Notice]
              case id if id == StateId.transformationId.id => (supervisor ? InitTransformationState(flowId)).mapTo[Notice]
              case id if id == StateId.normalizationId.id => (supervisor ? InitNormalizationState(flowId)).mapTo[Notice]
              case id if id == StateId.validationId.id => (supervisor ? InitValidationState(flowId)).mapTo[Notice]
              case id if id == StateId.loadId.id => (supervisor ? InitLoadState(flowId)).mapTo[Notice]
              case _ => Future.successful(FailureNotice()).mapTo[Notice]
            }
            complete(res.map {
              case _: SuccessfulNotice => HttpResponse(StatusCodes.OK)
              case _: FailureNotice => HttpResponse(StatusCodes.BadRequest)
            })
          }
        }
      }
    }
}

