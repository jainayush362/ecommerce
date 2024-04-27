package com.ayushjainttn.bootcampproject.ecommerce.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="confirmation_token_table")
@NoArgsConstructor
@Slf4j
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="token_id")
    private Long tokenid;

    @Column(name="confirmation_token")
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ConfirmationToken(User user) {
        this.user = user;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

    public boolean isTokenExpired(Long expireTime) {
        log.info("----inside isTokenExpired() method----");
        //validity is 3hrs
        final Long EXPIRATION_TIME = expireTime;
        Date date = new Date();
        //System.out.println((date.getTime()-this.createdDate.getTime())/1000);
        //System.out.println(expireTime);
        log.info("----isTokenExpired() method executed success----");
        return (((date.getTime()-this.createdDate.getTime())/1000)> EXPIRATION_TIME);
    }
}
