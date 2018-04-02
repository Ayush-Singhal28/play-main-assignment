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

    def * : ProvenShape[AssignmentData] = (id,
      title,
      description
    ) <> (AssignmentData.tupled, AssignmentData.unapply)

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")

  }

}

trait UserAssignmentTrait {
  def storingAssignment(assignment: AssignmentData): Future[Boolean]

  def viewAssignment(): Future[List[AssignmentData]]

}

trait UserAssignmentImpl extends UserAssignmentTrait {
  self: userAssignmentTable =>

  import profile.api._

  def storingAssignment(assignment: AssignmentData): Future[Boolean] = {
    db.run(assignmentQuery += assignment) map (_ > 0)
  }

  def viewAssignment(): Future[List[AssignmentData]] = {
    db.run(assignmentQuery.to[List].result)
  }

}

class AssignmentRepository @Inject()
(
  protected val dbConfigProvider: DatabaseConfigProvider
)
  extends UserAssignmentTrait with userAssignmentTable with UserAssignmentImpl
