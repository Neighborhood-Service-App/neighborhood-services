package com.neighborhoodservice.user.repository;

import com.neighborhoodservice.user.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
