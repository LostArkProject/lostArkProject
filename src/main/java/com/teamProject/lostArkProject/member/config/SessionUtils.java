package com.teamProject.lostArkProject.member.config;

import com.teamProject.lostArkProject.common.exception.UnauthorizedException;
import com.teamProject.lostArkProject.member.domain.Member;
import jakarta.servlet.http.HttpSession;

/** 세션 관리를 위한 유틸 클래스 */
public class SessionUtils {
    /** member 객체를 전달하는 전역 메서드 */
    public static Member getMember(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        return member;
    }
    
    /** memberId를 전달하는 전역 메서드 */
    public static String getMemberId(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null || member.getMemberId() == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        return member.getMemberId();
    }
}
