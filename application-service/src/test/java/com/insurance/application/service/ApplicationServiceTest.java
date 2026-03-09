package com.insurance.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.application.client.ProductClient;
import com.insurance.application.client.UnderwritingClient;
import com.insurance.application.entity.Application;
import com.insurance.application.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ApplicationService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private UnderwritingClient underwritingClient;

    @InjectMocks
    private ApplicationService applicationService;

    private Application testApplication;

    @BeforeEach
    void setUp() {
        testApplication = new Application();
        testApplication.setApplicationId(1L);
        testApplication.setApplicationNo("APP20240301001");
        testApplication.setProductId(100L);
        testApplication.setApplicantId(1L);
        testApplication.setCoverage(new BigDecimal("100000"));
        testApplication.setPremium(new BigDecimal("1000"));
        testApplication.setStatus("DRAFT");
    }

    @Test
    @DisplayName("创建投保申请 - 成功")
    void testCreateApplication_Success() {
        // Given
        Application newApp = new Application();
        newApp.setProductId(100L);
        newApp.setCoverage(new BigDecimal("100000"));

        Map<String, Object> productResponse = new HashMap<>();
        productResponse.put("code", 200);
        Map<String, Object> productData = new HashMap<>();
        productData.put("productName", "测试产品");
        productData.put("productCode", "PROD100");
        productResponse.put("data", productData);

        when(productClient.getProductById(100L)).thenReturn(productResponse);
        when(applicationRepository.insert(any(Application.class))).thenAnswer(invocation -> {
            Application app = invocation.getArgument(0);
            app.setApplicationId(1L);
            return 1;
        });

        // When
        Application result = applicationService.createApplication(newApp);

        // Then
        assertNotNull(result);
        assertNotNull(result.getApplicationNo());
        assertEquals("DRAFT", result.getStatus());
        assertEquals("测试产品", result.getProductName());
        assertEquals(new BigDecimal("1000.00"), result.getPremium()); // 100000 * 0.01
    }

    @Test
    @DisplayName("创建投保申请 - 产品服务不可用")
    void testCreateApplication_ProductServiceUnavailable() {
        // Given
        Application newApp = new Application();
        newApp.setProductId(100L);
        newApp.setCoverage(new BigDecimal("100000"));

        when(productClient.getProductById(100L)).thenThrow(new RuntimeException("Service unavailable"));
        when(applicationRepository.insert(any(Application.class))).thenAnswer(invocation -> {
            Application app = invocation.getArgument(0);
            app.setApplicationId(1L);
            return 1;
        });

        // When
        Application result = applicationService.createApplication(newApp);

        // Then
        assertNotNull(result);
        assertEquals("产品100", result.getProductName()); // 默认值
    }

    @Test
    @DisplayName("根据ID获取投保申请 - 存在")
    void testGetApplicationById_Exists() {
        // Given
        when(applicationRepository.selectById(1L)).thenReturn(testApplication);

        // When
        Application result = applicationService.getApplicationById(1L);

        // Then
        assertNotNull(result);
        assertEquals("APP20240301001", result.getApplicationNo());
    }

    @Test
    @DisplayName("根据ID获取投保申请 - 不存在")
    void testGetApplicationById_NotExists() {
        // Given
        when(applicationRepository.selectById(999L)).thenReturn(null);

        // When
        Application result = applicationService.getApplicationById(999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("提交投保申请")
    void testSubmitApplication() {
        // Given
        when(applicationRepository.selectById(1L)).thenReturn(testApplication);
        when(applicationRepository.updateById(any(Application.class))).thenReturn(1);

        // When
        Application result = applicationService.submitApplication(1L);

        // Then
        assertNotNull(result);
        assertEquals("SUBMITTED", result.getStatus());
        verify(applicationRepository, times(1)).updateById(any(Application.class));
    }

    @Test
    @DisplayName("更新投保申请状态")
    void testUpdateApplicationStatus() {
        // Given
        when(applicationRepository.selectById(1L)).thenReturn(testApplication);
        when(applicationRepository.updateById(any(Application.class))).thenReturn(1);

        // When
        Application result = applicationService.updateApplicationStatus(1L, "APPROVED");

        // Then
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
    }

    @Test
    @DisplayName("删除投保申请 - 草稿状态可删除")
    void testDeleteApplication_DraftStatus() {
        // Given
        testApplication.setStatus("DRAFT");
        when(applicationRepository.selectById(1L)).thenReturn(testApplication);
        when(applicationRepository.deleteById(1L)).thenReturn(1);

        // When
        boolean result = applicationService.deleteApplication(1L);

        // Then
        assertTrue(result);
        verify(applicationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("删除投保申请 - 非草稿状态不可删除")
    void testDeleteApplication_NotDraftStatus() {
        // Given
        testApplication.setStatus("SUBMITTED");
        when(applicationRepository.selectById(1L)).thenReturn(testApplication);

        // When
        boolean result = applicationService.deleteApplication(1L);

        // Then
        assertFalse(result);
        verify(applicationRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("删除投保申请 - 不存在")
    void testDeleteApplication_NotExists() {
        // Given
        when(applicationRepository.selectById(999L)).thenReturn(null);

        // When
        boolean result = applicationService.deleteApplication(999L);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("更新保单ID")
    void testUpdatePolicyId() {
        // Given
        when(applicationRepository.selectById(1L)).thenReturn(testApplication);
        when(applicationRepository.updateById(any(Application.class))).thenReturn(1);

        // When
        applicationService.updatePolicyId(1L, 200L);

        // Then
        assertEquals(200L, testApplication.getPolicyId());
        assertEquals("POLICY_ISSUED", testApplication.getStatus());
    }

    @Test
    @DisplayName("根据投保人ID查询申请列表")
    void testGetApplicationsByApplicant() {
        // Given
        Application app1 = new Application();
        app1.setApplicationId(1L);
        app1.setApplicantId(1L);

        Application app2 = new Application();
        app2.setApplicationId(2L);
        app2.setApplicantId(1L);

        when(applicationRepository.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(app1, app2));

        // When
        List<Application> result = applicationService.getApplicationsByApplicant(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
