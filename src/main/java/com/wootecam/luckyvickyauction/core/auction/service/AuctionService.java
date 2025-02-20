package com.wootecam.luckyvickyauction.core.auction.service;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.CancelAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.exception.AuthorizationException;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.util.Mapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    @Transactional
    public void createAuction(SignInInfo sellerInfo, CreateAuctionCommand command) {
        Auction auction = Auction.builder()
                .sellerId(sellerInfo.id())
                .productName(command.productName())
                .currentPrice(command.originPrice())
                .originPrice(command.originPrice())
                .currentStock(command.stock())
                .originStock(command.stock())
                .maximumPurchaseLimitCount(command.maximumPurchaseLimitCount())
                .pricePolicy(command.pricePolicy())
                .variationDuration(command.variationDuration())
                .startedAt(command.startedAt())
                .finishedAt(command.finishedAt())
                .isShowStock(command.isShowStock())
                .build();
        auctionRepository.save(auction);
    }

    /**
     * 경매 시작 전에는 경매를 취소할 수 있다.
     *
     * @param signInInfo 경매를 취소하려는 사용자 정보
     * @param command    취소할 경매 정보
     */
    @Transactional
    public void cancelAuction(SignInInfo signInInfo, CancelAuctionCommand command) {
        if (!signInInfo.isType(Role.SELLER)) {
            throw new AuthorizationException("판매자만 경매를 취소할 수 있습니다.", ErrorCode.A017);
        }

        Auction auction = findAuctionObject(command.auctionId());

        if (!auction.isSeller(signInInfo.id())) {
            throw new AuthorizationException("자신이 등록한 경매만 취소할 수 있습니다.", ErrorCode.A018);
        }
        if (!auction.currentStatus(command.requestTime()).isWaiting()) {
            String message = String.format("시작 전인 경매만 취소할 수 있습니다. 시작시간=%s, 요청시간=%s", auction.getStartedAt(),
                    command.requestTime());
            throw new BadRequestException(message, ErrorCode.A019);
        }

        auctionRepository.deleteById(command.auctionId());
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 진행한다.
     *
     * @param auctionId   경매 번호
     * @param price       구매를 원하는 가격
     * @param quantity    수량
     * @param requestTime 요청 시간
     */
    @Transactional
    public void submitPurchase(long auctionId, long price, long quantity, LocalDateTime requestTime) {
        Auction auction = findAuctionObject(auctionId);
        auction.submit(price, quantity, requestTime);
        auctionRepository.save(auction);
    }

    /**
     * 경매 상품에 대한 입찰 취소를 진행한다. - 경매 도메인 내에서 이를 quantity에 대한 검증 로직을 넣었습니다.
     *
     * @param auctionId 경매 아이디
     * @param quantity  환불할 수량
     */
    @Transactional
    public void cancelPurchase(long auctionId, long quantity) {
        Auction auction = findAuctionObjectForUpdate(auctionId);
        auction.refundStock(quantity);
        auctionRepository.save(auction);
    }

    private Auction findAuctionObjectForUpdate(long auctionId) {
        return auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
    }

    private Auction findAuctionObject(long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
    }

    /**
     * 경매 단건 조회
     */
    public AuctionInfo getAuction(long auctionId) {
        Auction auction = findAuctionObject(auctionId);

        return Mapper.convertToAuctionInfo(auction);
    }

    /**
     * 구매자용 경매 조회
     *
     * @param auctionId 경매 ID
     * @return 구매자용 경매 정보
     */
    public BuyerAuctionInfo getBuyerAuction(long auctionId) {
        Auction auction = findAuctionObject(auctionId);

        return Mapper.convertToBuyerAuctionInfo(auction);
    }

    /**
     * 판매자용 경매 조회
     *
     * @param auctionId 경매 ID
     * @return 판매자용 경매 정보
     */
    public SellerAuctionInfo getSellerAuction(SignInInfo sellerInfo, long auctionId) {
        Auction auction = findAuctionObject(auctionId);

        if (!auction.isSeller(sellerInfo.id())) {
            throw new AuthorizationException("판매자는 자신이 등록한 경매만 조회할 수 있습니다.", ErrorCode.A020);
        }

        return Mapper.convertToSellerAuctionInfo(auction);
    }

    /**
     * 구매자용 경매 목록 조회
     *
     * @param condition
     * @return 구매자용 경매 목록
     */
    public List<BuyerAuctionSimpleInfo> getBuyerAuctionSimpleInfos(AuctionSearchCondition condition) {
        return auctionRepository.findAllBy(condition).stream()
                .map(Mapper::convertToBuyerAuctionSimpleInfo)
                .toList();
    }

    /**
     * 판매자용 경매 목록 조회
     *
     * @param condition
     * @return 판매자용 경매 목록
     */
    public List<SellerAuctionSimpleInfo> getSellerAuctionSimpleInfos(SellerAuctionSearchCondition condition) {
        return auctionRepository.findAllBy(condition).stream()
                .map(Mapper::convertToSellerAuctionSimpleInfo)
                .toList();
    }

    public AuctionInfo getAuctionForUpdate(long auctionId) {
        Auction auction = findAuctionObjectForUpdate(auctionId);

        return Mapper.convertToAuctionInfo(auction);
    }
}
