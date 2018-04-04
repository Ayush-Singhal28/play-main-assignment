package controllers

import javax.inject._

import models.{UserData, UserInfo, UserProfileRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */


@Singleton
class HomeController @Inject()(userProfileRepository: UserProfileRepository,
                               cc: ControllerComponents) extends
  AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi


  val allLetters = """[A-Za-z]*"""
  val userForm: Form[UserInfo] = Form(
    mapping(
      "fname" -> text.verifying("must contain character only", name => name.matches(allLetters)),
      "mname" -> text,
      "lname" -> text.verifying("must contain character only", name => name.matches(allLetters)),
      "email" -> email,
      "password" -> text,
      "confirmPassword" -> text,
      "mobile" -> text.verifying("Must contain 10 digit number", mobile => mobile.length() == 10),
      "gender" -> text,
      //scalastyle:off
      "age" -> number(min = 18, max = 75),
      //scalastyle:on
      "hobbies" -> text
    )

    (UserInfo.apply)(UserInfo.unapply)
      .verifying("Please Reconfirm Password", UserInfo => UserInfo.password == UserInfo.confirmPassword)
  )

  /**
    * Redirect to index page.
    * This is welcome Page
    */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  /**
    * Redirect to signUp page.
    * User able to fill signUp form
    */
  def signUp(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signUp(userForm))
  }

  /**
    * Validate user form field
    * and then store in database
    */
  def validateAndStoreInDb(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    userForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.signUp(formWithErrors)))
      },
      userData => {
        val newUser = UserData(0,
          userData.fname,
          userData.mname,
          userData.lname,
          userData.email,
          userData.password,
          userData.mobile,
          userData.gender,
          userData.age,
          userData.hobbies,
          true,
          true
        )

        userProfileRepository.isUserExist(userData.email).flatMap {
          case true => Future.successful(Redirect(routes.ProfileController.signIn)
            .flashing("failed" -> "user already exist. Do sign In"))

          case false => userProfileRepository.store(newUser).flatMap {
            case true =>
              Future.successful(Redirect(routes.ProfileController.viewProfile)
                .withSession("email" -> newUser.email)
                .flashing("success" -> "user data stored successfully"))
            case false => Future.successful(Ok(views.html.signUp(userForm)))
          }
        }
      })
  }
}
