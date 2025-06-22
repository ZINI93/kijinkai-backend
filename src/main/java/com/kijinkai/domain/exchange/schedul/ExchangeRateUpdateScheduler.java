package com.kijinkai.domain.exchange.schedul;

import com.kijinkai.domain.exchange.service.ExchangeRateUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateUpdateScheduler {

    private final ExchangeRateUpdater exchangeRateUpdater;

    // 매 6시간마다 실행 (21600000 밀리초 = 6시간)
    // cron 표현식을 사용하는 것이 더 유연할 수 있습니다. 예: "0 0 */6 * * *"
    // 또는 fixedRateString을 사용하여 설정 파일에서 관리할 수도 있습니다.
    // @Scheduled(fixedRateString = "${exchangerate.update.interval.ms:21600000}")
    @Scheduled(fixedRate = 21600000) // 6시간마다 실행
    // 특정 시간에 실행하려면 (예: 매일 00시, 06시, 12시, 18시)
    // @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void scheduleExchangeRateUpdate() {
        log.info("Scheduled exchange rate update job started at {}", System.currentTimeMillis());
        // 실제 서비스에서는 여러 통화 쌍을 업데이트해야 할 수 있습니다.
        // 예를 들어, 애플리케이션 시작 시 설정된 통화 쌍 목록을 반복하며 업데이트합니다.
        try {
            exchangeRateUpdater.updateExchangeRate("USD", "KRW");
            exchangeRateUpdater.updateExchangeRate("EUR", "KRW");
            exchangeRateUpdater.updateExchangeRate("JPY", "KRW"); // 일본 엔도 고려할 수 있겠네요 (현재는 JST 기준 시간입니다)
            // 필요한 다른 통화 쌍 추가
        } catch (Exception e) {
            log.error("Error during scheduled exchange rate update: {}", e.getMessage(), e);
            // 스케줄러 자체는 멈추지 않도록 예외를 잡습니다.
        }
    }
}
