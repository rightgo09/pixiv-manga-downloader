package pixivmangadownloader

import java.io.File
import scalaj.http.{BaseHttp, HttpRequest}

class PixivMangaDownloader(url: String) {
  object PixivHttp extends BaseHttp(userAgent = "pixiv-scala/0.1")

  def mangaUrl = url.replace("medium", "manga")

  def createFilePath(baseDir: File, file: String): String = new File(baseDir, file).toString

  def filePath(baseDir: File)(i: Int, ext: String): String = createFilePath(baseDir, "%03d.%s".format(i, ext))

  def save(baseDir: String = ".") {
    val url = mangaUrl
    p(url)
    val res = PixivHttp(url).asString
    sleep(2)

    val imageSrcRe = if (res.body.contains("<html class=\"_book-viewer rtl\"")) {
      """pixiv.context.originalImages\[\d+\] = "(.*?)"""".r
    } else {
      """data-src="(.*?)"""".r
    }

    val titleRe = """<title>(.*)</title>""".r

    // 半角スラッシュはディレクトリ名にしたくない
    val title: String = titleRe.findAllIn(res.body).matchData.toList.head.group(1).replace("/", "／")
    p(title)

    val baseTitleDir = new File(baseDir, title)
    p(baseTitleDir)
    baseTitleDir.mkdir()
    val path = filePath(baseTitleDir)_ // 部分適用

    imageSrcRe.findAllIn(res.body).matchData.zipWithIndex.foreach { case (m, i) =>
      //原寸画像のURL組み立て
      //val imageUrl = m.group(1).replace("\\", "").replace("/c/1200x1200", "").replace("img-master", "img-original").replace("_master1200", "")
      val imageUrl = m.group(1).replace("\\", "")
      p(imageUrl)
      val ext: String = imageUrl.split('.').last
      val res = PixivHttp(imageUrl).header("Referer", url).asBytes
      withFile(path(i, ext)) { file => file.write(res.body) }
      sleep(2)
    }
  }
}
