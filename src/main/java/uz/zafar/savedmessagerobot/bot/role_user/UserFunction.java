package uz.zafar.savedmessagerobot.bot.role_user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.zafar.savedmessagerobot.bot.TelegramBot;
import uz.zafar.savedmessagerobot.db.domain.File;
import uz.zafar.savedmessagerobot.db.domain.FileUser;
import uz.zafar.savedmessagerobot.db.domain.User;
import uz.zafar.savedmessagerobot.db.repositories.FileRepository;
import uz.zafar.savedmessagerobot.db.service.FileUserService;
import uz.zafar.savedmessagerobot.db.service.UserService;
import uz.zafar.savedmessagerobot.dto.ResponseDto;
//import uz.zafar.ibratfarzandlari.bot.TelegramBot;
//import uz.zafar.ibratfarzandlari.db.domain.*;
//import uz.zafar.ibratfarzandlari.db.service.UserService;
//import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
@RequiredArgsConstructor
@Log4j2
public class UserFunction {
    private final TelegramBot bot;
    private final UserService userService;
    private final UserKyb kyb;
    private final FileUserService fileUserService;
    private final FileRepository fileRepository;

    public void start(User user) {
        if (user.getFileUserId() == null) {
            bot.sendMessage(user.getChatId(), "Quyidagilardan birini tanlang", kyb.registerLogin);
            user.setEventCode("choose login or register");
            userService.save(user);
        } else {
            user.setEventCode("menu");
            userService.save(user);
            bot.sendMessage(user.getChatId(), "Asosiy menyudasiz", kyb.menu);
        }
    }

