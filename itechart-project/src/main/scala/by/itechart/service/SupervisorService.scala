package by.itechart.service

import akka.actor.ActorRef
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import by.itechart.action.{InitLoadState, InitNormalizationStateByKeys, _}
import by.itechart.constant.Constant
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import javax.ws.rs.{Consumes, POST, Path}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val creationNewFlow = jsonFormat0(CreateNewFlow)
  implicit val initializationStartState = jsonFormat1(InitStartState)
  implicit val initializationRetrievalState = jsonFormat1(InitRetrievalState)
  implicit val initializationTransformationState = jsonFormat1(InitTransformationState)
  implicit val initializationNormalizationState = jsonFormat1(InitNormalizationState)
  implicit val initializationValidationState = jsonFormat1(InitValidationState)
  implicit val initializationLoadState = jsonFormat1(InitLoadState)
  implicit val initializationFinishState = jsonFormat1(InitFinishState)
  implicit val initializationTransformationStateByKeys = jsonFormat4(InitTransformationStateByKeys)
  implicit val initializationNormalizationStateByKeys = jsonFormat4(InitNormalizationStateByKeys)
}

class SupervisorService(supervisor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with JsonSupport {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  val route =
    createFlow ~
      pathPrefix("flows" / Segment) { flowId =>
        pathPrefix("states") {
          initStartState(flowId) ~ initRetrievalState(flowId) ~ initTransformationState(flowId) ~
            initNormalizationState(flowId) ~ initValidationState(flowId) ~ initLoadState(flowId) ~
            initFinishState(flowId)
        }
      }

  @POST
  @Consumes(Array("application/json"))
  @Path("/flows")
  def createFlow =
    pathPrefix("flows") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? CreateNewFlow()).mapTo[Notice].map {
            case _: EmptyFolder => HttpResponse(StatusCodes.NotFound)
            case notice: NotEmptyFolder =>
              notice.results.collect { case value: FailureRequest => value }.isEmpty match {
                case true => HttpResponse(StatusCodes.OK)
                case false => HttpResponse(StatusCodes.Conflict)
              }
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("/flows/{flowId}/states/starting")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true, schema = new Schema(implementation = classOf[String]))
    ),
  )
  def initStartState(flowId: String) =
    pathPrefix("starting") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitStartState(flowId)).map {
            case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
            case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("/flows/{flowId}/states/retrieving")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true, schema = new Schema(implementation = classOf[String]))
    ),
  )
  def initRetrievalState(flowId: String) =
    pathPrefix("retrieving") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitRetrievalState(flowId)).map {
            case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
            case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("/flows/{flowId}/states/transforming")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true, schema = new Schema(implementation = classOf[String])),
      new Parameter(name = "companyName", in = ParameterIn.QUERY, required = false),
      new Parameter(name = "departmentName", in = ParameterIn.QUERY, required = false),
      new Parameter(name = "payDate", in = ParameterIn.QUERY, required = false)
    ),
  )
  def initTransformationState(flowId: String) =
    pathPrefix("transforming") {
      pathEndOrSingleSlash {
        post {
          parameters("companyName".as[String].?, "departmentName".as[String].?, "payDate".as[String].?) { (companyName, departmentName, payDate) ⇒
            val res =
              if (companyName.isDefined && departmentName.isDefined && payDate.isDefined) {
                (supervisor ? InitTransformationStateByKeys(flowId, companyName.get, departmentName.get, payDate.get)).map {
                  case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
                  case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
                }
              } else {
                (supervisor ? InitTransformationState(flowId)).map {
                  case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
                  case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
                }
              }
            complete(res)
          }
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("/flows/{flowId}/states/normalizing")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true, schema = new Schema(implementation = classOf[String])),
      new Parameter(name = "companyName", in = ParameterIn.QUERY, required = false),
      new Parameter(name = "departmentName", in = ParameterIn.QUERY, required = false),
      new Parameter(name = "payDate", in = ParameterIn.QUERY, required = false)
    ),
  )
  def initNormalizationState(flowId: String) =
    pathPrefix("normalizing") {
      pathEndOrSingleSlash {
        post {
          parameters("companyName".as[String].?, "departmentName".as[String].?, "payDate".as[String].?) { (companyName, departmentName, payDate) ⇒
            val res =
              if (companyName.isDefined && departmentName.isDefined && payDate.isDefined) {
                (supervisor ? InitNormalizationStateByKeys(flowId, companyName.get, departmentName.get, payDate.get)).map {
                  case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
                  case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
                }
              } else {
                (supervisor ? InitNormalizationState(flowId)).map {
                  case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
                  case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
                }
              }
            complete(res)
          }
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("/flows/{flowId}/states/validating")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true, schema = new Schema(implementation = classOf[String]))
    ),
  )
  def initValidationState(flowId: String) =
    pathPrefix("validating") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitValidationState(flowId)).map {
            case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
            case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("/flows/{flowId}/states/loading")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true, schema = new Schema(implementation = classOf[String]))
    ),
  )
  def initLoadState(flowId: String) =
    pathPrefix("loading") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitLoadState(flowId)).map {
            case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
            case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
          }
          complete(res)
        }
      }
    }

  @POST
  @Consumes(Array("application/json"))
  @Path("/flows/{flowId}/states/finishing")
  @Operation(
    parameters = Array(
      new Parameter(name = "flowId", in = ParameterIn.PATH, required = true, schema = new Schema(implementation = classOf[String]))
    ),
  )
  def initFinishState(flowId: String) =
    pathPrefix("finishing") {
      pathEndOrSingleSlash {
        post {
          val res = (supervisor ? InitFinishState(flowId)).map {
            case _: SuccessfulRequest => HttpResponse(StatusCodes.OK)
            case _: FailureRequest => HttpResponse(StatusCodes.NotFound)
          }
          complete(res)
        }
      }
    }
}

