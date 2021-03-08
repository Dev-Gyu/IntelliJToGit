package com.studyolle.studyolle.infra.config;

import com.studyolle.studyolle.modules.notification.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration // spring boot에선 enableWebMvc 주면 안됨. -> SpringBoot가 제공해주는 기본설정 안쓰게됨
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final NotificationInterceptor notificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // StaticResourceLocation = PathRequest.toStaticResources().atCommonLocations() 에서 사용하는 enum값들
        // Spring Security에서 WebSecurity 에 static 요청에는 security적용 안하기위해 사용. 역시 interceptor도 적용하지 않도록 하기 위함
        List<String> staticResourcesPath = Arrays.stream(StaticResourceLocation.values())
                                            .flatMap(StaticResourceLocation::getPatterns).collect(Collectors.toList());
        staticResourcesPath.add("/node_modules/**");

        InterceptorRegistration ir = registry.addInterceptor(notificationInterceptor);
        ir.excludePathPatterns(staticResourcesPath);
    }
}