    public void chooseLoginOrRegister(User user, String text) {
        if ("Yangi ochish".equals(text)) {
            bot.sendMessage(user.getChatId(), "Yangi profil ochish uchun emailingizni kiriting", kyb.back);
            user.setEventCode("get email for new profile");
        } else if (text.equals("Avvalgisiga ulanish")) {
            bot.sendMessage(user.getChatId(), "Profilga ulanish uchun emailingizni kiriting", kyb.back);
            user.setEventCode("connecting to profile for get email");
        } else {
            bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.registerLogin);
            return;
        }
        userService.save(user);
    }

    public void get_email_for_new_profile(User user, String text) {
        if (text.equals(kyb.backButton)) {
            start(user);
            return;
        }
        text = text.toLowerCase();
        ResponseDto<FileUser> fileUserDto = fileUserService.findByEmail(text);
        if (fileUserDto.isSuccess()) {
            bot.sendMessage(user.getChatId(), "Bu email band, iltimos boshqa email kiriting", kyb.back);
            return;
        }
        Integer code = getConfirmCode;
        user.setConfirmCode(code);
        ResponseDto<Void> sendEmail = userService.sendEmail(bot.botUsername + " tasdiqlash kod", "%s botiga kirish uchun tasdiqlash kodi %d".formatted(
                bot.botUsername, code
        ), text);
        if (!sendEmail.isSuccess()) {
            bot.sendMessage(user.getChatId(), "Kutilmagan xatolik, iltimos boshqa email kiriting", kyb.back);
            return;
        }
        bot.sendMessage(user.getChatId(), "%s \n\nUshbu emailga yuborilgan kodni kiriting".formatted(text), true);
        user.setHelperEmail(text.toLowerCase());
        user.setEventCode("confirm code for register email");
        userService.save(user);
    }

    private Integer getConfirmCode = new Random().ints(6, 0, 10).reduce(0, (a, b) -> a * 10 + b);

    public void confirm_code_for_register_email(User user, String text) {
        Integer code = Integer.valueOf(text);
        if (code.equals(user.getConfirmCode())) {
            FileUser fileUser = new FileUser();
            fileUser.setEmail(user.getHelperEmail());
            fileUser.setFiles(new ArrayList<>());
            fileUserService.save(fileUser);
            ResponseDto<FileUser> userEmail = fileUserService.findByEmail(user.getHelperEmail());
            if (userEmail.isSuccess()) {
                user.setFileUserId(userEmail.getData().getId());
            } else return;
            user.setEventCode("menu");
            userService.save(user);
            bot.sendMessage(user.getChatId(), "Asosiy menyudasiz", kyb.menu);
        } else {
            bot.sendMessage(user.getChatId(), "Kod noto'g'ri kiritildi, qaytadan kiriting");
        }
    }

    public void connecting_to_profile_for_get_email(User user, String text) {
        if (text.equals(kyb.backButton)){
            start(user);
            return;
        }
        text = text.toLowerCase();
        ResponseDto<FileUser> checkEmail = fileUserService.findByEmail(text);
        if (checkEmail.isSuccess()) {
            user.setHelperEmail(text.toLowerCase());
            Integer code = getConfirmCode;
            user.setConfirmCode(code);
            ResponseDto<Void> sendEmail = userService.sendEmail(bot.botUsername, "%s botiga kirish uchun tasdiqlash kodi: %d".formatted(
                    bot.botUsername, code
            ), text);
            if (!sendEmail.isSuccess()) {
                bot.sendMessage(user.getChatId(), "Kutilmagan xatolik, iltimos boshqa email kiritng");
                return;
            }
            bot.sendMessage(user.getChatId(), "%s emailiga tasdiqlash kodi yuborildi, o'sha koni kiriting".formatted(text), true);

            user.setEventCode("get confirm code for sign in");
            userService.save(user);
        } else
            bot.sendMessage(user.getChatId(), "Bunday emailga ega profil topilmadi, iltimos boshqa email kiriting", kyb.back);
    }

    public void get_confirm_code_for_sign_in(User user, String text) {
        Integer code = Integer.valueOf(text);
        if (code.equals(user.getConfirmCode())) {
            ResponseDto<FileUser> emailDto = fileUserService.findByEmail(user.getHelperEmail());
            if (!emailDto.isSuccess()) {
                bot.sendMessage(user.getChatId(), "Kutilmagan xatolik, /start tugmasini bosing");
                return;
            }
            user.setFileUserId(emailDto.getData().getId());
            user.setEventCode("menu");
            userService.save(user);
            bot.sendMessage(user.getChatId(), "Asosiy menyudasiz", kyb.menu);
        } else bot.sendMessage(user.getChatId(), "Kod noto'g'ri kiritildi, iltimos boshqa kod kiriting");
    }


    private void empty(User user){
        bot.sendMessage(user.getChatId(), "Bo'm bo'sh" , kyb.menu);
    }
    public void menu(User user, String text) {
        FileUser fileUser = fileUser(user);
        String[] menu = {
                "Barchasi", "\uD83C\uDFB5 Musiqalar", "\uD83D\uDCF7 Rasmlar",
                "\uD83D\uDCF9 Videolar", "\uD83D\uDCC3 Hujjatlar",
                "\uD83D\uDCDD Matnlar", "\uD83D\uDD04 Profilni o'zgartirish"
        };
        if (text.equals(menu[0])) {
            if (fileUser.getFiles().isEmpty()){
                bot.sendMessage(user.getChatId(), "Bo'm bo'sh" , kyb.menu);
                return;
            }
            for (File file : fileUser.getFiles()) {
                try {
                    switch (file.getType()) {
                        case "text" -> bot.sendMessage(user.getChatId(), file.getText(), kyb.aboutFile(file.getId()));
                        case "audio" -> {
                            SendAudio sendAudio = new SendAudio();
                            sendAudio.setAudio(new InputFile(file.getFileId()));
                            sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                            sendAudio.setChatId(user.getChatId());
                            bot.execute(sendAudio);
                        }
                        case "photo" -> {
                            SendPhoto sendAudio = new SendPhoto();
                            sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                            sendAudio.setPhoto(new InputFile(file.getFileId()));
                            sendAudio.setChatId(user.getChatId());
                            bot.execute(sendAudio);
                        }
                        case "video" -> {
                            SendVideo sendAudio = new SendVideo();
                            sendAudio.setVideo(new InputFile(file.getFileId()));
                            sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                            sendAudio.setChatId(user.getChatId());
                            bot.execute(sendAudio);
                        }
                        case "document" -> {
                            SendDocument sendAudio = new SendDocument();
                            sendAudio.setDocument(new InputFile(file.getFileId()));
                            sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                            sendAudio.setChatId(user.getChatId());
                            bot.execute(sendAudio);
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
        else if (text.equals(menu[1])) {
            List<File> list = fileUser.getFiles().stream().filter(file -> file.getType().equals("audio")).toList();
            if (list.isEmpty()) {
                empty(user);return;
            }
            for (File file : list) {
                try {
                    SendAudio sendAudio = new SendAudio();
                    sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                    sendAudio.setAudio(new InputFile(file.getFileId()));
                    sendAudio.setChatId(user.getChatId());
                    bot.execute(sendAudio);
                } catch (TelegramApiException e) {
                    log.error(e);
                }
            }
        } else if (text.equals(menu[2])) {
            try {
                List<File> list = fileUser.getFiles().stream().filter(file -> file.getType().equals("photo")).toList();
                if (list.isEmpty()){
                    empty(user);
                    return;
                }
                for (File file : list) {
                    SendPhoto sendAudio = new SendPhoto();
                    sendAudio.setPhoto(new InputFile(file.getFileId()));
                    sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                    sendAudio.setChatId(user.getChatId());
                    bot.execute(sendAudio);
                }

            } catch (TelegramApiException e) {
                log.error(e);
            }
        } else if (text.equals(menu[3])) {
            try {
                List<File> list = fileUser.getFiles().stream().filter(file -> file.getType().equals("video")).toList();
                if (list.isEmpty()){
                    empty(user);
                    return;
                }
                for (File file : list) {
                    SendVideo sendAudio = new SendVideo();
                    sendAudio.setVideo(new InputFile(file.getFileId()));
                    sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                    sendAudio.setChatId(user.getChatId());
                    bot.execute(sendAudio);
                }
            } catch (TelegramApiException e) {
                log.error(e);
            }
        } else if (text.equals(menu[4])) {
            try {
                List<File> list = fileUser.getFiles().stream().filter(file -> file.getType().equals("document")).toList();
                if (list.isEmpty()){
                    empty(user);
                    return;
                }
                for (File file : list) {
                    SendDocument sendAudio = new SendDocument();
                    sendAudio.setDocument(new InputFile(file.getFileId()));
                    sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                    sendAudio.setChatId(user.getChatId());
                    bot.execute(sendAudio);
                }

            } catch (Exception e) {
                log.error(e);
            }
        } else if (text.equals(menu[5])) {
            List<File> list = fileUser.getFiles().stream().filter(file -> file.getType().equals("text")).toList();
            if (list.isEmpty()){
                empty(user);
                return;
            }
            for (File file : list) {
                bot.sendMessage(user.getChatId(), file.getText(), kyb.aboutFile(file.getId()));
            }
        } else if (text.equals(menu[6])) {
            user.setFileUserId(null);
            user.setHelperEmail(null);
            userService.save(user);
            start(user);
        } else {
            addSavedMessages(user, "text", text);
        }
    }

    public void addSavedMessages(User user, String type, String fileId) {
        File file = new File();
        if (type.equals("text")) file.setText(fileId);
        else file.setFileId(fileId);
        file.setActive(true);
        file.setType(type);
        file.setFileUser(fileUser(user));
        ResponseDto<User> saveFile = userService.saveFile(file);
        bot.sendMessage(user.getChatId(), saveFile.isSuccess() ? "Muvaffaqiyatli qo'shildi" : "Nimagadir qo'shilmadi");
        start(user);
    }

    private FileUser fileUser(User user) {
        return fileUserService.findById(user.getFileUserId()).getData();
    }


    private File file(Long fileId) {
        Optional<File> fOp = fileRepository.findById(fileId);
        return fOp.orElse(null);
    }

    public void menu(User user, String data, CallbackQuery callbackQuery, Integer messageId) {
        if (data.equals("delete-chat")) {
            try {
                bot.execute(DeleteMessage.builder().chatId(user.getChatId()).messageId(messageId).build());
                return;
            } catch (Exception e) {
                log.error(e);
            }
        } else {
            File file = file(Long.valueOf(data));
            file.setActive(false);
            fileRepository.save(file);
            bot.alertMessage(callbackQuery,"Muvaffaqiyatli o'chirildi");
            try {
                bot.execute(DeleteMessage.builder().chatId(user.getChatId()).messageId(messageId).build());
                return;
            } catch (Exception e) {
                log.error(e);
            }

        }
    }

@SneakyThrows
    public void help(User user) {
        /*examplet069@gmail.com */
        bot.execute(
                new SendVideo(""+user.getChatId() , new InputFile("https://t.me/saved_message_robot_channel/4"))
        );
    }
}
