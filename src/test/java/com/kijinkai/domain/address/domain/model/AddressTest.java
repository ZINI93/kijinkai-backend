package com.kijinkai.domain.address.domain.model;

import com.kijinkai.domain.address.application.dto.AddressUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;



@ExtendWith(MockitoExtension.class)
class AddressTest {


    @Test
    @DisplayName("주소 업데이트")
    void addressUpdate(){
        //given
        Address address = Address.builder()
                .recipientName("kakaopark")
                .recipientPhoneNumber("070-1111-1111")
                .country("korea")
                .zipcode("1111-1111")
                .state("대구광역시")
                .city("동구")
                .street("신암동")
                .isDefault(Boolean.TRUE)
                .build();


        AddressUpdateDto updatedto = AddressUpdateDto.builder()
                .recipientName("kakaopark")
                .recipientPhoneNumber("070-1111-1111")
                .country("korea")
                .zipcode("1111-1111")
                .state("대구광역시")
                .city("동구")
                .street("신암동")
                .build();


        //when
        address.updateAddress(updatedto);

        //then
        assertThat(address.getRecipientName()).isEqualTo(updatedto.getRecipientName());
        assertThat(address.getZipcode()).isEqualTo(updatedto.getZipcode());
        assertThat(address.getCity()).isEqualTo(updatedto.getCity());

    }



}