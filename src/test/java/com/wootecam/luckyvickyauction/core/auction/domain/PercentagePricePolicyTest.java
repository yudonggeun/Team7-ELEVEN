package com.wootecam.luckyvickyauction.core.auction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PercentagePricePolicyTest {

    @ParameterizedTest
    @ValueSource(doubles = {0, 100})
    void 경매_할인율은_0보다_크고_50보다_작거나_같지_않다면_예외가_발생한다(double discountRate) {
        // expect
        assertThatThrownBy(() -> new PercentagePricePolicy(discountRate))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(String.format("할인율은 0%% 초과 50%% 이하여야 합니다. 할인율: %f%%", discountRate))
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A009);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1, 50.0})
    void 유효한_할인율로_PercentagePricePolicy_를_생성할_수_있다(double discountRate) {
        // expect
        assertThatNoException().isThrownBy(() -> new PercentagePricePolicy(discountRate));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 50.1})
    void 할인율이_0프로_이하이거나_50프로_초과이면_예외가_발생한다(double invalidRate) {
        // expect
        String message = String.format("할인율은 %d%% 초과 %d%% 이하여야 합니다. 할인율: %f%%", 0, 50, invalidRate);
        assertThatThrownBy(() -> new PercentagePricePolicy(invalidRate))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(message)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A009);
    }

    @Test
    void calculatePriceAtVariation_가격과_할인횟수가_주어지면_횟수만큼_할인이_적용된_가격을_계산한다() {
        // given
        PercentagePricePolicy percentagePricePolicy = new PercentagePricePolicy(10.0);
        long expected = 10000L;
        double discountFactor = (100 - 10.0) / 100.0;
        for (int i = 0; i < 10L; i++) {
            expected = (long) Math.floor(expected * discountFactor);
        }

        // when
        long result = percentagePricePolicy.calculatePriceAtVariation(10000L, 10L);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
