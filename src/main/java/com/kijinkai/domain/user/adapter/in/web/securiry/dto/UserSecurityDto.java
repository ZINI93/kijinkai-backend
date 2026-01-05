//package com.kijinkai.domain.user.adapter.in.web.securiry.dto;
//
//
//import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
//import com.kijinkai.domain.user.domain.model.User;
//import com.kijinkai.domain.user.domain.model.UserRole;
//import lombok.Builder;
//import lombok.Getter;
//
//import java.util.UUID;
//
//
//@Getter
//@Builder
//public class UserSecurityDto {
//
//    private final Long userId;
//    private final UUID userUuid;
//    private final String email;
//    private final String password;
//    private final UserRole userRole;
//
//
//    public static UserSecurityDto from(UserJpaEntity userJpaEntity){
//        return new UserSecurityDto(
//                userJpaEntity.getUserId(),
//                userJpaEntity.getUserUuid(),
//                userJpaEntity.getEmail(),
//                userJpaEntity.getPassword(),
//                userJpaEntity.getUserRole()
//        );
//    }
//}
