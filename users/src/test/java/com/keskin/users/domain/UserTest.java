package com.keskin.users.domain;

import com.keskin.common.enums.Role;
import com.keskin.users.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.createUser(
                "system",
                "john",
                21,
                "john@gmail.com",
                "12345"
        );
    }

    @Test
    void shouldReturnUserCreated(){
        assertNotNull(user);
        assertNotNull(user.getUuid());
        assertEquals("john", user.getName().value());
        assertEquals(21, user.getAge().value());
        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isActive());
        assertFalse(user.isDeleted());
    }

    @Test
    void shouldReturnUserUpdated(){
        user.updateUser(
                "jane",
                null,
                "john12@gmail.com",
                "system"
        );

        assertEquals(21, user.getAge().value());
        assertEquals("jane", user.getName().value());
        assertNotNull(user.getUpdatedBy());
    }

    @Test
    void shouldThrowIllegalStateException_ifUserIsInactive(){
        user.deactivate("system");

        assertThrowsExactly(IllegalStateException.class, () ->
                user.updateUser(
                        "michael",
                        22,
                        null,
                        "system"
                ));
    }

    @Test
    void shouldThrowIllegalStateException_ifUserIsDeleted(){
        user.deleteUser("system");

        assertThrowsExactly(IllegalStateException.class, () ->
                user.updateUser(
                        "michael",
                        22,
                        null,
                        "system"
                ));
    }

    @Test
    void shouldPromoteToAdmin(){
        user.promoteToAdmin("system");
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void shouldThrowIllegalStateException_ifDeleteAdmin(){
        user.promoteToAdmin("system");
        IllegalStateException ex = assertThrowsExactly(IllegalStateException.class, () ->
                user.deleteUser("system"));
        assertEquals("Admins can't be deleted!", ex.getMessage());
    }
}
