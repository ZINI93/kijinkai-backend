package com.kijinkai.config;

import com.kijinkai.domain.jwt.repository.RefreshEntityRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduleConfig {

    private final RefreshEntityRepository refreshEntityRepository;

    public ScheduleConfig(RefreshEntityRepository refreshEntityRepository) {
        this.refreshEntityRepository = refreshEntityRepository;
    }


    @Scheduled(cron = "0 0 3 * * *")
    public void refreshEntityTtlSchedule(){
        LocalDateTime cutoff = LocalDateTime.now().minusDays(8);
        refreshEntityRepository.deleteByCreatedAtBefore(cutoff);
    }


}
