package com.insurance.policy.service;

import com.insurance.policy.entity.Policy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    public byte[] generatePolicyPdf(Policy policy) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            Paragraph title = new Paragraph("保险保单")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            document.add(new Paragraph("\n"));
            
            Paragraph header = new Paragraph("保单信息")
                .setFontSize(14)
                .setBold();
            document.add(header);
            
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            
            addTableRow(table, "保单号", policy.getPolicyNo());
            addTableRow(table, "产品名称", policy.getProductName());
            addTableRow(table, "产品代码", policy.getProductCode());
            addTableRow(table, "保费", String.format("%.2f元", policy.getPremium()));
            addTableRow(table, "保额", String.format("%.2f元", policy.getCoverage()));
            addTableRow(table, "生效日期", policy.getEffectiveDate() != null ? policy.getEffectiveDate().toString() : "");
            addTableRow(table, "到期日期", policy.getExpirationDate() != null ? policy.getExpirationDate().toString() : "");
            addTableRow(table, "状态", getStatusText(policy.getStatus()));
            
            document.add(table);
            
            document.add(new Paragraph("\n\n"));
            
            Paragraph footer = new Paragraph("本保单由保险公司出具，具体条款以保险合同为准。")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);
            
            Paragraph datePara = new Paragraph("生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(datePara);
            
        } catch (Exception e) {
            throw new RuntimeException("生成PDF失败", e);
        }
        
        return outputStream.toByteArray();
    }
    
    private void addTableRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "")));
    }
    
    private String getStatusText(String status) {
        if (status == null) return "未知";
        return switch (status) {
            case "EFFECTIVE" -> "生效中";
            case "EXPIRED" -> "已过期";
            case "SURRENDERED" -> "已退保";
            case "TERMINATED" -> "已终止";
            case "LAPSED" -> "已失效";
            default -> status;
        };
    }
}
