package uz.zafar.savedmessagerobot.db.service;

import org.springframework.data.domain.Page;
import uz.zafar.savedmessagerobot.db.domain.File;
import uz.zafar.savedmessagerobot.db.domain.User;
import uz.zafar.savedmessagerobot.dto.ResponseDto;

import java.util.List;

public interface UserService {
    ResponseDto<User> checkUser(Long chatId);
    ResponseDto<User> findById(Long userId) ;
    ResponseDto<Void> save(User user) ;
    ResponseDto<Void> sendEmail(String subject, String text, String to);
    ResponseDto<Page<User>> findAll(int page, int size);
    ResponseDto<User> saveFile(File file);
    ResponseDto<List<User>>findAll();
}
