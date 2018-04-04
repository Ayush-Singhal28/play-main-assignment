package controllers

import javax.inject.Inject

import models.{UserInfo, _}
import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class ProfileController @Inject()(userProfileRepository: UserProfileRepository, cc: ControllerComponents)
  extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi


  val userChangingPasswordForm: Form[UserChangingPassword] = Form(
    mapping(
      "email" -> text,
      "password" -> text,
      "confirmPassword" -> text,

    )

    (UserChangingPassword.apply)(UserChangingPassword.unapply)
      .verifying("Reconfirm Password", UserInfo => UserInfo.password == UserInfo.confirmPassword)
  )

  val userSignInForm: Form[UserSignIn] = Form(
    mapping(
      "email" -> text,
      "password" -> text,
    )

    (UserSignIn.apply)(UserSignIn.unapply)
  )

  val userProfileForm: Form[UserProfileForm] = Form(
    mapping(
      "fname" -> text,
      "mname" -> text,
      "lname" -> text,
      "mobile" -> text,
      "age" -> number,
      "hobbies" -> text
    )

    (UserProfileForm.apply)(UserProfileForm.unapply)
  )

  /**
    * Redirect to signIn page
    */
  def signIn(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signIn(userSignInForm))
  }

  /**
    * Redirect to profile page.
    */
  def dataDisplay(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.profile())
  }

  /**
    * User sign in and redirect
    * to profile page
    */
  def viewProfile(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    userSignInForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.signIn(formWithErrors)))
      },
      userData => {
        userProfileRepository.doSignIn(userData.email, userData.password).flatMap {
          case true => userProfileRepository.haveAuthToSignIn(userData.email).map {
            case true => Redirect(routes.ProfileController.dataDisplay()).withSession("email" -> userData.email).flashing("success" -> "user successfully sign in")
            case false => Redirect(routes.ProfileController.signIn()).flashing("failed" -> "take permission from admin")
          }
          case false => Future.successful(Redirect(routes.ProfileController.signIn()).flashing("incorrect" -> "check email and password"))
        }
      })
  }


  /**
    * Redirect to forgot password page.
    * If user forgot his password.
    */
  def forgotPassword(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.forgotPassword(userSignInForm))
  }

  /**
    * Store the updated password.
    */
  def storeUpdatedPassword(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    userSignInForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.forgotPassword(formWithErrors)))
      },
      userData => {

        userProfileRepository.updatePassword(userData.email, userData.password).map {
          case true => Redirect(routes.HomeController.index()).withSession("email" -> userData.email)
            .flashing("success" -> "password updated successfully")
          case false => Ok(views.html.forgotPassword(userSignInForm))
        }
      })
  }

  /**
    * User get profile information
    */
  def getUserInformation(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val email = request.session.get("email").get
    val userDetails: Future[UserData] = userProfileRepository.getInformation(email)
    userDetails.map {
      data =>
        val profile = UserProfileForm(data.fname, data.mname, data.lname,
          data.mobile, data.age, data.hobbies)
        Ok(views.html.viewProfile(userProfileForm.fill(profile)))
    }
  }

  /**
    * User get profile information
    * and update Profile Form.
    */
  def updateProfile(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userProfileForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.viewProfile(formWithErrors)))
      },
      profile => {
        val updatedDetails = UserProfileForm(profile.fname, profile.mname, profile.lname,
          profile.mobile, profile.age, profile.hobbies)
        val userUpdated = userProfileRepository.updateDetails(request.session.get("email").get, updatedDetails)
        userUpdated.map {
          case true =>
            Redirect(routes.ProfileController.getUserInformation())
          case false => InternalServerError("user details could not be updated")
        }
      })
  }


}
