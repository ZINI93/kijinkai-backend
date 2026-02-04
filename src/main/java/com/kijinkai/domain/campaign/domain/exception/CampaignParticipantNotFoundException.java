package com.kijinkai.domain.campaign.domain.exception;

public class CampaignParticipantNotFoundException extends RuntimeException {
    public CampaignParticipantNotFoundException(String message) {
        super(message);
    }
}
