package com.kijinkai.domain.address.service;

import com.kijinkai.domain.address.dto.AddressRequestDto;
import com.kijinkai.domain.address.dto.AddressResponseDto;
import com.kijinkai.domain.address.dto.AddressUpdateDto;
import com.kijinkai.domain.address.repository.AddressRepository;

public interface AddressService {

    AddressResponseDto createAddressWithValidate(String userUuid, AddressRequestDto requestDto);
    AddressResponseDto updateAddressWithValidate(String userUuid, String addressUuid, AddressUpdateDto updateDto);
    AddressResponseDto getAddressInfo(String userUuid, String addressUuid);
    void deleteAddress(String userUuid, String addressUuid);
}
