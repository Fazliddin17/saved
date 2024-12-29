package uz.zafar.savedmessagerobot.db.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uz.zafar.savedmessagerobot.db.domain.FileUser;
import uz.zafar.savedmessagerobot.db.repositories.FileUserRepository;
import uz.zafar.savedmessagerobot.db.service.FileUserService;
import uz.zafar.savedmessagerobot.dto.ResponseDto;

import java.io.FileNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class FileUserServiceImpl implements FileUserService {
    private final FileUserRepository fileUserRepository;

    @Override
    public ResponseDto<Void> save(FileUser fileUser) {
        try {
            fileUserRepository.save(fileUser);
            return new ResponseDto<>(true , "Ok") ;
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<FileUser> findById(Long fileUserId) {
        try {
            Optional<FileUser> fOp = fileUserRepository.findById(fileUserId);
            if (fOp.isPresent()) {
                return new ResponseDto<>(true, "Ok", fOp.get()) ;
            }
            throw new FileNotFoundException("File not found");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false ,e.getMessage());
        }
    }

    @Override
    public ResponseDto<FileUser> findByEmail(String email) {
        try {
            Optional<FileUser> byEmail = fileUserRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                return new ResponseDto<>(true , "Ok" , byEmail.get());
            }
            throw new Exception("Not found file user");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
