package by.itechart.service

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import by.itechart.action.{InitLoadState, _}
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import javax.ws.rs.{Consumes, POST, Path}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val initializationStartState = jsonFormat1(InitStartState)
  implicit val initializationRetrievalState = jsonFormat1(InitRetrievalState)
  implicit val initializationTransformationState = jsonFormat1(InitTransformationState)
  implicit val initializationNormalizationState = jsonFormat1(InitNormalizationState)
  implicit val initializationValidationState = jsonFormat1(InitValidationState)
  implicit val initializationLoadState = jsonFormat1(InitLoadState)
}

@Path("/flows/{flowId}/states")
class SupervisorService(supervisor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with JsonSupport {
  implicit val timeout = Timeout(10.seconds)

  val route =
    pathPrefix("flows" / Segment) { flowId =>
      pathPrefix("states") {
        initStartState(flowId) ~ initRetrievalState(flowId) ~ initTransformationState(flowId) ~
          initNormalizationState(flowId) ~ initValidationState(flowId) ~ initLoadState(flowId)
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("starting")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initStartState(flowId: String) =
    pathPrefix("starting") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitStartState(flowId)).map {
            case _: SuccessfulNotice => HttpResponse(StatusCodes.OK)
            case _: FailureNotice => HttpResponse(StatusCodes.BadRequest)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("retrieving")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initRetrievalState(flowId: String) =
    pathPrefix("retrieving") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitRetrievalState(flowId)).map {
            case _: SuccessfulNotice => HttpResponse(StatusCodes.OK)
            case _: FailureNotice => HttpResponse(StatusCodes.BadRequest)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("transforming")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initTransformationState(flowId: String) =
    pathPrefix("transforming") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitTransformationState(flowId)).map {
            case _: SuccessfulNotice => HttpResponse(StatusCodes.OK)
            case _: FailureNotice => HttpResponse(StatusCodes.BadRequest)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("normalizing")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initNormalizationState(flowId: String) =
    pathPrefix("normalizing") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitNormalizationState(flowId)).map {
            case _: SuccessfulNotice => HttpResponse(StatusCodes.OK)
            case _: FailureNotice => HttpResponse(StatusCodes.BadRequest)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("validating")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initValidationState(flowId: String) =
    pathPrefix("validating") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitValidationState(flowId)).map {
            case _: SuccessfulNotice => HttpResponse(StatusCodes.OK)
            case _: FailureNotice => HttpResponse(StatusCodes.BadRequest)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("loading")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initLoadState(flowId: String) =
    pathPrefix("loading") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitLoadState(flowId)).map {
            case _: SuccessfulNotice => HttpResponse(StatusCodes.OK)
            case _: FailureNotice => HttpResponse(StatusCodes.BadRequest)
          }
          complete(res)
        }
      }
    }
}

