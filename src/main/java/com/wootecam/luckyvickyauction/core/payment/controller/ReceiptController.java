package com.wootecam.luckyvickyauction.core.payment.controller;

import com.wootecam.luckyvickyauction.core.member.controller.BuyerOnly;
import com.wootecam.luckyvickyauction.core.member.controller.Login;
import com.wootecam.luckyvickyauction.core.member.controller.Roles;
import com.wootecam.luckyvickyauction.core.member.controller.SellerOnly;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.service.BidHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final BidHistoryService bidHistoryService;

    // 사용자는 자신의 거래 이력을 상세 조회할 수 있다.
    @Roles({Role.BUYER, Role.SELLER})
    @GetMapping("/{receiptId}")
    public ResponseEntity<BidHistoryInfo> getReceipt(@Login SignInInfo memberInfo,
                                                     @PathVariable("receiptId") Long receiptId) {
        BidHistoryInfo bidHistoryInfo = bidHistoryService.getBidHistoryInfo(memberInfo, receiptId);
        return ResponseEntity.ok(bidHistoryInfo);
    }

    // 구매자는 자신의 거래 이력 목록을 조회할 수 있다.
    @BuyerOnly
    @GetMapping("/buyer")
    public ResponseEntity<List<BuyerReceiptSimpleInfo>> getReceipts(@Login SignInInfo buyerInfo,
                                                                    @RequestBody BuyerReceiptSearchCondition condition) {
        List<BuyerReceiptSimpleInfo> infos = bidHistoryService.getBuyerReceiptSimpleInfos(buyerInfo, condition);
        return ResponseEntity.ok(infos);
    }

    // 판매자는 자신의 거래 이력 목록을 조회할 수 있다.
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerReceiptSimpleInfo>> getSellerReceipts(@Login SignInInfo sellerInfo,
                                                                           @RequestBody SellerReceiptSearchCondition condition) {
        List<SellerReceiptSimpleInfo> infos = bidHistoryService.getSellerReceiptSimpleInfos(sellerInfo, condition);
        return ResponseEntity.ok(infos);
    }
}
