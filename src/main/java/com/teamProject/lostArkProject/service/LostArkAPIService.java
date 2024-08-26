package com.teamProject.lostArkProject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.domain.CharacterInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class LostArkAPIService {

    private final WebClient webClient;

    public LostArkAPIService(WebClient webClient) {
        this.webClient = webClient;
    }

//    public Mono<String> getCharacterInfo(String characterName) {
//        return webClient.get()
//                .uri("/characters/" + characterName + "/siblings")
//                .retrieve()
//                .bodyToMono(String.class)
//    }

    public Mono<List<CharacterInfo>> getCharacterInfo(String characterName) {
        return webClient.get()
                .uri("/characters/" + characterName + "/siblings")
                .retrieve()
                .bodyToMono(String.class)
                /* flatMap(): json 방식의 데이터를 CharacterInfo 타입의 List로 변환하는 역할을 하는 메소드
                 *
                 * 1. API 요청 결과로 얻은 json 문자열의 키를 ObjectMapper를 사용하여 CharacterInfo 클래스의 필드와 매핑
                 * 2. json의 각 요소가 CharacterInfo 클래스의 인스턴스로 변환 (이 인스턴스의 필드에는 json에서 추출한 값들이 할당)
                 * 3. CharacterInfo 객체들이 모여 하나의 List<CharacterInfo>를 생성하고 characterInfos 변수에 저장
                 * 4. getter 메소드를 이용해 각 필드의 값을 추출할 수 있음
                 */
                .flatMap(response -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        List<CharacterInfo> characterInfos = objectMapper.readValue(response, new TypeReference<List<CharacterInfo>>() {});
                        return Mono.just(characterInfos);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .doOnNext(data -> System.out.println("API 응답: " + data));
    }
}