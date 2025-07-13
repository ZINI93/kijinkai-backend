package com.kijinkai.domain.wallet.exception;

public class InactiveWalletException extends RuntimeException {
  public InactiveWalletException(String message) {
    super(message);
  }
}
