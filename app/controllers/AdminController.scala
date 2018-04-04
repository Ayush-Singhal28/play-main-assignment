package controllers

import javax.inject.Inject

import jdk.nashorn.internal.ir.Assignment
import models.{AssignmentRepository, UserProfileRepository}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, _}


import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class AdminController @Inject()(assignmentRepository: AssignmentRepository,
                                userProfileRepository: UserProfileRepository,
                                cc: ControllerComponents)
  extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi


  /**
    * Redirect to admin page
    */
  def admin(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.admin())
  }

  /**
    * Display list Of User
    * on display page
    */
  def viewListOfUser(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val list = Await.result(userProfileRepository.listOfUser(), Duration.Inf)
    Future.successful(Ok(views.html.listOfUserDisplay(list)))
  }

}
