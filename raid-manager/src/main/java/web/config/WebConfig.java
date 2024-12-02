package web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/m/**")           // 해당 경로의 요청이 올 때
                .addResourceLocations("classpath:/m/") // classpath 기준으로 'm' 디렉토리 밑에서 제공
                .setCachePeriod(20);                   // 캐싱 지정
    }
}