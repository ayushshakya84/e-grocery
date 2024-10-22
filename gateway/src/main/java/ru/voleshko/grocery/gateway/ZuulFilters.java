package ru.voleshko.grocery.gateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class ZuulFilters {

    @Bean
    public ZuulFilter keycloakFilterRoute() {
        return new KeycloakFilterRoute();
    }

    /**
     * Zuul filter to add JWT token to Authorization header when proxying request to microservices
     */
    static class KeycloakFilterRoute extends ZuulFilter {

        @Override
        public String filterType() {
            return "route";
        }

        @Override
        public int filterOrder() {
            return 0;
        }

        @Override
        public boolean shouldFilter() {
            return true;
        }

        @Override
        public Object run() {
            RequestContext ctx = RequestContext.getCurrentContext();
            if (ctx.getRequest().getHeader(HttpHeaders.AUTHORIZATION) == null) {
                addKeycloakTokenToHeader(ctx);
            }
            return null;
        }

        private void addKeycloakTokenToHeader(RequestContext ctx) {
            RefreshableKeycloakSecurityContext securityContext = getRefreshableKeycloakSecurityContext(ctx);
            if (securityContext != null) {
                ctx.addZuulRequestHeader(HttpHeaders.AUTHORIZATION, buildBearerToken(securityContext));
            }
        }

        private RefreshableKeycloakSecurityContext getRefreshableKeycloakSecurityContext(RequestContext ctx) {
            if (ctx.getRequest().getUserPrincipal() instanceof KeycloakAuthenticationToken) {
                KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) ctx.getRequest().getUserPrincipal();
                return (RefreshableKeycloakSecurityContext) token.getCredentials();
            }
            return null;
        }

        private String buildBearerToken(RefreshableKeycloakSecurityContext securityContext) {
            return "Bearer " + securityContext.getTokenString();
        }
    }

}
