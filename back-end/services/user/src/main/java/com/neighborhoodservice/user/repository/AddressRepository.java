package com.neighborhoodservice.user.repository;

import com.neighborhoodservice.user.model.Address;
import com.neighborhoodservice.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByUser(User user);
}
