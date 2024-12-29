package uz.zafar.savedmessagerobot.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Kyb {
    public ReplyKeyboardMarkup markup(List<KeyboardRow> r) {
        return ReplyKeyboardMarkup.builder().selective(true).resizeKeyboard(true).keyboard(r).build();
    }
    public ReplyKeyboardMarkup setKeyboards(String[] words, int size) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            button = new KeyboardButton();
            button.setText(words[i]);
            row.add(button);
            if ((i + 1) % size == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);
        return markup;
    }
    public ReplyKeyboardMarkup requestContact(String word) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        button = new KeyboardButton();
        button.setText(word);
        button.setRequestContact(true);
        row.add(button);
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);
        return markup;
    }


    public InlineKeyboardButton createButton(String text, String data) {
        return InlineKeyboardButton.builder().callbackData(data).text(text).build();
    }

    public InlineKeyboardButton createButton(String text, Long data) {
        return InlineKeyboardButton.builder().callbackData(String.valueOf(data)).text(text).build();
    }

}
