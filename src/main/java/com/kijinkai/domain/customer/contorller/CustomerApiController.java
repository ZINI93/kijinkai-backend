package com.kijinkai.domain.customer.contorller;


import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.service.CustomerService;
import com.kijinkai.domain.user.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;

    @PostMapping("/join")
    public ResponseEntity<CustomerResponseDto> createCustomer(Authentication authentication,
                                                              @Valid @RequestBody CustomerRequestDto customerRequestDto){
        UUID userUuid = getUserUuid(authentication);

        CustomerResponseDto customer = customerService.createCustomerWithValidate(userUuid, customerRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/customers/{customerUuid}")
                .buildAndExpand(customer.getCustomerUuid())
                .toUri();

        return ResponseEntity.created(location).body(customer);
    }

    @PutMapping("/{customerUuid}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable String customerUuid,
                                                              Authentication authentication,
                                                              @Valid @RequestBody CustomerUpdateDto customerUpdateDto){
        UUID userUuid = getUserUuid(authentication);

        CustomerResponseDto customer = customerService.updateCustomerWithValidate(userUuid, UUID.fromString(customerUuid), customerUpdateDto);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponseDto> getCustomerInfo(Authentication authentication){
        UUID userUuid = getUserUuid(authentication);

        CustomerResponseDto customer = customerService.getCustomerInfo(userUuid);

        return ResponseEntity.ok(customer);
    }

    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }
}
