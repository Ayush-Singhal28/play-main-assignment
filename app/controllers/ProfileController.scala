package controllers

import javax.inject.Inject

import models.{UserInfo, _}
import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global




class ProfileController @Inject()(userProfileRepository: UserProfileRepository,cc: ControllerComponents) extends AbstractController(cc) with I18nSupport  {
  implicit val message = cc.messagesApi


  val userChangingPasswordForm: Form[UserChangingPassword] = Form(
    mapping(
      "email" -> text ,
      "password" -> text,
      "confirmPassword" -> text,

    )

    (UserChangingPassword.apply)(UserChangingPassword.unapply).verifying("Reconfirm Password",UserInfo => UserInfo.password == UserInfo.confirmPassword)
  )

  val userSignInForm: Form[UserSignIn] = Form(
    mapping(
      "email" -> text ,
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

  def signIn(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signIn(userSignInForm))
  }

  def dataDisplay(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.profile())
  }

  def viewProfile(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    userSignInForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.signIn(formWithErrors)))
      },
      userData => {
        userProfileRepository.doSignIn(userData.email, userData.password).flatMap{
          case true => userProfileRepository.haveAuthToSignIn(userData.email).map {
            case true =>  Redirect(routes.ProfileController.dataDisplay()).withSession("email" -> userData.email).flashing("success" -> "user successfully sign in")
            case false => Redirect(routes.ProfileController.signIn()).flashing("failed" -> "take permission from admin")
          }
          case false => Future.successful(Redirect(routes.ProfileController.signIn()).flashing("incorrect" ->"check email and password"))
        }})
  }


  /*def updateProfile() = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.profile())
  }*/

  def forgotPassword(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.forgotPassword(userSignInForm))
  }


  def storeUpdatedPassword(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    userSignInForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.forgotPassword(formWithErrors)))
      },
      userData => {
        val newUser = UserSignIn(
          userData.email,
          userData.password

        )
        userProfileRepository.updatePassword(newUser.email,newUser.password).map {
          case true => Redirect(routes.ProfileController.viewProfile).withSession("email" -> newUser.email)
            .flashing("success" -> "password updated successfully")
          case false => Ok(views.html.forgotPassword(userSignInForm))
        }
      })
  }

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

  def updateProfile(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userProfileForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.viewProfile(formWithErrors)))
      },
      profile => {
        val updatedDetails = UserProfileForm(profile.fname, profile.mname, profile.lname,
          profile.mobile,profile.age, profile.hobbies)
        val userUpdated = userProfileRepository.updateDetails(request.session.get("email").get, updatedDetails)
        userUpdated.map {
          case true =>
            Redirect(routes.ProfileController.getUserInformation())
          case false => InternalServerError("user details could not be updated")
        }
      })
  }


  }
