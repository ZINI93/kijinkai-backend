package com.kijinkai.domain.customer.adapter.out.persistence.mapper;


import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerPersistenceMapper {

    Customer toCustomer(CustomerJpaEntity customerJpaEntity);
    CustomerJpaEntity toCustomerJpaEntity(Customer customer);

    List<Customer> toCustomerList(List<CustomerJpaEntity> jpaEntities);
    List<CustomerJpaEntity> toCustomerJpaEntityInList(List<Customer> customers);

}
