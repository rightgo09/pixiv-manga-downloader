import java.io._

package object pixivmangadownloader {
  def p(msg: Any) { println(msg)}

  def sleep(sec: Int) { p("sleep %d...".format(sec)); Thread.sleep(sec * 1000) }

  def withFile(filePath: String)(op: BufferedOutputStream => Unit) {
    p(filePath)
    val file = new BufferedOutputStream(new FileOutputStream(filePath))
    try { op(file) } finally { file.close() }
  }
}

