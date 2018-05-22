package com.t2systems.ps.controllers;

import com.t2systems.ps.constants.TestEndpoints;
import com.t2systems.ps.security.Auth;
import com.t2systems.model.TokenUser;
import com.t2systems.security.PermissionEnum;
import com.t2systems.test.RequestBuilder;
import com.t2systems.test.assertions.OAuth2AssertionBuilder;
import com.t2systems.test.security.MockAuthoritiesResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles({ "test" })
public class PermitAuthoritiesTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy securityFilter;

    @Autowired
    private OAuth2AssertionBuilder assertionsBuilder;

    @Autowired
    private MockAuthoritiesResolver authoritiesResolver;

    @Before
    public void setUp() {
        this.authoritiesResolver.clear();

        final MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(this.securityFilter).build();

        this.assertionsBuilder.mockMvc(mockMvc).allScopes(TokenUser.AuthType.values()).allPermissions(Auth.values());
    }

    @Test
    public void viewPermitsByTransactonType() throws Exception {
        assertEither(() -> get(TestEndpoints.PERMIT_MAPPING + TestEndpoints.GET_TRANSACTION_TYPE_PERMIT_MAPPING),
                TokenUser.AuthType.MICROSERVICE, Auth.VIEW_PERMIT_TRANSACTION);
    }

    private void assertEither(final RequestBuilder requestBuilder, final TokenUser.AuthType scope,
            final PermissionEnum permission) throws Exception {
        this.assertionsBuilder.requestBuilder(requestBuilder).excludeFromAllPermissions(permission)
                .requiredScopes(scope).assertAll();

        this.assertionsBuilder.requestBuilder(requestBuilder).excludeFromAllScopes(scope)
                .requiredPermissions(permission).assertAll();
    }
}
