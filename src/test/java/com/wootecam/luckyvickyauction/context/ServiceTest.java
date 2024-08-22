package com.wootecam.luckyvickyauction.context;

import com.wootecam.luckyvickyauction.core.auction.domain.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.service.MemberService;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptRepository;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import com.wootecam.luckyvickyauction.core.payment.service.ReceiptService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class ServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected AuctionRepository auctionRepository;

    @Autowired
    protected AuctionService auctionService;

    @Autowired
    protected Auctioneer auctioneer;

    @Autowired
    protected ReceiptRepository receiptRepository;

    @Autowired
    protected PaymentService paymentService;

    @Autowired
    protected ReceiptService receiptService;

    @AfterEach
    void tearDown() {
        databaseCleaner.clear();
    }
}
