package com.playblog.userservice.neighbor.Repository;

import com.playblog.userservice.neighbor.Entity.Neighbor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeighborRepository extends JpaRepository<Neighbor,Long> {

}
