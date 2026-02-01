package com.keskin.users.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should successfully update user details and set audit fields")
    void shouldUpdateUserDetails() {
        User user = new User("Old Name", 20, "old@mail.com", "pass123");

        user.updateUser("New Name", 25, "new@mail.com", "ADMIN_USER");

        assertEquals("New Name", user.getName().value(), "Name should be updated");
        assertEquals(25, user.getAge().value(), "Age should be updated");
        assertEquals("new@mail.com", user.getEmail().value(), "Email should be updated");
        assertNotNull(user.getUpdatedAt(), "Updated timestamp should not be null");
        assertEquals("ADMIN_USER", user.getUpdatedBy(), "UpdatedBy should match the updater");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating a deleted user")
    void shouldThrowExceptionWhenUpdatingDeletedUser() {
        User user = new User("Mert", 30, "mert@mail.com", "sifre");
        user.deleteUser("SYSTEM");

        assertThrows(IllegalStateException.class, () -> {
            user.updateUser("New Name", 31, "new@mail.com", "SYSTEM");
        }, "Updating a deleted user must throw an exception");
    }
}