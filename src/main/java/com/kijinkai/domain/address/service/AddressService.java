package com.kijinkai.domain.address.service;

import com.kijinkai.domain.address.dto.AddressRequestDto;
import com.kijinkai.domain.address.dto.AddressResponseDto;
import com.kijinkai.domain.address.dto.AddressUpdateDto;
import com.kijinkai.domain.address.repository.AddressRepository;

import java.util.UUID;

public interface AddressService {

    AddressResponseDto createAddressWithValidate(UUID userUuid, AddressRequestDto requestDto);
    AddressResponseDto updateAddressWithValidate(UUID userUuid, UUID addressUuid, AddressUpdateDto updateDto);
    AddressResponseDto getAddressInfo(UUID userUuid, UUID addressUuid);
    void deleteAddress(UUID userUuid, UUID addressUuid);
}
