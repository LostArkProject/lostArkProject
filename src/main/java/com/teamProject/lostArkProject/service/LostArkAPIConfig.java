package com.teamProject.lostArkProject.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LostArkAPIConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://developer-lostark.game.onstove.com")
                .defaultHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDA0MjM4MjQifQ.dTM2m1Gt8HRkPrEz0rpE9L7ZssjomkuKcFqUyFFQl_q_DH8aUiKcXHDGKx-7B4AIDrgNGKvcW-LQcQ9rRrwUv0_vEpwIcNomD-Glns_eW8MphjYn1hcyLzSkp-KeJ2p1vQjOz3Mc5-W3dF2L79qW0K-Ai81eNnG_s_SiCQQYiKgusnmb6cDxXh3_vxXS7bpAE5SrNLhnd1Ky157MRZX5X--o6ginr2UtMwoTNSo13Y1RbQWFPWPKPDzz67JWzmdtFedzxN8SCNOAuQ0frQuinkqv-K8X5a8I_KrLSj3Sj7ccHMWmOeSxJ10kSyqGz1P3rD21Cu3J4fyl4xBe5tCYRg") // 여기에 자신의 API 키를 넣어야 합니다.
                .build();
    }
}