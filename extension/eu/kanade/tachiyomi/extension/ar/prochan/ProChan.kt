package eu.kanade.tachiyomi.extension.ar.prochan

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.OkHttpClient
import okhttp3.Request

class ProChan : ParsedHttpSource() {

    override val name = "ProChan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    override val client: OkHttpClient = OkHttpClient()

    // Popular
    override fun popularMangaRequest(page: Int) = Request.Builder().url("$baseUrl/popular?page=$page").build()
    override fun popularMangaSelector() = "div.manga"
    override fun popularMangaFromElement(element: org.jsoup.nodes.Element): SManga {
        val manga = SManga.create()
        manga.title = element.select("h3").text()
        manga.setUrlWithoutDomain(element.select("a").attr("href"))
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }
    override fun popularMangaNextPageSelector() = "a.next"

    // Latest
    override fun latestUpdatesRequest(page: Int) = Request.Builder().url("$baseUrl/latest?page=$page").build()
    override fun latestUpdatesSelector() = "div.manga"
    override fun latestUpdatesFromElement(element: org.jsoup.nodes.Element): SManga {
        val manga = SManga.create()
        manga.title = element.select("h3").text()
        manga.setUrlWithoutDomain(element.select("a").attr("href"))
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }
    override fun latestUpdatesNextPageSelector() = "a.next"

    // Search
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList) =
        Request.Builder().url("$baseUrl/search?q=$query&page=$page").build()
    override fun searchMangaSelector() = "div.manga"
    override fun searchMangaFromElement(element: org.jsoup.nodes.Element): SManga {
        val manga = SManga.create()
        manga.title = element.select("h3").text()
        manga.setUrlWithoutDomain(element.select("a").attr("href"))
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }
    override fun searchMangaNextPageSelector() = "a.next"

    // Manga Details
    override fun mangaDetailsParse(document: org.jsoup.nodes.Document): SManga {
        val manga = SManga.create()
        manga.title = document.select("h1.title").text()
        manga.description = document.select("div.description").text()
        manga.genre = document.select("div.genres a").joinToString { it.text() }
        manga.thumbnail_url = document.select("img.cover").attr("src")
        manga.status = SManga.UNKNOWN
        return manga
    }

    // Chapters
    override fun chapterListSelector() = "ul.chapters li"
    override fun chapterFromElement(element: org.jsoup.nodes.Element): SChapter {
        val chapter = SChapter.create()
        chapter.name = element.select("a").text()
        chapter.setUrlWithoutDomain(element.select("a").attr("href"))
        return chapter
    }

    // Pages
    override fun pageListParse(document: org.jsoup.nodes.Document) = document.select("div.pages img").mapIndexed { i, el ->
        Page(i, "", el.attr("src"))
    }
    override fun imageUrlParse(document: org.jsoup.nodes.Document) = document.select("img.page").attr("src")
}
