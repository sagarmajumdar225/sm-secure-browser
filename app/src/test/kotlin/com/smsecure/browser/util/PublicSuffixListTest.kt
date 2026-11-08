package com.smsecure.browser.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PublicSuffixListTest {

    @Test
    fun testStandardDomains() {
        assertEquals("example.com", PublicSuffixList.getRegistrableDomain("example.com"))
        assertEquals("example.com", PublicSuffixList.getRegistrableDomain("sub.example.com"))
        assertEquals("example.com", PublicSuffixList.getRegistrableDomain("a.b.c.example.com"))
    }

    @Test
    fun testTwoPartTlds() {
        assertEquals("bbc.co.uk", PublicSuffixList.getRegistrableDomain("news.bbc.co.uk"))
        assertEquals("bbc.co.uk", PublicSuffixList.getRegistrableDomain("bbc.co.uk"))
        assertEquals("amazon.co.jp", PublicSuffixList.getRegistrableDomain("sub.amazon.co.jp"))
    }

    @Test
    fun testFullUrls() {
        assertEquals("example.org", PublicSuffixList.getRegistrableDomain("https://sub.domain.example.org/path/test?q=1"))
        assertEquals("github.com", PublicSuffixList.getRegistrableDomain("http://github.com/smsecure"))
    }

    @Test
    fun testIpAndLocalhost() {
        assertEquals("127.0.0.1", PublicSuffixList.getRegistrableDomain("127.0.0.1"))
        assertEquals("localhost", PublicSuffixList.getRegistrableDomain("localhost"))
    }
}
