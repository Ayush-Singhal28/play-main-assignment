package model

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.reflect.ClassTag

class ModelsTest[T: ClassTag] {
  def fakeApp: Application = {
    new GuiceApplicationBuilder()
      .build()
  }

  lazy val app2dao = Application.instanceCache[T]
  lazy val repository: T = app2dao(fakeApp)

}