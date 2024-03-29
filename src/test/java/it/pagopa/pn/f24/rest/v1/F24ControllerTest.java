package it.pagopa.pn.f24.rest.v1;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Response;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.NumberOfPagesResponse;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.RequestAccepted;
import it.pagopa.pn.f24.service.F24ParserService;
import it.pagopa.pn.f24.service.F24Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@ContextConfiguration(classes = {F24Controller.class})
@ExtendWith(SpringExtension.class)
class F24ControllerTest {
    @MockBean
    private F24Service f24Service;
    @MockBean
    private F24ParserService f24ParserService;
    @Autowired
    private F24Controller f24Controller;
    @MockBean
    private Scheduler scheduler;

    /**
     * Method under test: {@link F24Controller#saveMetadata(String, String, Mono, ServerWebExchange)}
     */
    @Test
    void testSaveMetadata() {
        when(f24Service.saveMetadata(any(), any(), any()))
                .thenReturn((Mono<RequestAccepted>) mock(Mono.class));
        ServerHttpRequestDecorator serverHttpRequestDecorator = mock(ServerHttpRequestDecorator.class);
        when(serverHttpRequestDecorator.getHeaders()).thenReturn(new HttpHeaders());
        when(serverHttpRequestDecorator.getId()).thenReturn("https://example.org/example");
        WebSessionManager webSessionManager = mock(WebSessionManager.class);
        when(webSessionManager.getSession(any())).thenReturn((Mono<WebSession>) mock(Mono.class));
        MockServerHttpResponse response = new MockServerHttpResponse();
        DefaultServerCodecConfigurer codecConfigurer = new DefaultServerCodecConfigurer();
        f24Controller.saveMetadata("42", "42", null, new DefaultServerWebExchange(serverHttpRequestDecorator, response,
                webSessionManager, codecConfigurer, new AcceptHeaderLocaleContextResolver()));
        verify(f24Service).saveMetadata(any(), any(), any());
        verify(serverHttpRequestDecorator).getId();
        verify(serverHttpRequestDecorator, atLeast(1)).getHeaders();
        verify(webSessionManager).getSession(any());
    }

    /**
     * Method under test: {@link F24Controller#validateMetadata(String, String, Mono, ServerWebExchange)}
     */
    @Test
    void testValidateMetadata() {
        when(f24Service.validate(any(), any())).thenReturn((Mono<RequestAccepted>) mock(Mono.class));
        ServerHttpRequestDecorator serverHttpRequestDecorator = mock(ServerHttpRequestDecorator.class);
        when(serverHttpRequestDecorator.getHeaders()).thenReturn(new HttpHeaders());
        when(serverHttpRequestDecorator.getId()).thenReturn("https://example.org/example");
        WebSessionManager webSessionManager = mock(WebSessionManager.class);
        when(webSessionManager.getSession(any())).thenReturn((Mono<WebSession>) mock(Mono.class));
        MockServerHttpResponse response = new MockServerHttpResponse();
        DefaultServerCodecConfigurer codecConfigurer = new DefaultServerCodecConfigurer();
        f24Controller.validateMetadata("42", "42", null, new DefaultServerWebExchange(serverHttpRequestDecorator,
                response, webSessionManager, codecConfigurer, new AcceptHeaderLocaleContextResolver()));
        verify(f24Service).validate(any(), any());
        verify(serverHttpRequestDecorator).getId();
        verify(serverHttpRequestDecorator, atLeast(1)).getHeaders();
        verify(webSessionManager).getSession(any());
    }

