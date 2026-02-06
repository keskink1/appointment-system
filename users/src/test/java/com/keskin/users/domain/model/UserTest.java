package com.keskin.users.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should successfully update user details and set audit fields")
    void shouldUpdateUserDetails() {
        // GEREKLİ: createUser metoduna 5 parametre (createdBy ekledik)
        User user = User.createUser("SYSTEM", "Old Name", 20, "old@mail.com", "pass123");

        // Önemli: İlk oluşturulma audit bilgilerini doğrula
        assertEquals("SYSTEM", user.getCreatedBy());
        assertNotNull(user.getCreatedAt());

        // GÜNCELLEME
        user.updateUser("New Name", 25, "new@mail.com", "ADMIN_USER");

        assertEquals("New Name", user.getName().value(), "Name should be updated");
        assertEquals(25, user.getAge().value(), "Age should be updated");
        assertEquals("new@mail.com", user.getEmail().value(), "Email should be updated");

        assertNotNull(user.getUpdatedAt(), "Updated timestamp should not be null");
        assertEquals("ADMIN_USER", user.getUpdatedBy(), "UpdatedBy should match the actor");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating a deleted user")
    void shouldThrowExceptionWhenUpdatingDeletedUser() {
        // GEREKLİ: 5 parametre
        User user = User.createUser("SYSTEM", "Mert", 30, "mert@mail.com", "sifre");

        user.deleteUser("ADMIN_ACTOR");

        assertTrue(user.isDeleted(), "User should be marked as deleted");
        assertEquals("ADMIN_ACTOR", user.getDeletedBy());

        assertThrows(IllegalStateException.class, () -> {
            user.updateUser("New Name", 31, "new@mail.com", "SYSTEM");
        }, "Updating a deleted user must throw an exception");
    }

    @Test
    @DisplayName("Should not update fields if new values are null or blank")
    void shouldNotUpdateIfValuesAreNullOrBlank() {
        User user = User.createUser("SYSTEM", "Mert", 30, "mert@mail.com", "sifre");

        // Null veya boş gönderiyoruz
        user.updateUser(null, null, "  ", "UPDATER");

        // Eski değerler korunmalı
        assertEquals("Mert", user.getName().value());
        assertEquals(30, user.getAge().value());
        assertEquals("mert@mail.com", user.getEmail().value());
        assertEquals("UPDATER", user.getUpdatedBy());
    }
}