package controllers

import javax.inject.Inject

import models.{AssignmentData, AssignmentRepository}
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class AssignmentController @Inject()(assignmentRepository: AssignmentRepository,cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi

  val assignmentData = Form(
    mapping(
      "id" -> number,
      "title" -> nonEmptyText,
      "description" -> nonEmptyText
    )(AssignmentData.apply)(AssignmentData.unapply)
  )

  /**
    * Redirect to addingAssignment page
    */
  def addingAssignment(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.addingAssignment(assignmentData))
  }

  /**
    * store assignment in database
    */
  def storeAssignment(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    assignmentData.bindFromRequest.fold(
     formWithErrors => {
       print("\n-----")
        Future.successful(BadRequest(views.html.addingAssignment(formWithErrors)))
      },
      userData =>
      {
        assignmentRepository.storingAssignment(AssignmentData(0, userData.title, userData.description)).map {
          case true => Redirect(routes.HomeController.index())
            .flashing("success" -> "assignment stored successfully")
          case false => Ok(views.html.index())
        }
      })
  }

  /**
    * Redirect to assignment page
    * and display list of assignment
    */
  def listOfAssignment(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val list = Await.result(assignmentRepository.viewAssignment(), Duration.Inf)
    Future.successful(Ok(views.html.assignment(list)))
  }

  /**
    * admin delete assignment by id
    */
  def deleteAssignmentById(id: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    assignmentRepository.deleteAssignment(id).map {
      case true => Redirect(routes.AssignmentController.listOfAssignment())
      case false => InternalServerError("Failed to delete from database")
    }
  }

}
