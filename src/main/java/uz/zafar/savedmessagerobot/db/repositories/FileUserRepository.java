package uz.zafar.savedmessagerobot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.savedmessagerobot.db.domain.FileUser;

import java.util.Optional;

public interface FileUserRepository extends JpaRepository<FileUser, Long> {
    Optional<FileUser> findByEmail(String email);
}
