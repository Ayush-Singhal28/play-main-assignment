package models


case class UserInfo(
                     fname: String,
                     mname: String,
                     lname: String,
                     email: String,
                     password: String,
                     confirmPassword: String,
                     mobile: String,
                     gender: String,
                     age: Int,
                     hobbies: String
                   )
