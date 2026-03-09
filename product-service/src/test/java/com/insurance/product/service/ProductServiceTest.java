package com.insurance.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insurance.product.dto.ProductQueryDTO;
import com.insurance.product.entity.Product;
import com.insurance.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProductService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductService productService = new ProductService();

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 设置baseMapper
        ReflectionTestUtils.setField(productService, "baseMapper", productRepository);
        
        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setProductCode("TEST001");
        testProduct.setProductName("测试产品");
        testProduct.setProductType("LIFE");
        testProduct.setInsuranceType("TERM");
        testProduct.setStatus("DRAFT");
        testProduct.setMinCoverage(new BigDecimal("10000"));
        testProduct.setMaxCoverage(new BigDecimal("1000000"));
        testProduct.setCreatedTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("创建产品 - 成功")
    void testCreateProduct_Success() {
        // Given
        Product newProduct = new Product();
        newProduct.setProductCode("NEW001");
        newProduct.setProductName("新产品");

        when(productRepository.insert(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setProductId(2L);
            return 1;
        });

        // When
        boolean result = productService.createProduct(newProduct);

        // Then
        assertTrue(result);
        assertEquals("DRAFT", newProduct.getStatus());
        verify(productRepository, times(1)).insert(any(Product.class));
    }

    @Test
    @DisplayName("根据ID获取产品 - 存在")
    void testGetById_Exists() {
        // Given
        when(productRepository.selectById(1L)).thenReturn(testProduct);

        // When
        Product found = productService.getById(1L);

        // Then
        assertNotNull(found);
        assertEquals("TEST001", found.getProductCode());
        assertEquals("测试产品", found.getProductName());
    }

    @Test
    @DisplayName("根据ID获取产品 - 不存在")
    void testGetById_NotExists() {
        // Given
        when(productRepository.selectById(999L)).thenReturn(null);

        // When
        Product found = productService.getById(999L);

        // Then
        assertNull(found);
    }

    @Test
    @DisplayName("更新产品状态 - 成功")
    void testUpdateStatus_Success() {
        // Given
        when(productRepository.selectById(1L)).thenReturn(testProduct);
        when(productRepository.updateById(any(Product.class))).thenReturn(1);

        // When
        boolean result = productService.updateStatus(1L, "ACTIVE");

        // Then
        assertTrue(result);
        assertEquals("ACTIVE", testProduct.getStatus());
    }

    @Test
    @DisplayName("更新产品状态 - 产品不存在")
    void testUpdateStatus_ProductNotFound() {
        // Given
        when(productRepository.selectById(999L)).thenReturn(null);

        // When
        boolean result = productService.updateStatus(999L, "ACTIVE");

        // Then
        assertFalse(result);
        verify(productRepository, never()).updateById(any());
    }

    @Test
    @DisplayName("发布产品 - 成功")
    void testPublishProduct_Success() {
        // Given
        testProduct.setStatus("DRAFT");
        when(productRepository.selectById(1L)).thenReturn(testProduct);
        when(productRepository.updateById(any(Product.class))).thenReturn(1);

        // When
        boolean result = productService.publishProduct(1L);

        // Then
        assertTrue(result);
        assertEquals("ACTIVE", testProduct.getStatus());
    }

    @Test
    @DisplayName("停售产品 - 成功")
    void testStopProduct_Success() {
        // Given
        testProduct.setStatus("ACTIVE");
        when(productRepository.selectById(1L)).thenReturn(testProduct);
        when(productRepository.updateById(any(Product.class))).thenReturn(1);

        // When
        boolean result = productService.stopProduct(1L);

        // Then
        assertTrue(result);
        assertEquals("INACTIVE", testProduct.getStatus());
    }

    @Test
    @DisplayName("根据类型查询产品列表")
    void testListByType() {
        // Given
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductType("LIFE");
        product1.setStatus("ACTIVE");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductType("LIFE");
        product2.setStatus("ACTIVE");

        when(productRepository.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(product1, product2));

        // When
        List<Product> result = productService.listByType("LIFE");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
