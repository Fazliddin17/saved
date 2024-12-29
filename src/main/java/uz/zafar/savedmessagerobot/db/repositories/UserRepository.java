package uz.zafar.savedmessagerobot.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.savedmessagerobot.db.domain.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import uz.zafar.ibratfarzandlari.db.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByOrderByIdDesc() ;
    User findByChatId(Long chatId);

    List<User> findAllByRoleOrderByIdDesc(String role);
    Page<User>findAllByOrderByIdDesc(Pageable pageable);
}
