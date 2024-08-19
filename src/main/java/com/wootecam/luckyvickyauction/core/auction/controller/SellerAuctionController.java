package com.wootecam.luckyvickyauction.core.auction.controller;

import com.wootecam.luckyvickyauction.core.auction.controller.dto.SellerAuctionSearchRequest;
import com.wootecam.luckyvickyauction.core.auction.dto.CancelAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.controller.Login;
import com.wootecam.luckyvickyauction.core.member.controller.SellerOnly;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class SellerAuctionController {

    private final AuctionService auctionService;

    // 판매자는 경매를 생성한다.
    @PostMapping
    public ResponseEntity<Void> createAuction(@RequestBody CreateAuctionCommand command) {
        auctionService.createAuction(command);
        return ResponseEntity.ok().build();
    }


    /**
     * 판매자는 경매를 취소한다.
     *
     * @param auctionId 취소할 경매의 ID
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/171">Github issue</a>
     */
    @DeleteMapping("/{auctionId}")
    public void cancelAuction(
            @SessionAttribute("signInMember") Member member,
            @PathVariable("auctionId") Long auctionId
    ) {
        SignInInfo signInInfo = new SignInInfo(member.getId(), member.getRole());
        CancelAuctionCommand command = new CancelAuctionCommand(ZonedDateTime.now(), auctionId);
        auctionService.cancelAuction(signInInfo, command);
    }

    // 판매자는 자신이 등록한 경매 목록을 조회한다.
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerAuctionSimpleInfo>> getSellerAuctions(
            @Login SignInInfo sellerInfo,
            @RequestBody SellerAuctionSearchRequest request) {
        SellerAuctionSearchCondition condition = new SellerAuctionSearchCondition(
                sellerInfo.id(),
                request.offset(),
                request.size());
        List<SellerAuctionSimpleInfo> infos = auctionService.getSellerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    // 판매자는 자신이 등록한 경매를 상세 조회한다.
    @GetMapping("/{auctionId}/seller")
    public ResponseEntity<SellerAuctionInfo> getSellerAuction(@PathVariable("auctionId") Long auctionId) {
        SellerAuctionInfo sellerAuctionInfo = auctionService.getSellerAuction(auctionId);
        return ResponseEntity.ok(sellerAuctionInfo);
    }

    // 판매자는 자신의 경매 상품의 재고를 수정한다.
    @PatchMapping("/{auctionId}/stock")
    public void changeAuctionStock(@PathVariable Long auctionId, @RequestParam long amount) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

}
