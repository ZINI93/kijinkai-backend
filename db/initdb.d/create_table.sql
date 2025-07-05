-- `users` 테이블 생성
-- 다른 테이블에서 `user_id`를 참조하므로 가장 먼저 생성합니다.
CREATE TABLE `users` (
    `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
    `user_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '사용자 고유 UUID', -- VARCHAR(36) 대신 BINARY(16) 사용 권장 (효율적인 저장 및 인덱싱)
    `email` VARCHAR(255) NOT NULL UNIQUE COMMENT '사용자 이메일',
    `password` VARCHAR(255) NOT NULL COMMENT '사용자 비밀번호 (해시값 저장 권장)',
    `nick_name` VARCHAR(50) NOT NULL UNIQUE COMMENT '사용자 닉네임',
    `user_role` VARCHAR(20) NOT NULL COMMENT '사용자 역할 (예: USER, ADMIN)',
    `email_verified` BOOLEAN NOT NULL COMMENT '사용자 메일 등록 확인 여부',
    `email_verifiedAt` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '이메일 등록 시간',
    `user_status` VARCHAR(20) NOT NULL COMMENT '사용자 계정 상태',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',

    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_user_status` CHECK (`user_status` IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DELETED')),
    CONSTRAINT `chk_user_role` CHECK (`user_role` IN ('USER', 'ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- `customers` 테이블 생성
-- `users` 테이블의 `user_id`를 참조합니다.
CREATE TABLE `customers` (
    `customer_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '고객 ID',
    `customer_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '고객 고유 UUID', -- VARCHAR(36) 대신 BINARY(16) 사용 권장
    `first_name` VARCHAR(100) NOT NULL COMMENT '이름',
    `last_name` VARCHAR(100) NOT NULL COMMENT '성',
    `phone_number` VARCHAR(20) NOT NULL COMMENT '전화번호',
    `customer_tier` VARCHAR(20) NOT NULL COMMENT '고객 등급 (예: BRONZE, SILVER, GOLD)',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '사용자 ID', -- `users` 테이블의 `user_id`와 1:1 관계

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',

    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_customer_tier` CHECK (`customer_tier` IN ('BRONZE', 'SILVER', 'GOLD')),
    CONSTRAINT `fk_customers_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_customers_customer_uuid` ON `customers` (`customer_uuid`);
CREATE INDEX `idx_customers_phone_number` ON `customers` (`phone_number`);
CREATE INDEX `idx_customers_user_id` ON `customers` (`user_id`);
CREATE INDEX `idx_customers_customer_tier` ON `customers` (`customer_tier`);

CREATE TABLE `wallets` (
    `wallet_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '지갑 ID',
    `wallet_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '지갑 고유 UUID',
    `customer_id` BIGINT NOT NULL UNIQUE COMMENT '고객 ID', -- `customers` 테이블의 `customer_id`와 1:1 관계
    `balance` DECIMAL(16, 4) NOT NULL COMMENT '지갑 잔액',
    `currency` VARCHAR(10) NOT NULL COMMENT '통화 종류 (예: USD, KRW)',
    `wallet_status` VARCHAR(20) NOT NULL COMMENT '지갑 상태 (예: ACTIVE, BLOCKED)',
    `version` BIGINT NOT NULL COMMENT '낙관적 락 버전',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',

    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_currency` CHECK (`currency` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `fk_wallets_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_wallets_customer_id` ON `wallets` (`customer_id`);
CREATE INDEX `idx_wallets_wallet_uuid` ON `wallets` (`wallet_uuid`);
CREATE INDEX `idx_wallets_currency` ON `wallets` (`currency`);

CREATE TABLE `platforms` (
    `platform_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '플랫폼 ID',
    `platform_uuid` BINARY(16) UNIQUE COMMENT '플랫폼 고유 UUID',
    `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
    `base_url` VARCHAR(255) NOT NULL COMMENT '플랫폼 기본 URL',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',

    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `fk_platforms_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `addresses` (
    `address_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주소 ID',
    `address_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '주소 고유 UUID', -- VARCHAR(36) 대신 BINARY(16) 사용 권장
    `customer_id` BIGINT NOT NULL COMMENT '고객 ID',
    `recipient_name` VARCHAR(100) COMMENT '수령인 이름',
    `recipient_phone_number` VARCHAR(20) COMMENT '수령인 전화번호',
    `country` VARCHAR(100) NOT NULL COMMENT '국가',
    `zipcode` VARCHAR(20) NOT NULL COMMENT '우편번호',
    `state` VARCHAR(100) NOT NULL COMMENT '주/도',
    `city` VARCHAR(100) NOT NULL COMMENT '도시',
    `street` VARCHAR(255) NOT NULL COMMENT '거리 주소',
    `is_default` BOOLEAN NOT NULL COMMENT '기본 주소 여부',

    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `fk_addresses_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_addresses_customer_id` ON `addresses` (`customer_id`);
CREATE INDEX `idx_addresses_address_uuid` ON `addresses` (`address_uuid`);
CREATE INDEX `idx_addresses_zipcode` ON `addresses` (`zipcode`);

CREATE TABLE `orders` (
    `order_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 ID',
    `order_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '주문 고유 UUID',
    `customer_id` BIGINT NOT NULL COMMENT '고객 ID',
    `total_price_original` DECIMAL(19, 4) COMMENT '엔화 기준 상품 전체 가격',
    `total_price_converted` DECIMAL(19, 4) COMMENT '변환된 통화 기준 상품 전체 가격',
    `final_price_original` DECIMAL(19, 4) COMMENT '엔화 기준 배송비 포함 최종 가격',
    `converted_currency` VARCHAR(20) COMMENT '변환된 통화 (ISO 4217 코드)',
    `order_status` VARCHAR(20) NOT NULL COMMENT '주문 상태 (예: PENDING, COMPLETED, CANCELED)',
    `memo` TEXT COMMENT '주문 메모',
    `rejected_reason` VARCHAR(255) COMMENT '주문 거절 사유',
    `payment_type` VARCHAR(50) COMMENT '결제 유형',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',

    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_order_status` CHECK (`order_status` IN ('DRAFT', 'PENDING_APPROVAL', 'AWAITING_PAYMENT', 'CANCEL', 'PAID', 'PREPARE_DELIVERY', 'SHIPPING', 'DELIVERED', 'REJECTED')),
    CONSTRAINT `chk_converted_currency` CHECK (`converted_currency` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `fk_orders_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_orders_customer_id` ON `orders` (`customer_id`);
CREATE INDEX `idx_orders_order_uuid` ON `orders` (`order_uuid`);
CREATE INDEX `idx_orders_status` ON `orders` (`order_status`);

CREATE TABLE `order_items` (
    `order_item_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 상품 ID',
    `order_item_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '주문 상품 고유 UUID',
    `platform_id` BIGINT NOT NULL COMMENT '플랫폼 ID',
    `order_id` BIGINT NOT NULL UNIQUE COMMENT '주문 ID', -- UNIQUE 제약 조건은 OrderItem이 Order에 대해 1:1 관계일 때 사용 (제공된 엔티티 정의에 따름)
    `product_link` VARCHAR(255) NOT NULL COMMENT '상품 링크',
    `quantity` INT NOT NULL COMMENT '수량',
    `price_original` DECIMAL(19, 4) NOT NULL COMMENT '원본 통화(엔화) 기준 상품 단가',
    `price_converted` DECIMAL(19, 4) NOT NULL COMMENT '변환된 통화 기준 상품 단가',
    `order_item_currency_original` VARCHAR(20) NOT NULL COMMENT '원본 통화 코드 (ISO 4217)',
    `order_item_currency_converted` VARCHAR(20) NOT NULL COMMENT '변환된 통화 코드 (ISO 4217)',
    `exchange_rate` DECIMAL(10, 6) NOT NULL COMMENT '환율',
    `memo` TEXT COMMENT '상품 항목 관련 메모',

    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_order_item_currency_original` CHECK (`order_item_currency_original` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `chk_order_item_currency_converted` CHECK (`order_item_currency_converted` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `fk_order_items_platform_id` FOREIGN KEY (`platform_id`) REFERENCES `platforms` (`platform_id`),
    CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_order_items_platform_id` ON `order_items` (`platform_id`);
CREATE INDEX `idx_order_items_order_id` ON `order_items` (`order_id`);
CREATE INDEX `idx_order_items_order_item_uuid` ON `order_items` (`order_item_uuid`);

CREATE TABLE `deliveries` (
    `delivery_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '배송 ID',
    `delivery_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '배송 고유 UUID',
    `order_id` BIGINT NOT NULL COMMENT '주문 ID',
    `customer_id` BIGINT NOT NULL COMMENT '고객 ID',
    `delivery_status` VARCHAR(20) NOT NULL COMMENT '배송 상태',
    `recipient_name` VARCHAR(100) NOT NULL COMMENT '수령인 이름',
    `recipient_phone_number` VARCHAR(20) NOT NULL COMMENT '수령인 전화번호',
    `country` VARCHAR(100) NOT NULL COMMENT '국가',
    `zipcode` VARCHAR(20) NOT NULL COMMENT '우편번호',
    `state` VARCHAR(100) NOT NULL COMMENT '주/도',
    `city` VARCHAR(100) NOT NULL COMMENT '도시',
    `street` VARCHAR(255) NOT NULL COMMENT '거리 주소',
    `carrier` VARCHAR(50) NOT NULL COMMENT '택배사 이름',
    `tracking_number` VARCHAR(100) UNIQUE NOT NULL COMMENT '송장 번호',
    `delivery_fee` DECIMAL(10, 2) NOT NULL COMMENT '배송비',
    `estimated_delivery_at` DATETIME COMMENT '예상 배송 완료 일시',
    `shipped_at` DATETIME COMMENT '실제 발송 일시',
    `delivered_at` DATETIME COMMENT '실제 배송 완료 일시',
    `delivery_request` VARCHAR(500) COMMENT '배송 요청 사항',
    `cancel_reason` VARCHAR(255) COMMENT '배송 취소/실패 사유',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',

    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_delivery_status` CHECK (`delivery_status` IN ('PENDING', 'SHIPPED', 'DELIVERED', 'IN_TRANSIT', 'CANCELLED')),
    CONSTRAINT `chk_carrier` CHECK (`carrier` IN ('YAMATO', 'JAPANPOST')),
    CONSTRAINT `fk_deliveries_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `fk_deliveries_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_deliveries_order_id` ON `deliveries` (`order_id`);
CREATE INDEX `idx_deliveries_customer_id` ON `deliveries` (`customer_id`);
CREATE INDEX `idx_deliveries_delivery_uuid` ON `deliveries` (`delivery_uuid`);
CREATE INDEX `idx_deliveries_tracking_number` ON `deliveries` (`tracking_number`);
CREATE INDEX `idx_deliveries_delivery_status` ON `deliveries` (`delivery_status`);
CREATE INDEX `idx_deliveries_estimated_delivery_at` ON `deliveries` (`estimated_delivery_at`);

CREATE TABLE `payments` (
    `payment_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '결제 ID',
    `payment_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '결제 고유 UUID',
    `customer_id` BIGINT NOT NULL COMMENT '고객 ID',
    `wallet_id` BIGINT NOT NULL COMMENT '지갑 ID',
    `order_id` BIGINT NOT NULL COMMENT '주문 ID',
    `payment_status` VARCHAR(20) NOT NULL COMMENT '결제 상태 (예: PENDING, COMPLETED, FAILED)',
    `payment_method` VARCHAR(50) NOT NULL COMMENT '결제 수단 (예: CREDIT_CARD, BANK_TRANSFER)',
    `amount_original` DECIMAL(19, 4) NOT NULL COMMENT '원본 통화 기준 결제 금액',
    `payment_currency_original` VARCHAR(20) NOT NULL COMMENT '원본 통화 코드 (ISO 4217)',
    `amount_converter` DECIMAL(19, 4) NOT NULL COMMENT '변환된 통화 기준 결제 금액',
    `payment_currency_converter` VARCHAR(20) NOT NULL COMMENT '변환된 통화 코드 (ISO 4217)',
    `payment_type` VARCHAR(50) NOT NULL COMMENT '결제 유형',
    `description` VARCHAR(500) COMMENT '결제 설명',
    `external_transaction_id` VARCHAR(255) COMMENT '외부 트랜잭션 ID',
    `exchange_rate` DECIMAL(10, 6) NOT NULL COMMENT '적용된 환율',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',

    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_payment_status` CHECK (`payment_status` IN ('PENDING', 'COMPLETED', 'CANCEL', 'FAILED', 'REFUNDED')),
    CONSTRAINT `chk_payment_method` CHECK (`payment_method` IN ('CAS', 'BALANCE')),
    CONSTRAINT `chk_payment_currency_original` CHECK (`payment_currency_original` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `chk_payment_currency_converter` CHECK (`payment_currency_converter` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `chk_payment_type` CHECK (`payment_type` IN ('CREDIT', 'DEBIT')),
    CONSTRAINT `fk_payments_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_payments_wallet_id` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`wallet_id`),
    CONSTRAINT `fk_payments_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_payments_customer_id` ON `payments` (`customer_id`);
CREATE INDEX `idx_payments_wallet_id` ON `payments` (`wallet_id`);
CREATE INDEX `idx_payments_order_id` ON `payments` (`order_id`);
CREATE INDEX `idx_payments_payment_uuid` ON `payments` (`payment_uuid`);
CREATE INDEX `idx_payments_status` ON `payments` (`payment_status`);
CREATE INDEX `idx_payments_method` ON `payments` (`payment_method`);


CREATE TABLE `transactions` (
    `transaction_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '트랜잭션 ID',
    `transaction_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '트랜잭션 고유 UUID',
    `customer_id` BIGINT NOT NULL COMMENT '고객 ID',
    `wallet_id` BIGINT NOT NULL COMMENT '지갑 ID',
    `order_id` BIGINT NOT NULL COMMENT '주문 ID',
    `transaction_type` VARCHAR(20) NOT NULL COMMENT '트랜잭션 유형 (예: DEPOSIT, WITHDRAWAL, PAYMENT)',
    `amount` DECIMAL(19, 4) NOT NULL COMMENT '거래 금액',
    `balance_before` DECIMAL(19, 4) NOT NULL COMMENT '거래 전 잔액',
    `balance_after` DECIMAL(19, 4) NOT NULL COMMENT '거래 후 잔액',
    `currency` VARCHAR(20) NOT NULL COMMENT '거래 통화 코드 (ISO 4217)',
    `transaction_status` VARCHAR(20) NOT NULL COMMENT '트랜잭션 상태 (예: PENDING, COMPLETED, FAILED)',
    `memo` TEXT COMMENT '트랜잭션 메모',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',

    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_transaction_type` CHECK (`transaction_type` IN ('PAYMENT', 'REFUND', 'CHARGE', 'WITHDRAWAL', 'ADMIN_ADJUSTMENT')),
    CONSTRAINT `chk_transaction_currency` CHECK (`currency` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `chk_transaction_status` CHECK (`transaction_status` IN ('PENDING', 'COMPLETED', 'FAILED')),
    CONSTRAINT `fk_transactions_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_transactions_wallet_id` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`wallet_id`),
    CONSTRAINT `fk_transactions_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_transactions_customer_id` ON `transactions` (`customer_id`);
CREATE INDEX `idx_transactions_wallet_id` ON `transactions` (`wallet_id`);
CREATE INDEX `idx_transactions_order_id` ON `transactions` (`order_id`);
CREATE INDEX `idx_transactions_transaction_uuid` ON `transactions` (`transaction_uuid`);
CREATE INDEX `idx_transactions_type` ON `transactions` (`transaction_type`);
CREATE INDEX `idx_transactions_status` ON `transactions` (`transaction_status`);
CREATE INDEX `idx_transactions_currency` ON `transactions` (`currency`);


CREATE TABLE `exchange_rates` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '환율 ID',
    `from_currency` VARCHAR(10) NOT NULL COMMENT '기준 통화 (예: USD)',
    `to_currency` VARCHAR(10) NOT NULL COMMENT '대상 통화 (예: KRW)',
    `rate` DECIMAL(18, 8) NOT NULL COMMENT '환율',
    `fetched_at` DATETIME NOT NULL COMMENT '환율 조회 일시',

    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `uq_exchange_rates_currency_pair_fetched_at` UNIQUE (`from_currency`, `to_currency`, `fetched_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_exchange_rates_from_currency` ON `exchange_rates` (`from_currency`);
CREATE INDEX `idx_exchange_rates_to_currency` ON `exchange_rates` (`to_currency`);
CREATE INDEX `idx_exchange_rates_fetched_at` ON `exchange_rates` (`fetched_at`);
