package com.community.zerobase.repository;

import com.community.zerobase.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

  Optional<Users> findByEmail(String email);

  boolean existsByEmail(String email);

  List<Users> findByEmailContaining(String email);
}
