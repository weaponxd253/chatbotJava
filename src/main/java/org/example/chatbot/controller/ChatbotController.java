package org.example.chatbot.controller;

import org.example.chatbot.model.ChatRequest;
import org.example.chatbot.model.ChatResponse;
import org.example.chatbot.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/chat")
    public String chat(@ModelAttribute ChatRequest chatRequest, Model model) {
        ChatResponse chatResponse = chatbotService.getChatResponse(chatRequest);
        model.addAttribute("response", chatResponse.getResponse());
        return "index";
    }
}


