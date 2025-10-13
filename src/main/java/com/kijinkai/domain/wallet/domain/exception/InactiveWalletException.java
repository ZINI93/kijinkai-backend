package com.kijinkai.domain.wallet.domain.exception;

public class InactiveWalletException extends RuntimeException {
  public InactiveWalletException(String message) {
    super(message);
  }
}
