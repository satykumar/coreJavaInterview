package com.t2systems.ps.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.t2systems.model.TokenCustomer;
import com.t2systems.model.TokenUser;
import com.t2systems.ps.PermitServiceApplication;
import com.t2systems.ps.client.CoreCpsServiceClient;
import com.t2systems.ps.client.RateServiceClient;
import com.t2systems.ps.client.TopologyServiceClient;
import com.t2systems.ps.constants.Beans;
import com.t2systems.ps.constants.Domains;
import com.t2systems.ps.constants.TestEndpoints;
import com.t2systems.ps.models.PermitTransaction;
import com.t2systems.ps.models.TransactionType;
import com.t2systems.ps.repositories.PermitTransactionRepository;
import com.t2systems.ps.repositories.TransactionTypeRepository;
import com.t2systems.ps.request.LocationValidationRequest;
import com.t2systems.ps.request.PaymentRequest;
import com.t2systems.ps.request.PermitTransactionRequest;
import com.t2systems.ps.request.ThirdPartyPaymentsList;
import com.t2systems.ps.responses.LocationValidationResponse;
import com.t2systems.ps.responses.ObjectResponse;
import com.t2systems.ps.responses.RateDetailsResponse;
import com.t2systems.ps.responses.ThirdPartyPayment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PermitServiceApplication.class)
@ActiveProfiles({ "test" })
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionControllerTest {
    
    private static final String PERMIT_TRANSACTION_ENDPOINT = "/api/v1/permits/transaction";
    
    private static final String RESPONSE_STATUS = "$.status.responseStatus";
    
    private static final String IDENTIFIER = "$.status.errors[0].identifier";
    
    private static final String MESSAGE = "$.status.errors[0].message";
    
    private static final String FORWARD_SLASH = "/";
    
    @MockBean
    private TransactionTypeRepository transactionRepository;
    
    @MockBean
    private RateServiceClient rateServiceClient;
    
    @MockBean
    private TopologyServiceClient topologyServiceClient;
    
    @MockBean
    private PermitTransactionRepository permitTransactionRepository;
    
    @MockBean
    private CoreCpsServiceClient coreCpsServiceClient;
    /**
     * mockMvc , not null.
     */
    private MockMvc mockMvc;
    
    /**
     * wac , not null.
     */
    @Autowired
    private WebApplicationContext wac;
    
    private MockHttpServletRequestBuilder requestBuilder;
    
    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        final TokenCustomer.Builder customer = new TokenCustomer.Builder().setId(2).setDefault(true);
        final TokenUser testUser = new TokenUser.Builder().setId(1).setName("testUser").appendCustomer(customer).create();
        final Authentication authentication = mock(Authentication.class);
        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
    }
    
    @Test
    public void testCreatePermitTransactionInvalidTransactionDate() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setActiveDate(new Date());
        request.setExpiryDate(new Date());
        Thread.sleep(2000);
        request.setTransactionDate(new Date());
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PERMIT))
        .andExpect(jsonPath(MESSAGE).value(Beans.PREMIT_TRANSACTION_DATE_ERROR)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionInvalidActiveDate() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        Thread.sleep(2000);
        request.setActiveDate(new Date());
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PERMIT))
        .andExpect(jsonPath(MESSAGE).value(Beans.PREMIT_ACTIVE_DATE_ERROR)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionInvalidPlateNumber() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber(" ");
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PERMIT))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_PLATE_NUMBER)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionInvalidSpaceId() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(0);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PERMIT))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_SPACE_ID)).andDo(print());
    }
       
    @Test
    public void testCreatePermitTransactionInvalidAmount() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setAmount(new BigDecimal(0));
        request.setSpaceId(10);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PERMIT))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_TOTAL_AMMOUNT_ERROR)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionEmptyPayments() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        final List<PaymentRequest> payments = new ArrayList<>();
        request.setPayments(payments);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PAYMENT))
        .andExpect(jsonPath(MESSAGE).value(Beans.EMPTY_PAYMENTS_ERROR)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionInvalidPaymentsAmt() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(8);
        payments.add(payment);
        request.setPayments(payments);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PAYMENT))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_TOTAL_AMMOUNT_SUM_ERROR)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionInvalidVendorId() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setVendorId(2);
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PAYMENT))
        .andExpect(jsonPath(MESSAGE).value(Beans.VENDOR_ID_MISMATCH_ERROR_MSG)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionInvalidTransactionUuid() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setVendorId(1);
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payment.setTransactionUuid(UUID.randomUUID());
        payments.add(payment);
        request.setPayments(payments);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PAYMENT))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_TRANSACTION_UUID + payment.getTransactionUuid().toString())).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionInvalidPaymentCustomerId() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setVendorId(1);
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payment.setCustomerId(1);
        payments.add(payment);
        request.setPayments(payments);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON).content(myString)
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PAYMENT))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_CUSTOMER_ID + payment.getCustomerId())).andDo(print());
    }
    
    private PaymentRequest populatePaymentRequest(final UUID uuid) {
        final PaymentRequest payment = new PaymentRequest();
        payment.setAuthorizationNumber("1234");
        payment.setCardType("Credit");
        payment.setCustomerId(2);
        payment.setLast4Digits("123");
        payment.setCardExpiry("2020");
        payment.setProductType("permit");
        payment.setPaymentType("charge");
        payment.setTransactionUuid(uuid);
        payment.setProcessorTransactionId("12");
        payment.setPurchaseUTC(new Date());
        payment.setVendorId(1);
        payment.setDeviceId("D121");
        return payment;
    }

    @Test
    public void testCreatePermitTransactionInvalidTransactionTypeId() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setVendorId(1);
        request.setPayments(payments);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(null);
        this.mockMvc.perform(requestBuilder).andExpect(status().isNotFound())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.PERMIT))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_TRANSACTION_TYPE)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionInvalidRateId() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        final ResponseEntity<ObjectResponse<RateDetailsResponse>> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(response);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.RATE))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_RATE_ID)).andDo(print());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCreatePermitTransactionUnableTCoonectRateService() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenThrow(HystrixRuntimeException.class);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.RATE))
        .andExpect(jsonPath(MESSAGE).value(Beans.INVALID_RATE_ID)).andDo(print());
    }

    @Test
    public void testCreatePermitTransactionInvalidTopology() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        final ResponseEntity<ObjectResponse<RateDetailsResponse>> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(response);
        
        final ObjectResponse<LocationValidationResponse> body = new ObjectResponse<>();
        final LocationValidationResponse objRes = new LocationValidationResponse();
        objRes.setIsValid(Boolean.FALSE);
        body.setResponse(objRes);
        final ResponseEntity<ObjectResponse<LocationValidationResponse>> topologyResponse = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        Mockito.when(this.topologyServiceClient.validateCustomerRelationship(Mockito.any(LocationValidationRequest.class))).thenReturn(topologyResponse);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
        .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.TOPOLOGY))
        .andExpect(jsonPath(MESSAGE).value(Beans.TOPOLOGY_ERROR)).andDo(print());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCreatePermitTransactionUnableTCoonectTopologyService() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        final ResponseEntity<ObjectResponse<RateDetailsResponse>> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(response);
        Mockito.when(this.topologyServiceClient.validateCustomerRelationship(Mockito.any(LocationValidationRequest.class)))
                .thenThrow(HystrixRuntimeException.class);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
                .andExpect(jsonPath(IDENTIFIER).value(Domains.TOPOLOGY)).andExpect(jsonPath(MESSAGE).value(Beans.INVALID_LOCATION_ID)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionCoreCpsFailure() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        final ResponseEntity<ObjectResponse<RateDetailsResponse>> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(response);
        final ObjectResponse<LocationValidationResponse> body = new ObjectResponse<>();
        final LocationValidationResponse objRes = new LocationValidationResponse();
        objRes.setIsValid(Boolean.TRUE);
        body.setResponse(objRes);
        final ResponseEntity<ObjectResponse<LocationValidationResponse>> topologyResponse = new ResponseEntity<>(body, HttpStatus.OK);
        Mockito.when(this.topologyServiceClient.validateCustomerRelationship(Mockito.any(LocationValidationRequest.class))).thenReturn(topologyResponse);
        final PermitTransaction permitResponse = new PermitTransaction(1);
        Mockito.when(this.permitTransactionRepository.save(Mockito.any(PermitTransaction.class))).thenReturn(permitResponse);
        final ResponseEntity<ObjectResponse<String>> coreCpsResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Mockito.when(this.coreCpsServiceClient.record(Mockito.any(ThirdPartyPaymentsList.class))).thenReturn(coreCpsResponse);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.CPS))
        .andExpect(jsonPath(MESSAGE).value(Beans.PAYMENT_SAVE_ERROR + request.getCustomerId())).andDo(print());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCreatePermitTransactionUnableToConnectCPS() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        final ResponseEntity<ObjectResponse<RateDetailsResponse>> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(response);
        final ObjectResponse<LocationValidationResponse> body = new ObjectResponse<>();
        final LocationValidationResponse objRes = new LocationValidationResponse();
        objRes.setIsValid(Boolean.TRUE);
        body.setResponse(objRes);
        final ResponseEntity<ObjectResponse<LocationValidationResponse>> topologyResponse = new ResponseEntity<>(body, HttpStatus.OK);
        Mockito.when(this.topologyServiceClient.validateCustomerRelationship(Mockito.any(LocationValidationRequest.class))).thenReturn(topologyResponse);
        final PermitTransaction permitResponse = new PermitTransaction(1);
        Mockito.when(this.permitTransactionRepository.save(Mockito.any(PermitTransaction.class))).thenReturn(permitResponse);
        Mockito.when(this.coreCpsServiceClient.record(Mockito.any(ThirdPartyPaymentsList.class))).thenThrow(HystrixRuntimeException.class);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andExpect(jsonPath(RESPONSE_STATUS).value(Domains.FAILURE))
        .andExpect(jsonPath(IDENTIFIER).value(Domains.CPS))
        .andExpect(jsonPath(MESSAGE).value(Beans.PAYMENT_SAVE_ERROR + request.getCustomerId())).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionSuccessWithoutPayments() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        final ResponseEntity<ObjectResponse<RateDetailsResponse>> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(response);
        final ObjectResponse<LocationValidationResponse> body = new ObjectResponse<>();
        final LocationValidationResponse objRes = new LocationValidationResponse();
        objRes.setIsValid(Boolean.TRUE);
        body.setResponse(objRes);
        final ResponseEntity<ObjectResponse<LocationValidationResponse>> topologyResponse = new ResponseEntity<>(body, HttpStatus.OK);
        Mockito.when(this.topologyServiceClient.validateCustomerRelationship(Mockito.any(LocationValidationRequest.class))).thenReturn(topologyResponse);
        final PermitTransaction permitResponse = new PermitTransaction(1);
        Mockito.when(this.permitTransactionRepository.save(Mockito.any(PermitTransaction.class))).thenReturn(permitResponse);
        this.mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath(RESPONSE_STATUS).value(Domains.SUCCESS)).andDo(print());
    }
    
    @Test
    public void testCreatePermitTransactionSuccess() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        final ResponseEntity<ObjectResponse<RateDetailsResponse>> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(response);
        final ObjectResponse<LocationValidationResponse> body = new ObjectResponse<>();
        final LocationValidationResponse objRes = new LocationValidationResponse();
        objRes.setIsValid(Boolean.TRUE);
        body.setResponse(objRes);
        final ResponseEntity<ObjectResponse<LocationValidationResponse>> topologyResponse = new ResponseEntity<>(body, HttpStatus.OK);
        Mockito.when(this.topologyServiceClient.validateCustomerRelationship(Mockito.any(LocationValidationRequest.class))).thenReturn(topologyResponse);
        final PermitTransaction permitResponse = new PermitTransaction(1);
        Mockito.when(this.permitTransactionRepository.save(Mockito.any(PermitTransaction.class))).thenReturn(permitResponse);
        final ResponseEntity<ObjectResponse<String>> coreCpsResponse = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.coreCpsServiceClient.record(Mockito.any(ThirdPartyPaymentsList.class))).thenReturn(coreCpsResponse);
        this.mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath(RESPONSE_STATUS).value(Domains.SUCCESS)).andDo(print());
    }
    
    /**
     * @param uuid 
     * @return
     */
    private PermitTransactionRequest populatePermitTransactionRequest(final UUID uuid) {
        final PermitTransactionRequest request = new PermitTransactionRequest();
        request.setCustomerId(2);
        request.setTransactionUuid(uuid);
        request.setTransactionTypeId(1);
        request.setPermitNumber("P123");
        request.setLocationId(123);
        request.setRateId(121);
        request.setAmount(new BigDecimal(10));
        return request;
    }
    
    @Test
    public void testGetPermitTransactionScuccessWithPayments() throws Exception {
        final PermitTransaction permitTransaction = new PermitTransaction();
        final TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        permitTransaction.setVendorId(1);
        permitTransaction.setCustomerId(1);
        permitTransaction.setTransactionUuid(getBytesFromUuid(UUID.randomUUID()));
        permitTransaction.setLinkedTransactionUuid(getBytesFromUuid(UUID.randomUUID()));
        permitTransaction.setTransactionTypeId(transactionType);
        permitTransaction.setPermitNumber("ABCD");
        permitTransaction.setPlateNumber("CCCC");
        permitTransaction.setLocationId(1);
        permitTransaction.setSpaceId(1);
        permitTransaction.setActiveDate(Instant.now());
        permitTransaction.setActiveDate(Instant.now());
        permitTransaction.setExpiryDate(Instant.now());
        permitTransaction.setAmount(new BigDecimal(10));
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(permitTransaction);
        final RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(PERMIT_TRANSACTION_ENDPOINT + FORWARD_SLASH + UUID.randomUUID() + "?customerId=12")
                        .accept(MediaType.APPLICATION_JSON).content(myString).contentType(MediaType.APPLICATION_JSON);
        final ObjectResponse<List<ThirdPartyPayment>> body = new ObjectResponse<>();
        final List<ThirdPartyPayment> payments = new ArrayList<>();
        payments.add(this.populatePayment());
        body.setResponse(payments);
        final ResponseEntity<ObjectResponse<List<ThirdPartyPayment>>> paymentResponse =
                new ResponseEntity<ObjectResponse<List<ThirdPartyPayment>>>(body, HttpStatus.OK);
        Mockito.when(this.coreCpsServiceClient.getPermits(Mockito.anyString(), Mockito.anyString())).thenReturn(paymentResponse);
        Mockito.when(this.permitTransactionRepository.findByTransactionUuidAndCustomerId(Mockito.any(), Mockito.anyInt()))
                .thenReturn(permitTransaction);
        this.mockMvc.perform(requestBuilder).andExpect(status().isOk()).andDo(print());
    }

    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetPermitTransactionScuccessWithoutPayments() throws Exception {
        final PermitTransaction permitTransaction = new PermitTransaction();
        final TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        permitTransaction.setVendorId(1);
        permitTransaction.setCustomerId(1);
        permitTransaction.setTransactionUuid(getBytesFromUuid(UUID.randomUUID()));
        permitTransaction.setLinkedTransactionUuid(getBytesFromUuid(UUID.randomUUID()));
        permitTransaction.setTransactionTypeId(transactionType);
        permitTransaction.setPermitNumber("ABCD");
        permitTransaction.setPlateNumber("CCCC");
        permitTransaction.setLocationId(1);
        permitTransaction.setSpaceId(1);
        permitTransaction.setActiveDate(Instant.now());
        permitTransaction.setActiveDate(Instant.now());
        permitTransaction.setExpiryDate(Instant.now());
        permitTransaction.setAmount(new BigDecimal(10));
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(permitTransaction);
        final RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(PERMIT_TRANSACTION_ENDPOINT + FORWARD_SLASH + UUID.randomUUID() + "?customerId=12")
                        .accept(MediaType.APPLICATION_JSON).content(myString).contentType(MediaType.APPLICATION_JSON);
        Mockito.when(this.coreCpsServiceClient.getPermits(Mockito.anyString(), Mockito.anyString())).thenThrow(HystrixRuntimeException.class);
        Mockito.when(this.permitTransactionRepository.findByTransactionUuidAndCustomerId(Mockito.any(), Mockito.anyInt()))
                .thenReturn(permitTransaction);
        this.mockMvc.perform(requestBuilder).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void testGetPermitTransactionEntityNotFound() throws Exception {
        final PermitTransaction permitTransaction = new PermitTransaction();
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(permitTransaction);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERMIT_TRANSACTION_ENDPOINT + FORWARD_SLASH + UUID.randomUUID() + "?customerId=12").accept(MediaType.APPLICATION_JSON)
                .content(myString).contentType(MediaType.APPLICATION_JSON);
        Mockito.when(this.permitTransactionRepository.findByTransactionUuidAndCustomerId(Mockito.any(), Mockito.anyInt())).thenReturn(null);
        this.mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andDo(print());
    }
    
    public final byte[] getBytesFromUuid(final UUID uuid) {
        final ByteBuffer bb = ByteBuffer.wrap(new byte[Domains.BYTE_ARRAY_SIZE]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
    
    /**
     * @return
     */
    private ThirdPartyPayment populatePayment() {
        final ThirdPartyPayment payment = new ThirdPartyPayment();
        payment.setAmount(2.0);
        payment.setAuthorizationNumber("A121");
        payment.setCardExpiry("2020");
        payment.setCardType("VISA");
        payment.setCustomerId(1);
        payment.setDeviceId("D121");
        payment.setId("121");
        payment.setLast4Digits("1234");
        payment.setPaymentType("ONLINE");
        payment.setProcessorTransactionId("TR121");
        payment.setProductType("PAY");
        payment.setPurchaseUTC(Instant.now());
        payment.setTransactionUuid("uuid-tranaction");
        payment.setVendorId(121);
        return payment;
    }
    
    @Test
    public void test1fetchCustomerMappingInformationForSuccess() throws Exception {
        final List<TransactionType> pransactionTypeResponse = new ArrayList<>();
        final TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setLabel("CHARGE");
        transactionType.setDescription("CHARGE Test");
        pransactionTypeResponse.add(transactionType);

        final Page<TransactionType> pages = new PageImpl<>(pransactionTypeResponse);
        this.requestBuilder = MockMvcRequestBuilders
                .get(TestEndpoints.PERMIT_MAPPING + TestEndpoints.GET_TRANSACTION_TYPE_PERMIT_MAPPING)
                .accept(MediaType.APPLICATION_JSON);
        Mockito.when(this.transactionRepository.findAll(Mockito.any(Pageable.class))).thenReturn(pages);
        this.mockMvc.perform(this.requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void test2fetchCustomerMappingInformationForSuccess() throws Exception {
        final List<TransactionType> pransactionTypeResponse = new ArrayList<>();
        final Page<TransactionType> pages = new PageImpl<>(pransactionTypeResponse);
        this.requestBuilder = MockMvcRequestBuilders
                .get(TestEndpoints.PERMIT_MAPPING + TestEndpoints.GET_TRANSACTION_TYPE_PERMIT_MAPPING)
                .accept(MediaType.APPLICATION_JSON);
        Mockito.when(this.transactionRepository.findAll(Mockito.any(Pageable.class))).thenReturn(pages);
        this.mockMvc.perform(this.requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreatePermitTransactionUniqueCombinationFailure() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final PermitTransaction response = new PermitTransaction();
        response.setExpiryDate(Instant.now());
        response.setTransactionDate(Instant.now());
        response.setActiveDate(Instant.now());
        response.setPlateNumber("P123");
        response.setSpaceId(10);
        response.setCustomerId(1);
        response.setTransactionUuid(getBytesFromUuid(uuid));
        response.setAmount(new BigDecimal(10));
        response.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON).content(myString).contentType(MediaType.APPLICATION_JSON);
        Mockito.when(
                this.permitTransactionRepository.findByTransactionUuidAndCustomerId(Mockito.any(), Mockito.anyInt()))
                .thenReturn(response);
        this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void testCreatePermitTransactionIfUniqueCombinationIsNotPresentSuccess() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final PermitTransactionRequest request = this.populatePermitTransactionRequest(uuid);
        request.setLinkedTransactionUuid(UUID.randomUUID());
        request.setExpiryDate(new Date());
        request.setTransactionDate(new Date());
        request.setActiveDate(new Date());
        request.setPlateNumber("P123");
        request.setSpaceId(10);
        request.setAmount(new BigDecimal(10));
        final List<PaymentRequest> payments = new ArrayList<>();
        final PaymentRequest payment = this.populatePaymentRequest(uuid);
        payment.setAmount(10);
        payments.add(payment);
        request.setPayments(payments);
        request.setVendorId(1);
        final ObjectMapper mapper = new ObjectMapper();
        final String myString = mapper.writeValueAsString(request);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PERMIT_TRANSACTION_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON).content(myString).contentType(MediaType.APPLICATION_JSON);
        Mockito.when(
                this.permitTransactionRepository.findByTransactionUuidAndCustomerId(Mockito.any(), Mockito.anyInt()))
                .thenReturn(null);
        final TransactionType transactionType = new TransactionType();
        Mockito.when(this.transactionRepository.findOne(Mockito.anyInt())).thenReturn(transactionType);
        final ResponseEntity<ObjectResponse<RateDetailsResponse>> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.rateServiceClient.getRateDetails(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(response);
        final ObjectResponse<LocationValidationResponse> body = new ObjectResponse<>();
        final LocationValidationResponse objRes = new LocationValidationResponse();
        objRes.setIsValid(Boolean.TRUE);
        body.setResponse(objRes);
        final ResponseEntity<ObjectResponse<LocationValidationResponse>> topologyResponse = new ResponseEntity<>(body,
                HttpStatus.OK);
        Mockito.when(
                this.topologyServiceClient.validateCustomerRelationship(Mockito.any(LocationValidationRequest.class)))
                .thenReturn(topologyResponse);
        final PermitTransaction permitResponse = new PermitTransaction(1);
        Mockito.when(this.permitTransactionRepository.save(Mockito.any(PermitTransaction.class)))
                .thenReturn(permitResponse);
        final ResponseEntity<ObjectResponse<String>> coreCpsResponse = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(this.coreCpsServiceClient.record(Mockito.any(ThirdPartyPaymentsList.class)))
                .thenReturn(coreCpsResponse);
        this.mockMvc.perform(requestBuilder).andExpect(status().isOk())
                .andExpect(jsonPath(RESPONSE_STATUS).value(Domains.SUCCESS)).andDo(print());
    }
}
