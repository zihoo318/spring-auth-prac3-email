package com.example.emailAuth.emailSender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ConfigurationProperties(prefix = "app.email") // 자동으로 properties에서 app.email.~을 찾아서 같은 이름에 값을 주입
public class EmailProperties {
    private String verifyBaseUrl;        // http://localhost:8080/auth/email
    private int tokenExpireMinutes = 15; // 기본 15분
    private List<String> allowedDomains = new ArrayList<>(); // ["innogrid.com"]
}