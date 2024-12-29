package uz.zafar.savedmessagerobot.db.service;

import uz.zafar.savedmessagerobot.db.domain.FileUser;
import uz.zafar.savedmessagerobot.dto.ResponseDto;

import java.util.List;

public interface FileUserService {
    ResponseDto<FileUser>findByEmail(String email);
    ResponseDto<FileUser>findById(Long fileUserId);
    ResponseDto<Void>save(FileUser fileUser);

/*
    ResponseDto<List<File>> getAllUserPhotos(Long fileUserId);

    ResponseDto<List<File>> getAllUserDocuments(Long fileUserId);

    ResponseDto<List<File>> getAllUserVideos(Long fileUserId);

    ResponseDto<List<File>> getAllUser(Long fileUserId);

    ResponseDto<List<File>> getAllUserTexts(Long fileUserId);
    ResponseDto<List<File>> getAllUserMusics(Long fileUserId);
*/

}