    /**
     * Method under test: {@link F24Controller#generatePDF(String, String, List, Integer, ServerWebExchange)}
     */
    @Test
    void testGeneratePDF() {
        when(f24Service.generatePDF(any(), any(), any(), any()))
                .thenReturn((Mono<F24Response>) mock(Mono.class));
        ServerHttpRequestDecorator serverHttpRequestDecorator = mock(ServerHttpRequestDecorator.class);
        when(serverHttpRequestDecorator.getHeaders()).thenReturn(new HttpHeaders());
        when(serverHttpRequestDecorator.getId()).thenReturn("https://example.org/example");
        WebSessionManager webSessionManager = mock(WebSessionManager.class);
        when(webSessionManager.getSession(any())).thenReturn((Mono<WebSession>) mock(Mono.class));
        MockServerHttpResponse response = new MockServerHttpResponse();
        DefaultServerCodecConfigurer codecConfigurer = new DefaultServerCodecConfigurer();
        f24Controller.generatePDF("42", "42", List.of("0","0"), 1000, new DefaultServerWebExchange(serverHttpRequestDecorator,
                response, webSessionManager, codecConfigurer, new AcceptHeaderLocaleContextResolver()));
        verify(f24Service).generatePDF(any(), any(), any(), any());
        verify(serverHttpRequestDecorator).getId();
        verify(serverHttpRequestDecorator, atLeast(1)).getHeaders();
        verify(webSessionManager).getSession(any());
    }


    /**
     * Method under test: {@link F24Controller#preparePDF(String, String, Mono, ServerWebExchange)}
     */
    @Test
    void testPreparePDF() {
        when(f24Service.preparePDF(any(), any(), any()))
                .thenReturn((Mono<RequestAccepted>) mock(Mono.class));
        ServerHttpRequestDecorator serverHttpRequestDecorator = mock(ServerHttpRequestDecorator.class);
        when(serverHttpRequestDecorator.getHeaders()).thenReturn(new HttpHeaders());
        when(serverHttpRequestDecorator.getId()).thenReturn("https://example.org/example");
        WebSessionManager webSessionManager = mock(WebSessionManager.class);
        when(webSessionManager.getSession(any())).thenReturn((Mono<WebSession>) mock(Mono.class));
        MockServerHttpResponse response = new MockServerHttpResponse();
        DefaultServerCodecConfigurer codecConfigurer = new DefaultServerCodecConfigurer();
        f24Controller.preparePDF("42", "42", null, new DefaultServerWebExchange(serverHttpRequestDecorator,
                response, webSessionManager, codecConfigurer, new AcceptHeaderLocaleContextResolver()));
        verify(f24Service).preparePDF(any(), any(), any());
        verify(serverHttpRequestDecorator).getId();
        verify(serverHttpRequestDecorator, atLeast(1)).getHeaders();
        verify(webSessionManager).getSession(any());
    }

    /**
     * Method under test:
     * {@link F24Controller#getTotalNumberOfPages(String, List, ServerWebExchange)}
     */
    @Test
    void testGetTotalNumberOfPages() {
        when(f24ParserService.getTotalPagesFromMetadataSet(any(), any())).thenReturn((Mono<NumberOfPagesResponse>) mock(Mono.class));
        ServerHttpRequestDecorator serverHttpRequestDecorator = mock(ServerHttpRequestDecorator.class);
        when(serverHttpRequestDecorator.getHeaders()).thenReturn(new HttpHeaders());
        when(serverHttpRequestDecorator.getId()).thenReturn("https://example.org/example");
        MockServerHttpResponse response = new MockServerHttpResponse();
        WebSessionManager webSessionManager = mock(WebSessionManager.class);
        when(webSessionManager.getSession(any())).thenReturn((Mono<WebSession>) mock(Mono.class));
        DefaultServerCodecConfigurer codecConfigurer = new DefaultServerCodecConfigurer();
        f24Controller.getTotalNumberOfPages("42", List.of("42"), new DefaultServerWebExchange(serverHttpRequestDecorator,
                response, webSessionManager, codecConfigurer, new AcceptHeaderLocaleContextResolver()));
        verify(f24ParserService).getTotalPagesFromMetadataSet(any(), any());
        verify(serverHttpRequestDecorator).getId();
        verify(serverHttpRequestDecorator, atLeast(1)).getHeaders();
        verify(webSessionManager).getSession(any());
    }
}

