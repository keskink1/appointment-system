package com.keskin.users.infrastructure.persistence.repository.impl;

import com.keskin.common.dto.response.PaginatedResponseDto;
import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import com.keskin.users.infrastructure.persistence.entity.UserEntity;
import com.keskin.users.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.keskin.users.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence adapter implementation for SQL databases.
 * Bridges the Domain Repository interface with Spring Data JPA.
 */
@Repository
public class SqlUserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserPersistenceMapper userMapper;

    public SqlUserRepositoryImpl(JpaUserRepository entityRepository, UserPersistenceMapper userMapper) {
        this.jpaUserRepository = entityRepository;
        this.userMapper = userMapper;
    }

    /**
     * Retrieves a paginated list of users from the database, sorted by creation date.
     * <p>
     * This method converts the Spring Data JPA {@link Page} result into a domain-specific
     * {@link PaginatedResponseDto} to ensure the Domain layer remains decoupled from
     * persistence framework dependencies.
     * </p>
     *
     * @param page The zero-based page index to retrieve.
     * @param size The number of records per page.
     * @return A {@link PaginatedResponseDto} containing the list of {@link User} domain models
     * and pagination metadata (total elements, total pages, etc.).
     */
    @Override
    public PaginatedResponseDto<User> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<UserEntity> entityPage = jpaUserRepository.findAll(pageable);

        List<User> domainUsers = entityPage
                .getContent()
                .stream()
                .map(userMapper::toDomain)
                .toList();

        return new PaginatedResponseDto<>(
                domainUsers,
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.getNumber()
        );
    }

    @Override
    public Optional<User> findById(UUID id) {
       return jpaUserRepository.findById(id)
               .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmailAndDeletedFalse(email)
                .map(userMapper::toDomain);
    }

    @Override
    public void saveUser(User user) {
        UserEntity entity = userMapper.toEntity(user);
        jpaUserRepository.save(entity);
    }

    @Override
    public boolean existsByEmailAndDeletedFalse(String email) {
        return jpaUserRepository.existsByEmailAndDeletedFalse(email);
    }
}
