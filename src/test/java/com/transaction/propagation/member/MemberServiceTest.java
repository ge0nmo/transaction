package com.transaction.propagation.member;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;

    @Autowired MemberRepository memberRepository;

    @Autowired LogRepository logRepository;

    /***
     * memberService        @Transactional:OFF
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON
     */

    @Test
    void outerTxOff_success() {
        // given
        String username= "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isPresent());
    }

    /***
     * memberService        @Transactional:OFF
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON Exception
     */

    @Test
    void outerTxOff_fail() {
        // given
        String username= "로그예외_outerTxOff_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }

    /***
     * memberService        @Transactional:OFF
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON
     */

    @Test
    void single_tx() {
        // given
        String username= "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isPresent());
    }

    /***
     * memberService        @Transactional:ON
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON
     */

    @Test
    void outerTxOn_success() {
        // given
        String username= "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isPresent());
    }


    /***
     * memberService        @Transactional:ON
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON Exception
     */

    @Test
    void outerTxOn_fail() {
        // given
        String username= "로그예외_outerTxOn_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then
        assertTrue(memberRepository.findByUsername(username).isEmpty());
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }


    /***
     * memberService        @Transactional:ON
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON Exception
     */

    @Test
    void recoverException_fail() {
        // given
        String username= "로그예외_recoverException_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // then
        assertTrue(memberRepository.findByUsername(username).isEmpty());
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }


    /***
     * memberService        @Transactional:ON
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON (REQUIRES_NEW) Exception
     */

    @Test
    void recoverException_success() {
        // given
        String username= "로그예외_recoverException_success";

        // when
        memberService.joinV2(username);

        // then
        // save member // rollback log
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }
}