package com.kijinkai.domain.user.adapter.out.persistence.mapper;

import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "social", source = "isSocial")
    User toUser(UserJpaEntity jpaEntity);

    @Mapping(target = "isSocial", source = "social")
    @Mapping(target = "isServiceTermAgreed", source = "serviceTermAgreed")
    @Mapping(target = "isPrivacyPolicyAgreed", source = "privacyPolicyAgreed")
    UserJpaEntity toUserJpaEntity(User user);

    List<User> toUserInList(List<UserJpaEntity> jpaEntities);
    List<UserJpaEntity> toUserJpaEntityInList(List<User> users);

}

