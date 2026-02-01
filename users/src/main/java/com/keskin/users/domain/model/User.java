package com.keskin.users.domain.model;

import com.keskin.users.domain.valueobject.Age;
import com.keskin.users.domain.valueobject.Email;
import com.keskin.users.domain.valueobject.Name;
import com.keskin.users.domain.valueobject.Password;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class User extends BaseEntity {

    private Name name;

    private Age age;

    private Email email;

    private Password password;

    private Role role;

    private boolean active = true;

    public User(UUID uuid, LocalDateTime createdAt, String createdBy, boolean deleted, LocalDateTime deletedAt, String deletedBy, String newNameValue, Integer newAgeValue, String newEmailValue, String newPasswordValue, Role role, boolean active) {
        super(uuid, createdAt, createdBy, deleted, deletedAt, deletedBy);
        this.name = new Name(newNameValue);
        this.age = new Age(newAgeValue);
        this.email = new Email(newEmailValue);
        this.password = new Password(newPasswordValue);
        this.role = role;
        this.active = active;
    }

    public User(String newNameValue, Integer newAgeValue, String newEmailValue, String newPasswordValue) {
        super(
                UUID.randomUUID(),
                LocalDateTime.now(),
                "SYSTEM"// change after jwt

        );
        this.name = new Name(newNameValue);
        this.age = new Age(newAgeValue);
        this.email = new Email(newEmailValue);
        this.password = new Password(newPasswordValue);
        this.role = Role.USER;
    }

    public void activate() {
        if (this.active) {
            throw new IllegalStateException("User is already active");
        }
        this.active = true;
    }

    public void deactivate() {
        if (!this.active) {
            throw new IllegalStateException("User is already inactive");
        }
        this.active = false;
    }

    public void changeRoleToEmployee(){
        this.role = (Role.EMPLOYEE);
    }

    public void promoteToAdmin(){
        this.role = (Role.ADMIN);
    }

    public void updateUser(String newNameValue, Integer newAgeValue, String newEmailValue, String updatedBy){
        if (isDeleted()){
            throw new IllegalStateException("Deleted user can't be updated");
        }
        this.name = new Name(newNameValue);
        this.age = new Age(newAgeValue);
        this.email = new Email(newEmailValue);
        super.updateAudit(updatedBy);
    }

    public void deleteUser(String deletedBy){
        if (this.role == Role.ADMIN) {
            throw new IllegalStateException("Admins can't be deleted!");
        }
        super.markAsDeleted(deletedBy);
    }
}
