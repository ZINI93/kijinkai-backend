package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.dto.ExchangeRateRequestDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateUpdateDto;
import com.kijinkai.domain.exchange.exception.ExchangeRateNotFoundException;
import com.kijinkai.domain.exchange.factory.ExchangeRateFactory;
import com.kijinkai.domain.exchange.mapper.ExchangeRateMapper;
import com.kijinkai.domain.exchange.repository.ExchangeRateRepository;
import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.adapter.out.persistence.repository.UserRepository;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// 엔이 고정이니까 .. 굳이 form to 가 필요없이 환전국가만 찾아서 환전하면 될것 같은데.
// 엔을 원화로 환전할때도,,, 엔을 칠레돈으로 환전할 때도 그냥 고정으로 된 환률만 있으면 되는데...

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateFactory exchangeRateFactory;
    private final ExchangeRateMapper exchangeRateMapper;

    private final UserRepository userRepository;
    private final UserApplicationValidator userValidator;

    @Override
    @Transactional
    public ExchangeRateResponseDto createExchangeRate(UUID adminUuid, ExchangeRateRequestDto requestDto) {

        findUserByUserUuidAndValidate(adminUuid);
        ExchangeRate exchangerate = exchangeRateFactory.createExchangerate(requestDto);

        ExchangeRate savedExchangeRate = exchangeRateRepository.save(exchangerate);

        return exchangeRateMapper.toResponse(savedExchangeRate);
    }

    @Override
    @Transactional
    public ExchangeRateResponseDto updateExchangeRate(UUID adminUuid, Long exchangeId, ExchangeRateUpdateDto updateDto) {
        findUserByUserUuidAndValidate(adminUuid);
        ExchangeRate exchangeRate = findExchangeByExchangeId(exchangeId);
        exchangeRate.updateExchangeRate(updateDto.getExchangeRate());

        return exchangeRateMapper.toResponse(exchangeRate);
    }

    @Override
    public ExchangeRateResponseDto getExchangeRateInfo(Long exchangeId) {
        ExchangeRate exchangeRate = findExchangeByExchangeId(exchangeId);
        return exchangeRateMapper.toResponse(exchangeRate);
    }

    @Override
    public ExchangeRateResponseDto getExchangeRateInfoByCurrency(Currency currency) {
        ExchangeRate exchangeRate = exchangeRateRepository.findByCurrency(currency)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate not found"));
        return exchangeRateMapper.toResponse(exchangeRate);
    }

    @Override
    public BigDecimal getExchangeRate(Long exchangeId) {

        ExchangeRate exchangeRate = findExchangeByExchangeId(exchangeId);

        return exchangeRate.getRate();
    }

    @Override
    public List<ExchangeRateResponseDto> getExchangeRates() {
        List<ExchangeRate> exchanges = exchangeRateRepository.findAll();

        return exchanges.stream().map(exchangeRateMapper::toResponse).toList();
    }


    @Override
    @Transactional
    public void deleteExchangeRate(UUID adminUuid, Long exchangeId) {
        findUserByUserUuidAndValidate(adminUuid);
        ExchangeRate exchangeRate = findExchangeByExchangeId(exchangeId);
        exchangeRateRepository.delete(exchangeRate);
    }

    //helper method

    private ExchangeRate findExchangeByExchangeId(Long exchangeId) {
        return exchangeRateRepository.findByExchangeRateId(exchangeId)
                .orElseThrow(() -> new ExchangeRateNotFoundException(String.format("Exchange not found for exchange id: %s", exchangeId)));
    }

    private void findUserByUserUuidAndValidate(UUID adminUuid) {
        UserJpaEntity admin = userRepository.findByUserUuid(adminUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for admin uuid: %s", adminUuid)));

        userValidator.requireJpaAdminRole(admin);
    }
}
