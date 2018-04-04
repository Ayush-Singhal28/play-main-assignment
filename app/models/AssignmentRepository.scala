package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import slick.lifted.ProvenShape.proveShapeOf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait userAssignmentTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val assignmentQuery: TableQuery[AssignmentTable] = TableQuery[AssignmentTable]

  class AssignmentTable(tag: Tag) extends Table[AssignmentData](tag, "AssignmentData") {

    //scalastyle:off
    def * : ProvenShape[AssignmentData] = (id,
      title,
      description
    ) <> (AssignmentData.tupled, AssignmentData.unapply)
    //scalastyle:on

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")

  }

}

trait UserAssignmentTrait {
  def storingAssignment(assignment: AssignmentData): Future[Boolean]

  def viewAssignment(): Future[List[AssignmentData]]

  def deleteAssignment(id: Int): Future[Boolean]

}

trait UserAssignmentImpl extends UserAssignmentTrait {
  self: userAssignmentTable =>

  import profile.api._

  /**
    * Used to store assignment in database
    */
  def storingAssignment(assignment: AssignmentData): Future[Boolean] = {
    db.run(assignmentQuery += assignment) map (_ > 0)
  }

  /**
    * Used to get Assignment Data from database.
    */
  def viewAssignment(): Future[List[AssignmentData]] = {
    db.run(assignmentQuery.to[List].result)
  }

  /**
    * Used to delete Assignment Data from database.
    */
   def deleteAssignment(id: Int): Future[Boolean] = {
     db.run(assignmentQuery.filter(_.id === id).delete) map (_ > 0)
   }
}

class AssignmentRepository @Inject()
(
  protected val dbConfigProvider: DatabaseConfigProvider
)
  extends UserAssignmentTrait with userAssignmentTable with UserAssignmentImpl

