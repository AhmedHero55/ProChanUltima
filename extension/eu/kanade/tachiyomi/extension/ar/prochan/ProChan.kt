package eu.kanade.tachiyomi.extension.ar.prochan

import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.source.ConfigurableSource
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.network.GET
import okhttp3.OkHttpClient
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ProChan : ParsedHttpSource(), ConfigurableSource {

    override val name = "ProChan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    override val client: OkHttpClient = network.client

    // Popular Manga
    override fun popularMangaRequest(page: Int) = GET("$baseUrl/manga/page/$page", headers)
    override fun popularMangaSelector() = "div.manga-item"
    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.title = element.select("h3.title").text()
        manga.setUrlWithoutDomain(element.select("a").attr("href"))
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }
    override fun popularMangaNextPageSelector() = "a.next"

    // Latest Updates
    override fun latestUpdatesRequest(page: Int) = GET("$baseUrl/latest/page/$page", headers)
    override fun latestUpdatesSelector() = "div.manga-item"
    override fun latestUpdatesFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.title = element.select("h3.title").text()
        manga.setUrlWithoutDomain(element.select("a").attr("href"))
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }
    override fun latestUpdatesNextPageSelector() = "a.next"

    // Search
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList) =
        GET("$baseUrl/search?q=$query&page=$page", headers)

    override fun searchMangaSelector() = "div.manga-item"
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = "a.next"

    // Manga Details
    override fun mangaDetailsParse(document: Document): SManga {
        val manga = SManga.create()
        manga.title = document.select("h1.title").text()
        manga.description = document.select("div.summary").text()
        manga.genre = document.select("div.genres a").joinToString { it.text() }
        manga.thumbnail_url = document.select("img.cover").attr("src")
        manga.status = SManga.ONGOING
        return manga
    }

    // Chapters
    override fun chapterListSelector() = "ul.chapter-list li"
    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        chapter.name = element.select("a").text()
        chapter.setUrlWithoutDomain(element.select("a").attr("href"))
        return chapter
    }

    // Pages
    override fun pageListParse(document: Document): List<Page> {
        return document.select("div.page-break img").mapIndexed { i, img ->
            Page(i, "", img.attr("src"))
        }
    }

    override fun imageUrlParse(document: Document) = document.select("img.manga-page").attr("src")
}
