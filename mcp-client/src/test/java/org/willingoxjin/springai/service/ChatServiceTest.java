package org.willingoxjin.springai.service;


import jakarta.annotation.Resource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author Jin.Nie
 */
@SpringBootTest
class ChatServiceTest {

    @Resource
    private ChatService chatService;

    @ParameterizedTest
    @ValueSource(strings = { "你是谁？"})
    void testChat(String prompt) {
        String response = chatService.chatTest(prompt);
        System.out.println(response);
    }

}