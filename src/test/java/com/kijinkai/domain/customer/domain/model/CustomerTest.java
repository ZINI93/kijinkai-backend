package com.kijinkai.domain.customer.domain.model;

import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerTest {



    @Test
    @DisplayName("고객 정보 업데이트")
    void customerUpdateTest(){


        //given
        Customer customer = Customer.builder()
                .firstName("JINHEE")
                .lastName("PARK")
                .phoneNumber("080-1234-1234")
                .build();


        CustomerUpdateDto customerUpdateDto = CustomerUpdateDto.builder()
                .firstName("wakaoji")
                .lastName("seiba")
                .phoneNumber("070-1111-111")
                .build();

        //when
        customer.updateCustomer(customerUpdateDto);

        //then
        assertThat(customer.getFirstName()).isEqualTo("wakaoji");
        assertThat(customer.getLastName()).isEqualTo("seiba");
        assertThat(customer.getPhoneNumber()).isEqualTo("070-1111-111");

    }



}