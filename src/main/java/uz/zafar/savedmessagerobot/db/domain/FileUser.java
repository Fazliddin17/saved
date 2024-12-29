package uz.zafar.savedmessagerobot.db.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class FileUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false , unique = true)
    private String email;
    private String password;
    @OneToMany(mappedBy = "fileUser", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<File> files;
    @Transient
    public List<File> getFiles() {
        return this.files.stream()
                .filter(File::getActive)
                .collect(Collectors.toList());
    }
    public List<File> getFiles1() {
        return this.files.stream()
                .filter(File::getActive)
                .collect(Collectors.toList());
    }
}
