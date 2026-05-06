package com.safesteps.app

import com.safesteps.app.data.Contact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactTest {

    @Test
    fun contactCopyPreservesAllFields() {
        val original = Contact(
            id = "abc-123",
            name = "Div",
            phoneNumber = "123-2434",
            relationship = "Friend",
            isPrimary = true
        )
        val updated = original.copy(name = "Bob", isPrimary = false)

        assertEquals("Bob", updated.name)
        assertEquals("abc-123", updated.id)
        assertEquals("123-2434", updated.phoneNumber)
        assertEquals("Friend", updated.relationship)
        assertFalse(updated.isPrimary)
    }

    @Test
    fun primaryContactFlagDefaultIsFalse() {
        val contact = Contact(id = "1", name = "Div", phoneNumber = "123")
        assertFalse(contact.isPrimary)
    }

    @Test
    fun relationshipDefaultIsEmpty() {
        val contact = Contact(id = "1", name = "Div", phoneNumber = "123")
        assertEquals("", contact.relationship)
    }

    @Test
    fun primaryContactSortedBeforeOthers() {
        val contacts = listOf(
            Contact(id = "1", name = "Dawg", phoneNumber = "111", isPrimary = false),
            Contact(id = "2", name = "Div", phoneNumber = "222", isPrimary = true)
        )
        val sorted = contacts.sortedByDescending { it.isPrimary }
        assertTrue(sorted.first().isPrimary)
        assertEquals("Div", sorted.first().name)
    }
}
