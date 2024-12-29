package uz.zafar.savedmessagerobot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.savedmessagerobot.db.domain.File;
//import uz.zafar.ibratfarzandlari.db.domain.File;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long> {

}
