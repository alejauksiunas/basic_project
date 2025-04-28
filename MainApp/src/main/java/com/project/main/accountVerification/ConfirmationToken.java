package com.project.main.accountVerification;

import com.project.main.user.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "confirmationToken")
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USERACCOUNT_SEQ", allocationSize = 1)
    private Long tokenId;

    @Column(name = "token")
    private String token;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "expiresAt")
    private LocalDateTime expiresAt;

    @Column(name = "confirmedAt")
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(
            name = "userId"
    )
    private UserAccount userAccount;

    public ConfirmationToken(String token,
                             LocalDateTime createdAt,
                             LocalDateTime expiresAt,
                             UserAccount userAccount) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.userAccount = userAccount;
    }
}
