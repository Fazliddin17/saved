package uz.zafar.savedmessagerobot.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileId;
    private Boolean active;
    @Column(columnDefinition = "TEXT")
    private String text;
    private String type;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "file_user_id", nullable = false)
    private FileUser fileUser;
}
