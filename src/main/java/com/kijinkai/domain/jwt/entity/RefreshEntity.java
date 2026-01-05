package com.kijinkai.domain.jwt.entity;


import com.kijinkai.domain.common.TimeBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "refresh_entity")
public class RefreshEntity extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long refresh_entity_id;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @Column(name = "refresh", nullable = false, length = 512)
    private String refresh;

}
