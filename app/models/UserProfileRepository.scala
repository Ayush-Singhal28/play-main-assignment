package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import slick.lifted.ProvenShape.proveShapeOf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait userRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val userQuery: TableQuery[UserTable] = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[UserData](tag, "UserData") {

    def * : ProvenShape[UserData] = (id,
      fname,
      mname,
      lname,
      email,
      password,
      mobile,
      gender,
      age,
      hobbies,
      haveAuthToLogin,
      isNormalUser) <> (UserData.tupled, UserData.unapply)

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def fname: Rep[String] = column[String]("fname")

    def mname: Rep[String] = column[String]("mname")

    def lname: Rep[String] = column[String]("lname")

    def email: Rep[String] = column[String]("email")

    def password: Rep[String] = column[String]("password")

    def mobile: Rep[String] = column[String]("mobile")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def hobbies: Rep[String] = column[String]("hobbies")

    def haveAuthToLogin: Rep[Boolean] = column[Boolean]("haveAuthToLogin")

    def isNormalUser: Rep[Boolean] = column[Boolean]("isNormalUser")

  }

}

trait UserRepositoryTrait {
  def store(user: UserData): Future[Boolean]

  def isUserExist(email: String): Future[Boolean]

  def doSignIn(email: String, password: String): Future[Boolean]

  def haveAuthToSignIn(email: String): Future[Boolean]

  def updatePassword(email: String, password: String): Future[Boolean]

  def getInformation(email: String): Future[UserData]

  def updateDetails(email: String, user: UserProfileForm): Future[Boolean]

  def listOfUser(): Future[List[UserData]]
}

trait UserRepositoryImpl extends UserRepositoryTrait {
  self: userRepositoryTable =>

  import profile.api._

  def store(user: UserData): Future[Boolean] = {
    db.run(userQuery += user) map (_ > 0)
  }

  def isUserExist(email: String): Future[Boolean] = {
    val queryResult = userQuery.filter(_.email.toLowerCase === email.toLowerCase).to[List].result
    db.run(queryResult) map (_.nonEmpty)
  }

  def doSignIn(email: String, password: String): Future[Boolean] = {
    val queryResult = userQuery.filter(user =>
      user.email.toLowerCase === email.toLowerCase && user.password === password)
      .to[List].result
    db.run(queryResult) map (_.nonEmpty)
  }

  def haveAuthToSignIn(email: String): Future[Boolean] = {
    val queryResult = userQuery.filter(user => user.email.toLowerCase === email.toLowerCase && user.haveAuthToLogin)
      .to[List].result
    db.run(queryResult) map (_.nonEmpty)
  }

  def updatePassword(email: String, password: String): Future[Boolean] = {
    val queryResult = userQuery.filter(user => user.email.toLowerCase === email.toLowerCase).map(_.password)
      .update(password)
    db.run(queryResult) map (_ > 0)
  }

  def getInformation(email: String): Future[UserData] = {
    val queryResult = userQuery.filter(user => user.email.toLowerCase === email.toLowerCase).result.head
    db.run(queryResult)
  }

  def updateDetails(email: String, changeField: UserProfileForm): Future[Boolean] = {
    val queryResult = userQuery.filter(_.email.toLowerCase === email.toLowerCase)
      .map(user => (user.fname, user.mname, user.lname, user.mobile, user.age, user.hobbies))
      .update(changeField.fname,
        changeField.mname,
        changeField.lname,
        changeField.mobile,
        changeField.age,
        changeField.hobbies)
    db.run(queryResult) map (_ > 0)
  }

  def listOfUser(): Future[List[UserData]] = {
    db.run(userQuery.to[List].result)
  }
}

class UserProfileRepository @Inject()(
                                       protected val dbConfigProvider: DatabaseConfigProvider
                                     )
  extends UserRepositoryTrait
    with userRepositoryTable
    with UserRepositoryImpl
