package com.elderly.apartment.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.HealthReport;
import com.elderly.apartment.entity.HealthReportDetail;
import com.elderly.apartment.service.HealthReportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 体检报告控制器
 */
@RestController
@RequestMapping("/health-report")
public class HealthReportController {

    @Autowired
    private HealthReportService healthReportService;

    /**
     * 获取体检报告列表
     */
    @GetMapping
    public Result<Page<HealthReport>> getReportList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) Integer reportType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Page<HealthReport> result = healthReportService.getReportPage(page, size, elderlyName, reportType, startDate, endDate);
        return Result.success(result);
    }

    /**
     * 生成体检报告
     */
    @PostMapping("/generate")
    public Result<HealthReport> generateReport(
            @RequestParam Integer elderlyId,
            @RequestParam Integer reportType,
            @RequestParam String reportDate) {

        try {
            HealthReport report = healthReportService.generateReport(elderlyId, reportType, reportDate);
            return Result.success(report);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取报告详情
     */
    @GetMapping("/{id}")
    public Result<HealthReport> getReportDetail(@PathVariable Integer id) {
        HealthReport report = healthReportService.getReportDetail(id);
        if (report == null) {
            return Result.error("报告不存在");
        }
        return Result.success(report);
    }

    /**
     * 审核报告
     */
    @PutMapping("/{id}/audit")
    public Result<Void> auditReport(
            @PathVariable Integer id,
            @RequestParam Integer doctorId,
            @RequestParam String doctorName,
            @RequestParam String auditOpinion) {

        boolean success = healthReportService.auditReport(id, doctorId, doctorName, auditOpinion);
        if (success) {
            return Result.success(null);
        }
        return Result.error("审核失败");
    }

    /**
     * 删除报告
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReport(@PathVariable Integer id) {
        boolean success = healthReportService.removeById(id);
        if (success) {
            return Result.success(null);
        }
        return Result.error("删除失败");
    }

    /**
     * 获取老人健康统计数据
     */
    @GetMapping("/statistics/{elderlyId}")
    public Result<Map<String, Object>> getHealthStatistics(@PathVariable Integer elderlyId) {
        Map<String, Object> stats = healthReportService.getHealthStatistics(elderlyId);
        return Result.success(stats);
    }

    /**
     * 导出体检报告为PDF
     */
    @GetMapping("/export/{id}")
    public void exportReport(@PathVariable Integer id, HttpServletResponse response) throws IOException, DocumentException {
        HealthReport report = healthReportService.getReportDetail(id);
        if (report == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"message\":\"报告不存在\"}");
            return;
        }

        // 设置响应头
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(
            report.getElderlyName() + "_" + getReportTypeText(report.getReportType()) + "_" + report.getReportDate(),
            "UTF-8"
        ).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".pdf");

        // 创建PDF文档
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // 设置中文字体
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD, new BaseColor(0, 102, 204));
        Font sectionFont = new Font(baseFont, 14, Font.BOLD, new BaseColor(0, 102, 204));
        Font labelFont = new Font(baseFont, 11, Font.BOLD);
        Font contentFont = new Font(baseFont, 11, Font.NORMAL);
        Font smallFont = new Font(baseFont, 10, Font.NORMAL);

        // 标题
        Paragraph title = new Paragraph("体检报告", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // 报告基本信息表格
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10);
        infoTable.setSpacingAfter(15);
        infoTable.setWidths(new float[]{1, 2});

        // 添加基本信息行
        addTableRow(infoTable, "老人姓名", report.getElderlyName(), labelFont, contentFont);
        addTableRow(infoTable, "房间号", report.getRoomNumber(), labelFont, contentFont);
        addTableRow(infoTable, "报告类型", getReportTypeText(report.getReportType()), labelFont, contentFont);
        addTableRow(infoTable, "报告日期", report.getReportDate().toString(), labelFont, contentFont);
        addTableRow(infoTable, "统计周期", report.getStartDate() + " 至 " + report.getEndDate(), labelFont, contentFont);
        addTableRow(infoTable, "健康评分", report.getHealthScore() + " 分", labelFont, contentFont);
        addTableRow(infoTable, "审核状态", report.getStatus() == 1 ? "已审核" : "待审核", labelFont, contentFont);

        document.add(infoTable);

        // 检查指标部分
        Paragraph checkTitle = new Paragraph("检查指标", sectionFont);
        checkTitle.setSpacingBefore(15);
        checkTitle.setSpacingAfter(10);
        document.add(checkTitle);

        if (report.getDetails() != null && !report.getDetails().isEmpty()) {
            PdfPTable checkTable = new PdfPTable(4);
            checkTable.setWidthPercentage(100);
            checkTable.setSpacingBefore(5);
            checkTable.setSpacingAfter(15);
            checkTable.setWidths(new float[]{2, 1.5f, 2, 1});

            // 表头
            addCheckTableHeader(checkTable, baseFont);

            // 数据行
            for (HealthReportDetail detail : report.getDetails()) {
                addCheckTableRow(checkTable, detail, baseFont);
            }

            document.add(checkTable);
        }

        // 总体评估
        Paragraph assessmentTitle = new Paragraph("总体评估", sectionFont);
        assessmentTitle.setSpacingBefore(15);
        assessmentTitle.setSpacingAfter(10);
        document.add(assessmentTitle);

        Paragraph assessmentContent = new Paragraph(
            report.getOverallAssessment() != null ? report.getOverallAssessment() : "暂无评估",
            contentFont
        );
        assessmentContent.setSpacingAfter(15);
        document.add(assessmentContent);

        // 健康建议
        Paragraph recommendTitle = new Paragraph("健康建议", sectionFont);
        recommendTitle.setSpacingBefore(15);
        recommendTitle.setSpacingAfter(10);
        document.add(recommendTitle);

        Paragraph recommendContent = new Paragraph(
            report.getRecommendations() != null ? report.getRecommendations() : "暂无建议",
            contentFont
        );
        recommendContent.setSpacingAfter(15);
        document.add(recommendContent);

        // 审核信息
        if (report.getStatus() == 1) {
            Paragraph auditTitle = new Paragraph("审核信息", sectionFont);
            auditTitle.setSpacingBefore(15);
            auditTitle.setSpacingAfter(10);
            document.add(auditTitle);

            PdfPTable auditTable = new PdfPTable(2);
            auditTable.setWidthPercentage(100);
            auditTable.setSpacingBefore(5);
            auditTable.setSpacingAfter(15);
            auditTable.setWidths(new float[]{1, 2});

            addTableRow(auditTable, "审核医生", report.getDoctorName() != null ? report.getDoctorName() : "", labelFont, contentFont);
            addTableRow(auditTable, "审核时间", report.getAuditTime() != null ? report.getAuditTime().toString() : "", labelFont, contentFont);
            addTableRow(auditTable, "审核意见", report.getAuditOpinion() != null ? report.getAuditOpinion() : "", labelFont, contentFont);

            document.add(auditTable);
        }

        // 页脚
        Paragraph footer = new Paragraph("老年公寓管理系统 - 体检报告", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font contentFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new BaseColor(240, 248, 255));
        labelCell.setPadding(8);
        labelCell.setBorderColor(new BaseColor(200, 200, 200));
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "", contentFont));
        valueCell.setPadding(8);
        valueCell.setBorderColor(new BaseColor(200, 200, 200));
        table.addCell(valueCell);
    }

    private void addCheckTableHeader(PdfPTable table, BaseFont baseFont) {
        Font headerFont = new Font(baseFont, 11, Font.BOLD, BaseColor.WHITE);
        BaseColor headerColor = new BaseColor(0, 102, 204);

        String[] headers = {"检查项目", "检查值", "参考范围", "结果"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(new BaseColor(200, 200, 200));
            table.addCell(cell);
        }
    }

    private void addCheckTableRow(PdfPTable table, HealthReportDetail detail, BaseFont baseFont) {
        Font normalFont = new Font(baseFont, 10, Font.NORMAL);
        Font resultFont = new Font(baseFont, 10, Font.BOLD,
            detail.getResult() == 1 ? new BaseColor(0, 153, 0) : new BaseColor(204, 0, 0));

        PdfPCell cell1 = new PdfPCell(new Phrase(detail.getCheckItem(), normalFont));
        cell1.setPadding(6);
        cell1.setBorderColor(new BaseColor(200, 200, 200));
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(detail.getCheckValue() + " " + detail.getUnit(), normalFont));
        cell2.setPadding(6);
        cell2.setBorderColor(new BaseColor(200, 200, 200));
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase(detail.getReferenceRange(), normalFont));
        cell3.setPadding(6);
        cell3.setBorderColor(new BaseColor(200, 200, 200));
        table.addCell(cell3);

        String resultText = detail.getResult() == 1 ? "正常" : "异常";
        PdfPCell cell4 = new PdfPCell(new Phrase(resultText, resultFont));
        cell4.setPadding(6);
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell4.setBorderColor(new BaseColor(200, 200, 200));
        table.addCell(cell4);
    }

    private String getReportTypeText(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "月度报告";
            case 2: return "季度报告";
            case 3: return "年度报告";
            default: return "未知";
        }
    }
}
