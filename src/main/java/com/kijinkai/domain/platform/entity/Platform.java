package com.kijinkai.domain.platform.entity;


import com.kijinkai.domain.TimeBaseEntity;
import com.kijinkai.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Table(name = "platforms")
@Entity
public class Platform extends TimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long platFromId;

    @Column(name = "platform_uuid")
    private String platformUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;


    @Builder
    public Platform(String platformUuid, User user, String baseUrl) {
        this.platformUuid = UUID.randomUUID().toString();
        this.user = user;
        this.baseUrl = baseUrl;
    }


    public void updatePlatform(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
