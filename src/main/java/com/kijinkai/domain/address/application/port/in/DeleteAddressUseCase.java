package com.kijinkai.domain.address.application.port.in;

import java.util.UUID;

public interface DeleteAddressUseCase {
    void deleteAddress(UUID userUuid, UUID addressUuid);

}
