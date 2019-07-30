package demo.fabric.repository;

import demo.fabric.entity.FabricUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Fabric user repository
 */
public interface FabricUserRepository extends JpaRepository<FabricUserEntity, Long> {

}
