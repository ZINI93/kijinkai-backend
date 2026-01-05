package com.kijinkai.domain.user.domain.model;


public enum SocialProviderType {

    NAVER("네이버"),
    GOOGLE("구글");

    private final String description;
    SocialProviderType(String description) {this.description = description;}
}
