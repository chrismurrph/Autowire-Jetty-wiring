package wiring

object Constants{
  val WebAppDirName = "exampleWiring"
}

trait Api{
  def list(path: String): Seq[String]
}