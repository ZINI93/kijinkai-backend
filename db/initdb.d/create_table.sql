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
    `email_verified_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '이메일 등록 시간',
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
    `customer_uuid` BINARY(16) NOT NULL COMMENT '고객 UUID',
    `order_id` BIGINT NOT NULL COMMENT '주문 ID',
    `product_payment_uuid` BINARY(16) COMMENT '상품 대금 결제 UUID',
    `delivery_fee_payment_uuid` BINARY(16) COMMENT '상품 배송비 결제 UUID',
    `product_link` VARCHAR(255) NOT NULL COMMENT '상품 링크',
    `quantity` INT NOT NULL COMMENT '수량',
    `price_original` DECIMAL(19, 4) NOT NULL COMMENT '원본 통화(엔화) 기준 상품 단가',
    `price_converted` DECIMAL(19, 4) NOT NULL COMMENT '변환된 통화 기준 상품 단가',
    `order_item_currency_original` VARCHAR(20) NOT NULL COMMENT '원본 통화 코드 (ISO 4217)',
    `order_item_currency_converted` VARCHAR(20) NOT NULL COMMENT '변환된 통화 코드 (ISO 4217)',
    `exchange_rate` DECIMAL(10, 6) NOT NULL COMMENT '환율',
    `memo` TEXT COMMENT '상품 항목 관련 메모',
    `order_item_status` VARCHAR(20) NOT NULL COMMENT '상품의 상태',


    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_delivery_status` CHECK (`delivery_status` IN ('PENDING', 'PENDING_APPROVAL', 'PRODUCT_PURCHASE', 'PRODUCT_PURCHASE_COMPLETE', 'PRODUCT_PAYMENT_COMPLETED'
    , 'DELIVERY_FEE_PAYMENT_REQUEST', 'DELIVERY_FEE_PAYMENT_COMPLETED', 'COMPLETED', 'CANCELLED', 'REJECTED')),
    CONSTRAINT `chk_order_item_currency_original` CHECK (`order_item_currency_original` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `chk_order_item_currency_converted` CHECK (`order_item_currency_converted` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
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

-- DepositRequest 테이블 생성
CREATE TABLE `deposit_requests` (
    `deposit_request_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '입금 요청 ID',
    `request_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '입금 요청 고유 UUID',
    `customer_uuid` BINARY(16) NOT NULL COMMENT '고객 UUID',
    `wallet_uuid` BINARY(16) NOT NULL COMMENT '지갑 UUID',
    `amount_original` DECIMAL(19, 4) NOT NULL COMMENT '원본 입금 금액',
    `currency_original` VARCHAR(10) NOT NULL COMMENT '원본 통화',
    `amount_converted` DECIMAL(19, 4) NOT NULL COMMENT '변환된 입금 금액 (JPY)',
    `exchange_rate` DECIMAL(18, 8) NOT NULL COMMENT '환율',
    `depositor_name` VARCHAR(100) NOT NULL COMMENT '입금자명',
    `bank_account` VARCHAR(50) NOT NULL COMMENT '입금 계좌',
    `status` VARCHAR(30) NOT NULL COMMENT '입금 상태',
    `expires_at` DATETIME NOT NULL COMMENT '만료 일시',
    `processed_by_admin` BINARY(16) COMMENT '처리한 관리자 UUID',
    `processed_at` DATETIME COMMENT '처리 일시',
    `admin_memo` TEXT COMMENT '관리자 메모',
    `rejection_reason` TEXT COMMENT '거절 사유',
    `version` BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 잠금 버전',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `chk_deposit_status` CHECK (`status` IN ('PENDING_ADMIN_APPROVAL', 'APPROVED', 'REJECTED', 'EXPIRED')),
    CONSTRAINT `chk_deposit_currency_original` CHECK (`currency_original` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `chk_deposit_amount_original_positive` CHECK (`amount_original` > 0),
    CONSTRAINT `chk_deposit_amount_converted_positive` CHECK (`amount_converted` > 0),
    CONSTRAINT `chk_deposit_exchange_rate_positive` CHECK (`exchange_rate` > 0),
    CONSTRAINT `chk_deposit_version_non_negative` CHECK (`version` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_deposit_requests_request_uuid` ON `deposit_requests` (`request_uuid`);
CREATE INDEX `idx_deposit_requests_customer_uuid` ON `deposit_requests` (`customer_uuid`);
CREATE INDEX `idx_deposit_requests_wallet_uuid` ON `deposit_requests` (`wallet_uuid`);
CREATE INDEX `idx_deposit_requests_status` ON `deposit_requests` (`status`);
CREATE INDEX `idx_deposit_requests_status_created_at` ON `deposit_requests` (`status`, `created_at`);
CREATE INDEX `idx_deposit_requests_expires_at` ON `deposit_requests` (`expires_at`);
CREATE INDEX `idx_deposit_requests_processed_by_admin` ON `deposit_requests` (`processed_by_admin`);
CREATE INDEX `idx_deposit_requests_customer_request` ON `deposit_requests` (`customer_uuid`, `request_uuid`);
CREATE INDEX `idx_deposit_requests_status_expires_at` ON `deposit_requests` (`status`, `expires_at`);

-- WithdrawRequest 테이블 생성
CREATE TABLE `withdraw_requests` (
    `withdraw_request_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '출금 요청 ID',
    `request_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '출금 요청 고유 UUID',
    `customer_uuid` BINARY(16) NOT NULL COMMENT '고객 UUID',
    `wallet_uuid` BINARY(16) NOT NULL COMMENT '지갑 UUID',
    `request_amount` DECIMAL(19, 4) NOT NULL COMMENT '요청 출금 금액',
    `withdraw_fee` DECIMAL(19, 4) NOT NULL COMMENT '출금 수수료',
    `total_deduct_amount` DECIMAL(19, 4) NOT NULL COMMENT '총 차감 금액 (출금금액 + 수수료)',
    `target_currency` VARCHAR(10) NOT NULL COMMENT '목표 통화',
    `converted_amount` DECIMAL(19, 4) NOT NULL COMMENT '변환된 출금 금액',
    `exchange_rate` DECIMAL(18, 8) NOT NULL COMMENT '환율',
    `bank_name` VARCHAR(100) NOT NULL COMMENT '은행명',
    `account_holder` VARCHAR(100) NOT NULL COMMENT '계좌 소유자명',
    `status` VARCHAR(30) NOT NULL COMMENT '출금 상태',
    `processed_by_admin` BINARY(16) COMMENT '처리한 관리자 UUID',
    `processed_at` DATETIME COMMENT '처리 일시',
    `admin_memo` TEXT COMMENT '관리자 메모',
    `rejection_reason` TEXT COMMENT '거절 사유',
    `expires_at` DATETIME NOT NULL COMMENT '만료 일시',
    `version` BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 잠금 버전',

    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    -- 제약조건
    CONSTRAINT `chk_withdraw_status` CHECK (`status` IN ('PENDING_ADMIN_APPROVAL', 'APPROVED', 'BANK_TRANSFER_PENDING', 'COMPLETED', 'REJECTED', 'FAILED')),
    CONSTRAINT `chk_withdraw_target_currency` CHECK (`target_currency` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `chk_withdraw_request_amount_positive` CHECK (`request_amount` > 0),
    CONSTRAINT `chk_withdraw_converted_amount_positive` CHECK (`converted_amount` > 0),
    CONSTRAINT `chk_withdraw_fee_non_negative` CHECK (`withdraw_fee` >= 0),
    CONSTRAINT `chk_withdraw_total_deduct_positive` CHECK (`total_deduct_amount` > 0),
    CONSTRAINT `chk_withdraw_exchange_rate_positive` CHECK (`exchange_rate` > 0),
    CONSTRAINT `chk_withdraw_version_non_negative` CHECK (`version` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_withdraw_requests_request_uuid` ON `withdraw_requests` (`request_uuid`);
CREATE INDEX `idx_withdraw_requests_customer_uuid` ON `withdraw_requests` (`customer_uuid`);
CREATE INDEX `idx_withdraw_requests_wallet_uuid` ON `withdraw_requests` (`wallet_uuid`);
CREATE INDEX `idx_withdraw_requests_status` ON `withdraw_requests` (`status`);
CREATE INDEX `idx_withdraw_requests_status_created_at` ON `withdraw_requests` (`status`, `created_at`);
CREATE INDEX `idx_withdraw_requests_expires_at` ON `withdraw_requests` (`expires_at`);
CREATE INDEX `idx_withdraw_requests_processed_by_admin` ON `withdraw_requests` (`processed_by_admin`);
CREATE INDEX `idx_withdraw_requests_customer_request` ON `withdraw_requests` (`customer_uuid`, `request_uuid`);

-- RefundRequest 테이블 생성 (수정됨)
CREATE TABLE `refund_requests` (
    `refund_request_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '환불 요청 ID',
    `refund_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '환불 요청 고유 UUID',
    `customer_uuid` BINARY(16) NOT NULL COMMENT '고객 UUID',
    `wallet_uuid` BINARY(16) NOT NULL COMMENT '지갑 UUID',
    `order_item_uuid` BINARY(16) NOT NULL COMMENT '주문 상품 UUID',

    -- 환불 금액 정보
    `refund_amount` DECIMAL(19, 4) NOT NULL COMMENT '환불 금액',
    `refund_currency` VARCHAR(10) NOT NULL DEFAULT 'JPY' COMMENT '환불 통화', -- 누락된 컬럼 추가

    -- 환불 정보
    `refund_reason` VARCHAR(255) NOT NULL COMMENT '환불 사유',
    `refund_type` VARCHAR(30) NOT NULL COMMENT '환불 유형',

    -- 상태 관리
    `status` VARCHAR(30) NOT NULL COMMENT '환불 상태',

    -- 관리자 처리 정보
    `requested_by_admin` BINARY(16) NOT NULL COMMENT '요청한 관리자 UUID', -- 누락된 컬럼 추가
    `processed_by_admin` BINARY(16) COMMENT '처리한 관리자 UUID',
    `processed_at` DATETIME COMMENT '처리 일시',
    `admin_memo` TEXT COMMENT '관리자 메모',
    `rejection_reason` TEXT COMMENT '거절 사유', -- 누락된 컬럼 추가

    -- 낙관적 잠금
    `version` BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 잠금 버전',

    -- BaseEntity 필드들
    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    -- 제약조건 (수정됨)
    CONSTRAINT `chk_refund_status` CHECK (`status` IN ('PROCESSING', 'COMPLETED', 'FAILED')),
    CONSTRAINT `chk_refund_currency` CHECK (`refund_currency` IN ('JPY', 'KRW', 'CLP', 'USD')),
    CONSTRAINT `chk_refund_type` CHECK (`refund_type` IN ('STOCK_OUT', 'PURCHASE_CANCELLED', 'DEFECTIVE_PRODUCT', 'ADMIN_DECISION')),
    CONSTRAINT `chk_refund_amount_positive` CHECK (`refund_amount` > 0),
    CONSTRAINT `chk_refund_version_non_negative` CHECK (`version` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (수정됨)
CREATE INDEX `idx_refund_requests_refund_uuid` ON `refund_requests` (`refund_uuid`);
CREATE INDEX `idx_refund_requests_customer_uuid` ON `refund_requests` (`customer_uuid`);
CREATE INDEX `idx_refund_requests_wallet_uuid` ON `refund_requests` (`wallet_uuid`);
CREATE INDEX `idx_refund_requests_order_item_uuid` ON `refund_requests` (`order_item_uuid`);
CREATE INDEX `idx_refund_requests_status` ON `refund_requests` (`status`);
CREATE INDEX `idx_refund_requests_requested_by_admin` ON `refund_requests` (`requested_by_admin`);
CREATE INDEX `idx_refund_requests_processed_by_admin` ON `refund_requests` (`processed_by_admin`);
CREATE INDEX `idx_refund_requests_customer_refund` ON `refund_requests` (`customer_uuid`, `refund_uuid`);

-- OrderPayment 테이블 생성 (완전 수정됨)
CREATE TABLE `order_payments` (
    `order_payment_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 결제 ID',
    `payment_uuid` BINARY(16) NOT NULL UNIQUE COMMENT '결제 고유 UUID',
    `customer_uuid` BINARY(16) NOT NULL COMMENT '고객 UUID',
    `wallet_uuid` BINARY(16) NOT NULL COMMENT '지갑 UUID',
    `order_uuid` BINARY(16) COMMENT '주문 UUID',

    -- 결제 정보
    `payment_amount` DECIMAL(19, 4) NOT NULL COMMENT '결제 금액',
    `payment_order` VARCHAR(20) NOT NULL COMMENT '결제 순서 (FIRST, SECOND)', -- 누락된 컬럼 추가

    -- 상태 관리
    `order_payment_status` VARCHAR(30) NOT NULL COMMENT '결제 상태',
    `payment_type` VARCHAR(30) NOT NULL COMMENT '결제 유형 (PRODUCT_PAYMENT, SHIPPING_PAYMENT)',

    -- 처리 정보
    `rejection_reason` TEXT COMMENT '거절 사유',
    `paid_at` DATETIME COMMENT NOT NULL '지불 일시',
    `created_by_admin_uuid` BINARY(16) COMMENT '생성한 관리자 UUID', -- 컬럼명 수정

    -- 낙관적 잠금
    `version` BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 잠금 버전',

    -- BaseEntity 필드들
    `created_by` VARCHAR(50) COMMENT '생성자',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_by` VARCHAR(50) COMMENT '최종 수정자',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    -- 제약조건 (수정됨)
    CONSTRAINT `chk_order_payment_status` CHECK (`status` IN ('PENDING', 'COMPLETED', 'FAILED')),
    CONSTRAINT `chk_order_payment_type` CHECK (`payment_type` IN ('PRODUCT_PAYMENT', 'SHIPPING_PAYMENT')),
    CONSTRAINT `chk_order_payment_order` CHECK (`payment_order` IN ('FIRST', 'SECOND')),
    CONSTRAINT `chk_order_payment_version_non_negative` CHECK (`version` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (수정됨)
CREATE INDEX `idx_order_payments_payment_uuid` ON `order_payments` (`payment_uuid`);
CREATE INDEX `idx_order_payments_customer_uuid` ON `order_payments` (`customer_uuid`);
CREATE INDEX `idx_order_payments_wallet_uuid` ON `order_payments` (`wallet_uuid`);
CREATE INDEX `idx_order_payments_order_uuid` ON `order_payments` (`order_uuid`);
CREATE INDEX `idx_order_payments_status` ON `order_payments` (`order_payment_status`);
CREATE INDEX `idx_order_payments_payment_order` ON `order_payments` (`payment_order`);
CREATE INDEX `idx_order_payments_payment_type` ON `order_payments` (`payment_type`);
CREATE INDEX `idx_order_payments_created_by_admin_uuid` ON `order_payments` (`created_by_admin_uuid`);
CREATE INDEX `idx_order_payments_customer_payment` ON `order_payments` (`customer_uuid`, `payment_uuid`);
CREATE INDEX `idx_order_payments_order_payment_order` ON `order_payments` (`order_uuid`, `payment_order`);


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
    `exchange_rate_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '환율 ID',
    `currency` VARCHAR(10) NOT NULL COMMENT '기준 통화 (예: USD)',
    `rate` DECIMAL(18, 8) NOT NULL COMMENT '환율',
    `fetched_at` DATETIME NOT NULL COMMENT '환율 조회 일시',

    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시간',

    CONSTRAINT `uq_exchange_rates_currency_pair_fetched_at` UNIQUE (`currency`, `fetched_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (성능 고려)
CREATE INDEX `idx_exchange_rates_currency` ON `exchange_rates` (`currency`);
CREATE INDEX `idx_exchange_rates_fetched_at` ON `exchange_rates` (`fetched_at`);
