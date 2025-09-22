package com.kijinkai.domain.user.adapter.out.persistence.mapper;

import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.domain.model.User;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    User toUser(UserJpaEntity jpaEntity);

    UserJpaEntity toUserJpaEntity(User user);

    // 리스트 매핑
    List<User> toUserInList(List<UserJpaEntity> jpaEntities);

    List<UserJpaEntity> toUserJpaEntityInList(List<User> users);

}

