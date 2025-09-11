package com.kijinkai.domain.platform.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.common.TimeBaseEntity;
import com.kijinkai.domain.platform.dto.PlatformUpdateDto;
import com.kijinkai.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Table(name = "platforms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Platform extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platform_id", nullable = false)
    private Long platformId;

    @Column(name = "platform_uuid")
    private UUID platformUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "base_url", nullable
            = false)
    private String baseUrl;


    @Builder
    public Platform(UUID platformUuid, User user, String baseUrl) {
        this.platformUuid = platformUuid != null ? platformUuid : UUID.randomUUID();
        this.user = user;
        this.baseUrl = baseUrl;
    }


    public void updatePlatformBaseUrl(PlatformUpdateDto updateDto) {
        this.baseUrl = updateDto.getBaseUrl();
    }
}
