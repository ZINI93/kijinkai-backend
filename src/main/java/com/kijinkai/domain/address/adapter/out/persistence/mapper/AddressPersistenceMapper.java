package com.kijinkai.domain.address.adapter.out.persistence.mapper;

import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import com.kijinkai.domain.address.domain.model.Address;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressPersistenceMapper {

    Address toAddress(AddressJpaEntity addressJpaEntity);
    AddressJpaEntity toAddressJpaEntity(Address address);


    //리스트 맵핑
    List<Address> toAddressInList(List<AddressJpaEntity> jpaEntities);
    List<AddressJpaEntity> toAddressJpaInList(List<Address> addresses);
}
