package models

import model.ModelsTest
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AssignmentRepositoryTest extends Specification {

  val repo = new ModelsTest[AssignmentRepository]

  private val assignment = AssignmentData(1,"play","create a registration form")

  "Assignment Repository" should {
    "store assignment" in {

      val storeResult = Await.result(repo.repository.storingAssignment(assignment), Duration.Inf)
      storeResult must equalTo(true)
    }
  }

  "Assignment Repository" should {
    "view assignment" in {
      val storeResult = Await.result(repo.repository.viewAssignment(), Duration.Inf)
      storeResult must equalTo(List(assignment))
    }
  }

 /* "Assignment Repository" should {
    "delete assignment" in {
      val storeResult = Await.result(repo.repository.deleteAssignment(1), Duration.Inf)
      storeResult must equalTo(true)
    }
  }*/

}
