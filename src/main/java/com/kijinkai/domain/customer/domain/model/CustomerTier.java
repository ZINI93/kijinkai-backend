package com.kijinkai.domain.customer.domain.model;

public enum CustomerTier {
    BRONZE("브론즈", "기본 등급", 1),
    SILVER("실버", "실버 등급", 2),
    GOLD("골드", "골드 등급", 3);

    private final String displayName;
    private final String description;
    private final int level;

    CustomerTier(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getLevel() { return level; }

    public boolean isHigherThan(CustomerTier other) {
        return this.level > other.level;
    }

    public static CustomerTier getDefaultTier() {
        return BRONZE;
    }
}