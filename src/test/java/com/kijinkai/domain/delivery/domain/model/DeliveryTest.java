//package com.kijinkai.domain.delivery.domain.model;
//
//import com.kijinkai.domain.delivery.application.dto.DeliveryUpdateDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//
//import static org.assertj.core.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class DeliveryTest {
//
//    @Test
//    @DisplayName("고객 주소 업데이트")
//    void deliveryUpdate(){
//
//        //given
//        Delivery delivery = Delivery.builder()
//                .recipientName("JinheePark")
//                .recipientPhoneNumber("080-1111-1111")
//                .country("japan")
//                .zipcode("123-123")
//                .state("스기나미구")
//                .city("도쿄도")
//                .street("시라사키1-1-1")
//                .carrier(Carrier.YAMATO)
//                .trackingNumber("123123123")
//                .deliveryFee(new BigDecimal(10000.00))
//                .build();
//
//        DeliveryUpdateDto deliveryUpdateDto = DeliveryUpdateDto.builder()
//                .recipientName("kokopark")
//                .recipientPhoneNumber("070-1111-1111")
//                .country("korea")
//                .zipcode("123-123")
//                .state("동구")
//                .city("대구광역시")
//                .street("신암동1-1-1")
//                .carrier(Carrier.YAMATO)
//                .trackingNumber("222222")
//                .deliveryFee(new BigDecimal(20000.00))
//                .build();
//
//        //when
//        delivery.updateDelivery(deliveryUpdateDto);
//
//        //then
//        assertThat(delivery.getRecipientPhoneNumber()).isEqualTo(deliveryUpdateDto.getRecipientPhoneNumber());
//        assertThat(delivery.getCountry()).isEqualTo(deliveryUpdateDto.getCountry());
//
//
//    }
//
//}