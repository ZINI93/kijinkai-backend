package com.kijinkai.domain.exchange.schedul;

import com.kijinkai.domain.exchange.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyUpdateScheduler {

    @Autowired
    private ExchangeRateService exchangeRateService;

    /**
     * 6시간마다 환율 자동 업데이트
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void updateExchangeRates() {
        log.info("스케줄링된 환율 업데이트 작업 시작");
        exchangeRateService.updateExchangeRates();
        log.info("스케줄링된 환율 업데이트 작업 완료");
    }

    /**
     * 매일 오전 9시 환율 업데이트 (추가 옵션)
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void dailyExchangeRateUpdate() {
        log.info("매일 오전 9시 환율 업데이트 작업 시작");
        exchangeRateService.updateExchangeRates();
        log.info("매일 오전 9시 환율 업데이트 작업 완료");
    }
}

