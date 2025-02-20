package com.wootecam.luckyvickyauction.core.member.controller;

import com.wootecam.luckyvickyauction.core.member.controller.dto.SignInRequestInfo;
import com.wootecam.luckyvickyauction.core.member.controller.dto.SignInResponseInfo;
import com.wootecam.luckyvickyauction.core.member.controller.dto.SignUpRequestInfo;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // 신규 회원은 회원가입을 한다.
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignUpRequestInfo request) {
        memberService.signUp(request);

        return ResponseEntity.ok()
                .build();
    }

    // 사용자는 로그인을 한다.
    @PostMapping("/signin")
    public ResponseEntity<SignInResponseInfo> signin(@RequestBody SignInRequestInfo request, HttpSession session) {
        SignInInfo signInInfo = memberService.signIn(request);
        session.setAttribute("signInMember", signInInfo);

        SignInResponseInfo signInResponseInfo = new SignInResponseInfo(signInInfo.role());
        return ResponseEntity.ok(signInResponseInfo);
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
