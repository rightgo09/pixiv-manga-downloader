object Main extends App {
  import pixivmangadownloader._

  val pixivMangaUrl = args.head
  val dl = new PixivMangaDownloader(pixivMangaUrl)
//  dl.save(sys.env("HOME")+"/Picures")
  dl.save()
}
