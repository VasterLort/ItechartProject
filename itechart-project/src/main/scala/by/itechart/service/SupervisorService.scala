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

@Path("/supervisor")
class SupervisorService(supervisor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with JsonSupport {
  implicit val timeout = Timeout(10.seconds)

  val route =
    pathPrefix("supervisor") {
      initStartState ~ initRetrievalState ~ initTransformationState ~
        initNormalizationState ~ initValidationState ~ initLoadState
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("start/{flowId}")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initStartState =
    pathPrefix("start" / Segment) { flowId =>
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
  @Path("retrieval/{flowId}")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initRetrievalState =
    pathPrefix("retrieval" / Segment) { flowId =>
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
  @Path("transformation/{flowId}")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initTransformationState =
    pathPrefix("transformation" / Segment) { flowId =>
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
  @Path("normalization/{flowId}")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initNormalizationState =
    pathPrefix("normalization" / Segment) { flowId =>
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
  @Path("validation/{flowId}")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initValidationState =
    pathPrefix("validation" / Segment) { flowId =>
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
  @Path("load/{flowId}")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true,
        schema = new Schema(implementation = classOf[String])),
    ),
  )
  def initLoadState =
    pathPrefix("load" / Segment) { flowId =>
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

