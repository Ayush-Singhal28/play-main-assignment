package models

case class UserData(
                     id : Int,
                     fname: String,
                     mname: String,
                     lname: String,
                     email: String,
                     password: String,
                     mobile: String,
                     gender: String,
                     age: Int,
                     hobbies: String,
                     haveAuthToLogin: Boolean,
                     isNormalUser: Boolean
                   )

