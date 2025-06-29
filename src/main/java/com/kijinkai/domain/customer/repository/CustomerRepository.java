package com.kijinkai.domain.customer.repository;

import com.kijinkai.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryCustom {

    Optional<Customer> findByUserUserUuidAndCustomerUuid(UUID userUuid, UUID customerUuid);
    Optional<Customer> findByUserUserUuid(UUID userUuid);
}