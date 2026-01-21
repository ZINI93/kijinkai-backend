package com.kijinkai.domain.delivery.domain.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryType {

    EMS("EMS","EMS특급","https://www.post.japanpost.jp/int/ems/delivery/link_en.html?c=%s", 3 ),
    AIR_MAIL("AIR", "국제항공편","https://trackings.post.japanpost.jp/services/srv/search/direct?locale=en&reqCodeNo1=%s",5 ),
    SURFACE_MAIL("SURFACE","국제선편", "https://trackings.post.japanpost.jp/services/srv/search/direct?locale=en&reqCodeNo1=%s", 20 );

    private final String code;
    private final String description;
    private final String trackingUrlTemplate;
    private final int averageDeliveryDays;


    public String createTrackingUrl(String trackingNumber){
        return String.format(this.trackingUrlTemplate, trackingNumber);
    }
}
