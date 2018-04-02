package models

case class UserChangingPassword (
                                email: String,
                                password: String,
                                confirmPassword: String
                                )
