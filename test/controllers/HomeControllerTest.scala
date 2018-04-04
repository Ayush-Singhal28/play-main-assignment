import controllers.HomeController
import models.UserProfileRepository
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

class HomeControllerSpec extends PlaySpec with Mockito{


  "go to index page" in {
    val controller = getMockedObject
    val result = controller.homeController.index().apply(FakeRequest().withCSRFToken)
    status(result) must equal (OK)
  }

  "go to signUp page" in {
    val controller = getMockedObject
    val result = controller.homeController.signUp().apply(FakeRequest().withCSRFToken)
    status(result) must equal (OK)
  }

  "validate and store in database" in {
    val controller = getMockedObject
    val result = controller.homeController.validateAndStoreInDb().apply(FakeRequest().withCSRFToken)
    status(result) must equal (400)
  }

  def getMockedObject: TestObj = {
    val mockedUserProfileRepo = mock[UserProfileRepository]

    val controller = new HomeController(mockedUserProfileRepo, stubControllerComponents())

    TestObj(stubControllerComponents(),controller)
  }

  case class TestObj(controllerComponent: ControllerComponents,homeController: HomeController)

}

