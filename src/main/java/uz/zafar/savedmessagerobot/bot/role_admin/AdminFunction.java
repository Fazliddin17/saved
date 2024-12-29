package uz.zafar.savedmessagerobot.bot.role_admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.zafar.savedmessagerobot.bot.TelegramBot;
import uz.zafar.savedmessagerobot.db.domain.File;
import uz.zafar.savedmessagerobot.db.domain.FileUser;
import uz.zafar.savedmessagerobot.db.domain.User;
import uz.zafar.savedmessagerobot.db.service.FileUserService;
import uz.zafar.savedmessagerobot.db.service.UserService;
import uz.zafar.savedmessagerobot.dto.ResponseDto;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class AdminFunction {
    private final TelegramBot bot;
    private final UserService userService;
    private final AdminKyb kyb;
    private final FileUserService fileUserService;

    public void start(User user) {
        bot.sendMessage(user.getChatId(), "Asosiy menyudasiz, o'zingizga kerakli manyulardan birini tanlang", kyb.menu);
        user.setEventCode("menu");
        userService.save(user);
    }

    private void eventCode(User user, String eventCode) {
        user.setEventCode(eventCode);
        userService.save(user);
    }

    public void menu(User user, String text, int size) {
        String[] texts = {
                "Foydalanuvchilar bo'limi", "Reklama bo'limi"
        };
        user.setPage(0);
        userService.save(user);
        Integer page = user.getPage();
        if (text.equals(texts[0])) {
            ResponseDto<Page<User>> all = userService.findAll(page, size);
            bot.sendMessage(user.getChatId(), "Barcha foydalanuvchilarning ro'yxati", kyb.setUsers(all.getData().getContent()));
        } else if (text.equals(texts[1])) {
            bot.sendMessage(user.getChatId(), "Menga reklamangizni yuboring", kyb.setKeyboards(new String[]{"\uD83D\uDD19 Orqaga"},1));
            user.setEventCode("reklama");
            userService.save(user);
        } else bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.menu);
    }

    private FileUser fileUser(User user) {
        return fileUserService.findById(user.getFileUserId()).getData();
    }
    public void menu(User user, String data, CallbackQuery callbackQuery, Integer messageId, int size) {
        Integer page = user.getPage();
        Page<User> users = userService.findAll(page, size).getData();
        if (data.equals("back")) {
            if (users.isFirst()) {
                bot.alertMessage(callbackQuery, "Siz allaachon birinchi sahifadasiz");
                return;
            }
            try {
                users = userService.findAll(page - 1, size).getData();
                bot.execute(EditMessageText
                        .builder()
                        .messageId(messageId)
                        .chatId(user.getChatId())
                        .text("Barcha foydalanuvchilarning ro'yxati")
                        .replyMarkup(kyb.setUsers(users.getContent()))
                        .build());
                user.setPage(page - 1);
                userService.save(user);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        } else if (data.equals("active")) {
            ResponseDto<User> checkUser = userService.findById(user.getHelperUserId());
            if (!checkUser.isSuccess()){
                bot.alertMessage(callbackQuery , checkUser.getMessage());
                return;
            }
            User data1 = checkUser.getData();
            data1.setActive(false);
            userService.save(data1);
            bot.alertMessage(callbackQuery, "Muvaffaqiyatli bloklandi");
            String caption = """
                    Ushbu foydalanuvchining ma'lumotlari
                                        
                    ID: %d
                    CHAT ID: %d
                    firstname: %s
                    lastname: %s
                    username @%s
                    holati: %s
                    """.formatted(
                    data1.getFileUserId(), data1.getChatId(),
                    data1.getFirstname(), data1.getLastname(), data1.getUsername(),
                    data1.getActive() ? "Faol" : "Bloklangan"
            );
            try {
                bot.execute(EditMessageText
                        .builder()
                        .messageId(messageId)
                        .chatId(user.getChatId())
                        .text(caption)
                        .replyMarkup(kyb.editUser(data1.getActive()))
                        .build());
            } catch (Exception e) {
                log.error(e);
            }

        } else if (data.equals("inactive")) {
            User data1 = userService.findById(user.getHelperUserId()).getData();
            data1.setActive(true);
            userService.save(data1);
            bot.alertMessage(callbackQuery, "Muvaffaqiyatli blokdan chiqarildi");
            String caption = """
                    Ushbu foydalanuvchining ma'lumotlari
                                        
                    ID: %d
                    CHAT ID: %d
                    firstname: %s
                    lastname: %s
                    username @%s
                    holati: %s
                    """.formatted(
                    data1.getFileUserId(), data1.getChatId(),
                    data1.getFirstname(), data1.getLastname(), data1.getUsername(),
                    data1.getActive() ? "Faol" : "Bloklangan"
            );
            try {
                bot.execute(EditMessageText
                        .builder()
                        .messageId(messageId)
                        .chatId(user.getChatId())
                        .text(caption)
                        .replyMarkup(kyb.editUser(data1.getActive()))
                        .build());
            } catch (Exception e) {
                log.error(e);
            }

        } else if (data.equals("to back")) {
            ResponseDto<Page<User>> all = userService.findAll(page, size);
            try {
                bot.execute(
                        EditMessageText
                                .builder()
                                .chatId(user.getChatId())
                                .messageId(messageId)
                                .replyMarkup(kyb.setUsers(all.getData().getContent()))
                                .text("Barcha foydalanuvchilarning ro'yxati")
                                .build()
                );
            } catch (Exception e) {
                log.error(e);
            }
        } else if (data.equals("see messages")) {
            for (File file : fileUser(userService.findById(user.getHelperUserId()).getData()).getFiles1()) {
                switch (file.getType()) {
                    case "text" -> bot.sendMessage(user.getChatId(), file.getText());
                    case "audio" -> {
                        SendAudio sendAudio = new SendAudio();
                        sendAudio.setAudio(new InputFile(file.getFileId()));
//                        sendAudio.setReplyMarkup(kyb.aboutFile(file.getId()));
                        sendAudio.setChatId(user.getChatId());
                        try {
                            bot.execute(sendAudio);
                        } catch (TelegramApiException e) {
                            log.error(e);
                        }
                    }
                    case "photo" -> {
                        SendPhoto sendAudio = new SendPhoto();

                        sendAudio.setPhoto(new InputFile(file.getFileId()));
                        sendAudio.setChatId(user.getChatId());
                        try {
                            bot.execute(sendAudio);
                        } catch (TelegramApiException e) {
                            log.error(e);
                        }
                    }
                    case "video" -> {
                        SendVideo sendAudio = new SendVideo();
                        sendAudio.setVideo(new InputFile(file.getFileId()));

                        sendAudio.setChatId(user.getChatId());
                        try {
                            bot.execute(sendAudio);
                        } catch (TelegramApiException e) {
                            log.error(e);
                        }
                    }
                    case "document" -> {
                        SendDocument sendAudio = new SendDocument();
                        sendAudio.setDocument(new InputFile(file.getFileId()));
                        sendAudio.setChatId(user.getChatId());
                        try {
                            bot.execute(sendAudio);
                        } catch (TelegramApiException e) {
                            log.error(e);
                        }
                    }
                }
            }
            menu(user , "Foydalanuvchilar bo'limi" , size);
        } else if (data.equals("next")) {
            if (users.isLast()) {
                bot.alertMessage(callbackQuery, "Siz allaqachon oxirgi sahifadasiz");
                return;
            }
            try {
                users = userService.findAll(page + 1, size).getData();
                bot.execute(EditMessageText
                        .builder()
                        .messageId(messageId)
                        .chatId(user.getChatId())
                        .text("Barcha foydalanuvchilarning ro'yxati")
                        .replyMarkup(kyb.setUsers(users.getContent()))
                        .build());
                user.setPage(page + 1);
                userService.save(user);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        } else {
            Long userId = Long.valueOf(data);
            User user1 = userService.findById(userId).getData();
            user.setHelperUserId(userId);
            ResponseDto<Void> save = userService.save(user);
            if (!save.isSuccess())bot.sendMessage(user.getChatId(), save.getMessage());
            String caption = """
                    Ushbu foydalanuvchining ma'lumotlari

                    ID: %d
                    CHAT ID: %d
                    firstname: %s
                    lastname: %s
                    username @%s
                    holati: %s
                    """.formatted(
                    user1.getId(), user1.getChatId(),
                    user1.getFirstname(), user1.getLastname(), user1.getUsername(),
                    user1.getActive() ? "Faol" : "Bloklangan"
            );
            try {
                bot.execute(EditMessageText
                        .builder()
                        .messageId(messageId)
                        .chatId(user.getChatId())
                        .text(caption)
                        .replyMarkup(kyb.editUser(user1.getActive()))
                        .build());
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    public void copyMessage(User user, Message message) {
        Long chatId = user.getChatId();
        List<User> users = userService.findAll().getData();
        int count = 0 ;
        for (User data : users) {
            try {
                CopyMessage copyMessage = new CopyMessage(""+data.getChatId(),""+user.getChatId(),message.getMessageId());
                bot.execute(copyMessage);
                count ++ ;
            } catch (Exception e) {
                log.error(e);

            }
        }
        bot.sendMessage(chatId , "Xabaringiz %d kishidan %d kishiga muvaffaqiyatli yuborildi, %d kishiga yuborilmadi".formatted(
                users.size() , count , users.size() - count
        ));
        start(user);
    }
}
