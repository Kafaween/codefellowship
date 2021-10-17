package com.example.codefellowship.repositories;

import com.example.codefellowship.models.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser,Long> {
    ApplicationUser findApplicationUserByUsername(String username);
}
