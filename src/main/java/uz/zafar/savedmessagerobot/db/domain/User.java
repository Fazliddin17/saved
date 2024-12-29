package uz.zafar.savedmessagerobot.db.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long fileUserId;
    private String firstname;
    private String lastname;
    private String username;
    @Column(unique = true)
    private Long chatId;
    private String phone;
    private String role;
    private Integer page;
    private String eventCode;
    private Boolean active;
    private String helperEmail;
    private Integer confirmCode;
    private Long helperUserId;
}
