package com.kijinkai.domain.address.application.port.in;

import com.kijinkai.domain.address.application.dto.AddressResponseDto;
import com.kijinkai.domain.address.application.dto.AddressUpdateDto;

import java.util.UUID;

public interface UpdateAddressUseCase {
    AddressResponseDto updateAddress(UUID userUuid, UUID addressUuid, AddressUpdateDto updateDto);
}
