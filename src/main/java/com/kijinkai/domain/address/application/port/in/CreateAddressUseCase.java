package com.kijinkai.domain.address.application.port.in;

import com.kijinkai.domain.address.application.dto.AddressRequestDto;
import com.kijinkai.domain.address.application.dto.AddressResponseDto;

import java.util.UUID;

public interface CreateAddressUseCase {

    AddressResponseDto createAddress(UUID userUuid, AddressRequestDto requestDto);

}
