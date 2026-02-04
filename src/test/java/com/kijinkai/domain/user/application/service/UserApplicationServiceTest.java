//package com.kijinkai.domain.user.application.service;
//
//import com.kijinkai.domain.user.application.dto.request.UserRequestDto;
//import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
//import com.kijinkai.domain.user.application.dto.request.UserUpdateDto;
//import com.kijinkai.domain.user.application.mapper.UserMapper;
//import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
//import com.kijinkai.domain.user.application.validator.UserValidator;
//import com.kijinkai.domain.user.domain.factory.UserFactory;
//import com.kijinkai.domain.user.domain.model.User;
//import com.kijinkai.domain.user.domain.model.UserRole;
//import com.kijinkai.domain.user.domain.model.UserStatus;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserApplicationServiceTest {
//
//    @InjectMocks UserApplicationService userApplicationService;
//
//    @Mock UserPersistencePort userPersistencePort;
//    @Mock PasswordEncoder passwordEncoder;
//    @Mock UserFactory userFactory;
//    @Mock UserMapper userMapper;
//    @Mock UserValidator userValidator;
//
//
//    User user;
//    UserRequestDto userRequestDto;
//    UserResponseDto userResponseDto;
//
//    @BeforeEach
//    void setUp(){
//
//        userRequestDto = UserRequestDto.builder()
//                .email("pakupaku@gmail.com")
//                .password("12341234")
//                .nickname("파쿠파쿠짱")
//                .build();
//
//        user = User.builder()
//                .userUuid(UUID.randomUUID())
//                .email("pakupaku@gmail.com")
//                .password(userRequestDto.getPassword())
//                .nickname(userRequestDto.getNickname())
//                .userRole(UserRole.USER)
//                .userStatus(UserStatus.ACTIVE)
//                .build();
//
//
//        userResponseDto = UserResponseDto
//                .builder()
//                .userUuid(user.getUserUuid())
//                .email(user.getEmail())
//                .nickname(user.getNickname())
//                .build();
//
//    }
//
////    @Test
////    @DisplayName("이메일 중복 체크")
////    void existEmail(){
////        //given
////
////        // 중복이 참일때
////        when(userPersistencePort.existsByEmail(user.getEmail())).thenReturn(true);
////
////        //when
////        Boolean result = userApplicationService.existEmailByUser(user.getEmail());
////
////        //then
////        assertThat(result).isNotNull();
////        assertThat(result).isEqualTo(true);
////
////        verify(userPersistencePort, times(1)).existsByEmail(user.getEmail());
////    }
//
//
//
////    @Test
////    @DisplayName("회원가입")
////    void createUser() {
////        //given
////        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
////
////        when(userPersistencePort.existsByEmail(user.getEmail())).thenReturn(false);
////        when(passwordEncoder.encode("12341234")).thenReturn(encodedPassword);
////        when(userFactory.createUser(userRequestDto, encodedPassword)).thenReturn(user);
////        when(userPersistencePort.saveUser(any(User.class))).thenReturn(user);
////        when(userMapper.toResponse(user)).thenReturn(userResponseDto);
////
////        //when
////        UserResponseDto result = userApplicationService.createUser(userRequestDto);
////
////        //then
////        assertThat(result).isNotNull();
////        assertThat(result.getUserUuid()).isEqualTo(user.getUserUuid());
////        assertThat(result.getNickname()).isEqualTo(userRequestDto.getNickname());
////        assertThat(result.getEmail()).isEqualTo(userRequestDto.getEmail());
////
////        verify(userValidator).validateCreateUserRequest(userRequestDto);
////        verify(userPersistencePort,times(1)).saveUser(any(User.class));
////    }
//
//    @Test
//    @DisplayName("유저 - 정보 조회")
//    void getUserInfo() {
//
//        //given
//        when(userPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(user));
//        when(userMapper.toResponse(user)).thenReturn(userResponseDto);
//
//        //when
//        UserResponseDto result = userApplicationService.getUserInfo(user.getUserUuid());
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getUserUuid()).isEqualTo(user.getUserUuid());
//        assertThat(result.getNickname()).isEqualTo(user.getNickname());
//
//        verify(userPersistencePort,times(1)).findByUserUuid(user.getUserUuid());
//
//    }
//
//    @Test
//    @DisplayName("관리자 - 사용자 정보 리스트 조회")
//    void findAllByEmailAndNickName() {
//        //given
//        User admin = User.builder()
//                .userUuid(UUID.randomUUID())
//                .email("pakupaku@gmail.com")
//                .password(userRequestDto.getPassword())
//                .nickname(userRequestDto.getNickname())
//                .userRole(UserRole.ADMIN)
//                .userStatus(UserStatus.ACTIVE)
//                .build();
//
//        PageRequest pageable = PageRequest.of(0, 10);
//        List<User> mockData = List.of(admin);
//        PageImpl<User> mockPage = new PageImpl<>(mockData, pageable, mockData.size());
//
//        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
//        when(userPersistencePort.findAllByEmailAndNickName(admin.getEmail(), admin.getNickname(), pageable)).thenReturn(mockPage);
//        when(userMapper.toResponse(any(User.class))).thenReturn(userResponseDto);
//
//        //when
//        Page<UserResponseDto> result = userApplicationService.findAllByEmailAndNickName(admin.getUserUuid(), admin.getEmail(), admin.getNickname(), pageable);
//
//        //then
//        assertThat(result.getTotalElements()).isEqualTo(1);
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getEmail()).isEqualTo("pakupaku@gmail.com");
//
//
//        verify(userPersistencePort, times(1)).findAllByEmailAndNickName(admin.getEmail(), admin.getNickname(), pageable);
//    }
//
//    @Test
//    @DisplayName("회원 프로필 수정")
//    void updateUserProfile() {
//        //given
//        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
//                .newPassword("new12341234")
//                .nickname("newKUKUKAKA")
//                .build();
//
//        UserResponseDto updateResponse = UserResponseDto
//                .builder()
//                .userUuid(user.getUserUuid())
//                .email(user.getEmail())
//                .nickname(userUpdateDto.getNickname())
//                .build();
//
//        String encodedPassword = passwordEncoder.encode(userUpdateDto.getNewPassword());
//
//        when(userPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(user));
//        when(passwordEncoder.encode("new12341234")).thenReturn(encodedPassword);
//        when(userPersistencePort.saveUser(any(User.class))).thenReturn(user);
//        when(userMapper.toResponse(user)).thenReturn(updateResponse);
//
//        //when
//        UserResponseDto result = userApplicationService.updateUserProfile(user.getUserUuid(), userUpdateDto);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getNickname()).isEqualTo(userUpdateDto.getNickname());
//
//        verify(userPersistencePort,times(1)).saveUser(any(User.class));
//    }
//
//    @Test
//    void updateUserPassword() {
//
//        //given
//        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
//                .newPassword("new12341234")
//                .nickname("newKUKUKAKA")
//                .build();
//
//        UserResponseDto updateResponse = UserResponseDto
//                .builder()
//                .userUuid(user.getUserUuid())
//                .email(user.getEmail())
//                .nickname(userUpdateDto.getNickname())
//                .build();
//
//        String encodedPassword = passwordEncoder.encode(userUpdateDto.getNewPassword());
//
//        when(userPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(user));
//        when(passwordEncoder.encode("new12341234")).thenReturn(encodedPassword);
//        when(userPersistencePort.saveUser(any(User.class))).thenReturn(user);
//        when(userMapper.toResponse(user)).thenReturn(updateResponse);
//
//        //when
//        UserResponseDto result = userApplicationService.updateUserPassword(user.getUserUuid(), userUpdateDto);
//
//        //then
//        assertThat(result).isNotNull();
//        verify(userPersistencePort,times(1)).saveUser(any(User.class));
//    }
//
////    @Test
////    void deleteUser() {
////        //given
////        when(userPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(user));
////
////        //when
////        userApplicationService.deleteUser(user.getUserUuid());
////
////        //then
////    }
//
//}