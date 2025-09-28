package com.kijinkai.domain.user.domain.model;

import com.kijinkai.domain.user.domain.exception.UserRoleValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserTest {


    @Test
    @DisplayName("사용자 프로필 정보 업데이트")
    void userUpdateTest(){

        //given
        User user = User.builder()
                .email("pakupaku@gmail.com")
                .password("new12341234")
                .nickname("파쿠파쿠")
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        //when
        user.updateUser("뉴파쿠파쿠", "new12341234");

        //then
        assertThat(user.getNickname()).isEqualTo("뉴파쿠파쿠");
        assertThat(user.getPassword()).isEqualTo("new12341234");
    }

    @Test
    @DisplayName("사용자 권한 검증")
    void validateAdminRole_fail() {

        //given
        User user = User.builder()
                .userRole(UserRole.USER)
                        .build();
        //when

        //then
        assertThatThrownBy(
                () -> user.validateAdminRole()
        ).isInstanceOf(UserRoleValidateException.class);
    }
}