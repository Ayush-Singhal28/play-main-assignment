package models

import model.{ModelsTest}
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class UserProfileRepositoryTest extends Specification {

  val repo = new ModelsTest[UserProfileRepository]
  val userProfile = UserProfileForm("Ayush", "Kumar", "Singhal",
    "9897999465", 23, "watching movies")

  private val user = UserData(1, "Ayush", "Kumar", "Singhal",
    "ayush@knoldus", "12345", "9897999465",
    "male", 23, "watching movies", true, true)

  "User Profile Repository" should {
    "store user profile" in {
      val storeResult = Await.result(repo.repository.store(user), Duration.Inf)
      storeResult must equalTo(true)
    }
  }

  "User Profile Repository" should {
    "check is user exist" in {
      val storeResult = Await.result(repo.repository.isUserExist("ayush@knoldus"), Duration.Inf)
      storeResult must equalTo(true)
    }
  }

  "User Profile Repository" should {
    "do sign In" in {
      val storeResult = Await.result(repo.repository.doSignIn("ayush@knoldus", "12345"), Duration.Inf)
      storeResult must equalTo(true)
    }
  }

  "User Profile Repository" should {
    "check whether a user have authority to sign In" in {
      val storeResult = Await.result(repo.repository.haveAuthToSignIn("ayush@knoldus"), Duration.Inf)
      storeResult must equalTo(true)
    }
  }

  "User Profile Repository" should {
    "get information of particular user" in {
      val storeResult = Await.result(repo.repository.getInformation("ayush@knoldus"), Duration.Inf)
      storeResult must equalTo(user)
    }
  }

  "User Profile Repository" should {
    "get list of user" in {
      val storeResult = Await.result(repo.repository.listOfUser(), Duration.Inf)
      storeResult must equalTo(List(user))
    }
  }


  "User Profile Repository" should {
    "update password" in {
      val storeResult = Await.result(repo.repository.updatePassword("ayush@knoldus", "123"), Duration.Inf)
      storeResult must equalTo(true)
    }
  }

  "User Profile Repository" should {
    "update details of particular user" in {
      val userProfileForm = UserProfileForm("Ayush", "Kumar", "Singhal",
        "9897999465", 23, "watching movies")
      val email = "ayush@knoldus"
      val storeResult = Await.result(repo.repository.updateDetails(email, userProfile), Duration.Inf)
      storeResult must equalTo(true)
    }
  }


}


