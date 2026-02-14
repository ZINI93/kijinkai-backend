package com.kijinkai.domain.user.adapter.out.persistence;

import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.kijinkai.domain.user.adapter.out.persistence.repository.UserRepository;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserRepository userRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public User saveUser(User user) {
        UserJpaEntity userJpaEntity = userPersistenceMapper.toUserJpaEntity(user);
        userJpaEntity = userRepository.save(userJpaEntity);
        return userPersistenceMapper.toUser(userJpaEntity);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByUserUuid(UUID uuid) {
        return userRepository.findByUserUuid(uuid)
                .map(userPersistenceMapper::toUser);
    }

    @Override
    public Optional<User> findByEmailAndIsSocial(String email, Boolean isSocial) {
        return userRepository.findByEmailAndIsSocial(email, isSocial)
                .map(userPersistenceMapper::toUser);
    }


    @Override
    public Optional<User> findByEmailAndUserStatusAndIsSocial(String email, UserStatus userStatus, Boolean isSocial) {
        return userRepository.findByEmailAndUserStatusAndIsSocial(email, userStatus, isSocial)
                .map(userPersistenceMapper::toUser);
    }

    @Override
    public Optional<User> findByEmailAndUserStatus(String email, UserStatus userStatus) {
        return userRepository.findByEmailAndUserStatus(email, userStatus)
                .map(userPersistenceMapper::toUser);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .map(userPersistenceMapper::toUser);
    }

    @Override
    public List<User> findAllByUserUuidIn(List<UUID> userUuid) {
        return userRepository.findAllByUserUuidIn(userUuid)
                .stream().map(userPersistenceMapper::toUser).toList();
    }


    @Override
    public Page<User> findAllByEmailAndNickName(String email, String name, Pageable pageable) {
        return userRepository.findAllByEmailAndNickName(email, name, pageable)
                .map(userPersistenceMapper::toUser);
    }

    @Override
    public void deleteUser(User user) {
        UserJpaEntity userJpaEntity = userPersistenceMapper.toUserJpaEntity(user);
        userRepository.delete(userJpaEntity);
    }

    @Override
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }
}
