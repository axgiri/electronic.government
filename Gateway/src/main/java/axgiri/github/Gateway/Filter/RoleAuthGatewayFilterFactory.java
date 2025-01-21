package axgiri.github.Gateway.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import axgiri.github.Gateway.DTO.AuthRequestDTO;
import axgiri.github.Gateway.Service.AuthService;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class RoleAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Autowired
    private AuthService authService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public RoleAuthGatewayFilterFactory() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String[] segments = path.split("/");
            if (segments.length < 4) {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
            String routeRole = segments[3];
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (token == null || token.isEmpty()) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Map<String, String> queryMap = exchange.getRequest().getQueryParams().toSingleValueMap();
            Long companyId = parseLong(queryMap.get("companyId")); 
            Long projectId = parseLong(queryMap.get("projectId"));
            Mono<Long> companyIdMono;
            if ((companyId == null || companyId == 0) && projectId != null && projectId != 0) {
                companyIdMono = fetchCompanyIdByProjectId(projectId);
            } else {
                companyIdMono = Mono.justOrEmpty(companyId);
            }

            return companyIdMono.flatMap(finalCompanyId -> {
                if (finalCompanyId == null || finalCompanyId == 0) {
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    return exchange.getResponse().setComplete();
                }

                AuthRequestDTO dto = new AuthRequestDTO();
                dto.setToken(token);
                dto.setCompanyId(finalCompanyId);
                dto.setRouteRole(routeRole);

                boolean allowed;
                try {
                    allowed = authService.checkAuth(dto);
                } catch (Exception e) {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                }

                if (!allowed) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                return chain.filter(exchange);
            });
        };
    }

    private Long parseLong(String value) {
        if (value == null) return null;
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Mono<Long> fetchCompanyIdByProjectId(Long projectId) {
        String url = "http://host.docker.internal:8083/api/projects/public/getCompanyByProject/" + projectId;
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Long.class)
                .onErrorResume(ex -> {
                    return Mono.empty();
                });
    }
}
