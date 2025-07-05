package com.playblog.userservice.neighbor.Repository;

import com.playblog.userservice.neighbor.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
