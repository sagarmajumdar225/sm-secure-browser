package com.smsecure.browser.data

import org.junit.Assert.assertEquals
import org.junit.Test

class EntitiesTest {
    @Test
    fun testHistoryEntityCreation() {
        val entity = HistoryEntity(id = 1, url = "https://example.com", title = "Example")
        assertEquals(1, entity.id)
        assertEquals("https://example.com", entity.url)
        assertEquals("Example", entity.title)
    }

    @Test
    fun testBookmarkEntityCreation() {
        val entity = BookmarkEntity(id = 2, url = "https://duckduckgo.com", title = "DuckDuckGo")
        assertEquals(2, entity.id)
        assertEquals("https://duckduckgo.com", entity.url)
        assertEquals("DuckDuckGo", entity.title)
    }

    @Test
    fun testSiteExceptionEntityCreation() {
        val entity = SiteExceptionEntity(domain = "example.com", allowCookies = true, allowStorage = false, allowScripts = true)
        assertEquals("example.com", entity.domain)
        assertEquals(true, entity.allowCookies)
        assertEquals(false, entity.allowStorage)
        assertEquals(true, entity.allowScripts)
    }
}
