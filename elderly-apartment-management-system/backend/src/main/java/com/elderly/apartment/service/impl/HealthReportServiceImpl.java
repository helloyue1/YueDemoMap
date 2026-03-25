package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.HealthReport;
import com.elderly.apartment.entity.HealthReportDetail;
import com.elderly.apartment.entity.Room;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.mapper.HealthReportDetailMapper;
import com.elderly.apartment.mapper.HealthReportMapper;
import com.elderly.apartment.service.HealthReportService;
import com.elderly.apartment.service.RoomService;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 体检报告服务实现类
 */
@Service
public class HealthReportServiceImpl extends ServiceImpl<HealthReportMapper, HealthReport> implements HealthReportService {

    @Autowired
    private HealthReportMapper healthReportMapper;

    @Autowired
    private HealthReportDetailMapper healthReportDetailMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Override
    public Page<HealthReport> getReportPage(Integer page, Integer size, String elderlyName,
                                             Integer reportType, String startDate, String endDate) {
        Page<HealthReport> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HealthReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthReport::getDeleted, 0);

        if (reportType != null) {
            wrapper.eq(HealthReport::getReportType, reportType);
        }

        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(HealthReport::getReportDate, LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(HealthReport::getReportDate, LocalDate.parse(endDate));
        }

        wrapper.orderByDesc(HealthReport::getCreateTime);
        Page<HealthReport> result = this.page(pageParam, wrapper);

        // 填充老人信息
        List<HealthReport> records = result.getRecords();
        for (HealthReport report : records) {
            fillElderlyInfo(report);
        }

        return result;
    }

    @Override
    @Transactional
    public HealthReport generateReport(Integer elderlyId, Integer reportType, String reportDate) {
        // 验证住客是否存在
        User user = userService.getById(elderlyId);
        if (user == null) {
            throw new RuntimeException("住客信息不存在");
        }

        // 计算统计日期范围
        LocalDate reportLocalDate = LocalDate.parse(reportDate);
        LocalDate startDate;
        LocalDate endDate;

        switch (reportType) {
            case 1: // 月度报告
                startDate = reportLocalDate.withDayOfMonth(1);
                endDate = reportLocalDate.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case 2: // 季度报告
                int quarter = (reportLocalDate.getMonthValue() - 1) / 3;
                startDate = LocalDate.of(reportLocalDate.getYear(), quarter * 3 + 1, 1);
                endDate = startDate.plusMonths(3).minusDays(1);
                break;
            case 3: // 年度报告
                startDate = LocalDate.of(reportLocalDate.getYear(), 1, 1);
                endDate = LocalDate.of(reportLocalDate.getYear(), 12, 31);
                break;
            default:
                throw new RuntimeException("无效的报告类型");
        }

        // 查询该时间段内的健康档案
        List<com.elderly.apartment.entity.HealthRecord> records = healthReportMapper.selectHealthRecordsForReport(
                elderlyId, startDate.toString(), endDate.toString());

        if (records.isEmpty()) {
            throw new RuntimeException("该时间段内没有健康档案数据，请先添加健康档案记录后再生成报告");
        }

        // 创建报告
        HealthReport report = new HealthReport();
        report.setElderlyId(elderlyId);
        report.setReportType(reportType);
        report.setReportDate(reportLocalDate);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setStatus(0);

        // 计算各项指标平均值
        report.setHealthScore(calculateHealthScore(records));
        report.setOverallAssessment(generateOverallAssessment(records));
        report.setRecommendations(generateRecommendations(records));

        // 保存报告
        this.save(report);

        // 生成报告明细
        List<HealthReportDetail> details = generateReportDetails(report.getId(), records);
        for (HealthReportDetail detail : details) {
            healthReportDetailMapper.insert(detail);
        }

        return report;
    }

    @Override
    public HealthReport getReportDetail(Integer id) {
        HealthReport report = this.getById(id);
        if (report == null || report.getDeleted() == 1) {
            return null;
        }

        fillElderlyInfo(report);

        // 加载明细
        List<HealthReportDetail> details = healthReportDetailMapper.selectByReportId(id);
        report.setDetails(details);

        return report;
    }

    @Override
    @Transactional
    public boolean auditReport(Integer id, Integer doctorId, String doctorName, String auditOpinion) {
        HealthReport report = this.getById(id);
        if (report == null || report.getDeleted() == 1) {
            return false;
        }

        report.setStatus(1);
        report.setDoctorId(doctorId);
        report.setDoctorName(doctorName);
        report.setAuditOpinion(auditOpinion);
        report.setAuditTime(LocalDateTime.now());

        return this.updateById(report);
    }

    @Override
    public Map<String, Object> getHealthStatistics(Integer elderlyId) {
        Map<String, Object> stats = new HashMap<>();

        // 查询最近6个月的健康档案
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);

        List<com.elderly.apartment.entity.HealthRecord> records = healthReportMapper.selectHealthRecordsForReport(
                elderlyId, startDate.toString(), endDate.toString());

        if (records.isEmpty()) {
            stats.put("recordCount", 0);
            return stats;
        }

        // 计算平均值
        double avgSystolic = records.stream().mapToInt(r -> r.getSystolicPressure() != null ? r.getSystolicPressure() : 0).average().orElse(0);
        double avgDiastolic = records.stream().mapToInt(r -> r.getDiastolicPressure() != null ? r.getDiastolicPressure() : 0).average().orElse(0);
        double avgBloodSugar = records.stream().mapToDouble(r -> r.getBloodSugar() != null ? r.getBloodSugar() : 0).average().orElse(0);
        double avgHeartRate = records.stream().mapToInt(r -> r.getHeartRate() != null ? r.getHeartRate() : 0).average().orElse(0);

        stats.put("recordCount", records.size());
        stats.put("avgSystolic", BigDecimal.valueOf(avgSystolic).setScale(1, RoundingMode.HALF_UP));
        stats.put("avgDiastolic", BigDecimal.valueOf(avgDiastolic).setScale(1, RoundingMode.HALF_UP));
        stats.put("avgBloodSugar", BigDecimal.valueOf(avgBloodSugar).setScale(2, RoundingMode.HALF_UP));
        stats.put("avgHeartRate", BigDecimal.valueOf(avgHeartRate).setScale(1, RoundingMode.HALF_UP));

        // 健康状态分布
        long goodCount = records.stream().filter(r -> r.getHealthStatus() != null && r.getHealthStatus() == 1).count();
        long normalCount = records.stream().filter(r -> r.getHealthStatus() != null && r.getHealthStatus() == 2).count();
        long poorCount = records.stream().filter(r -> r.getHealthStatus() != null && r.getHealthStatus() == 3).count();
        long criticalCount = records.stream().filter(r -> r.getHealthStatus() != null && r.getHealthStatus() == 4).count();

        stats.put("goodCount", goodCount);
        stats.put("normalCount", normalCount);
        stats.put("poorCount", poorCount);
        stats.put("criticalCount", criticalCount);

        return stats;
    }

    private void fillElderlyInfo(HealthReport report) {
        if (report.getElderlyId() != null) {
            User user = userService.getById(report.getElderlyId());
            if (user != null) {
                report.setElderlyName(user.getRealName());
                // 直接从 user 对象获取房间号
                report.setRoomNumber(user.getRoomNumber());
            }
        }
    }

    private Integer calculateHealthScore(List<com.elderly.apartment.entity.HealthRecord> records) {
        if (records.isEmpty()) return 0;

        // 取最新的记录计算评分
        var latest = records.get(0);
        int score = 100;

        // 根据各项指标扣分
        if (latest.getSystolicPressure() != null) {
            if (latest.getSystolicPressure() > 140) score -= 15;
            else if (latest.getSystolicPressure() > 130) score -= 10;
        }

        if (latest.getDiastolicPressure() != null) {
            if (latest.getDiastolicPressure() > 90) score -= 15;
            else if (latest.getDiastolicPressure() > 85) score -= 10;
        }

        if (latest.getBloodSugar() != null) {
            if (latest.getBloodSugar() > 7.0) score -= 15;
            else if (latest.getBloodSugar() > 6.1) score -= 10;
        }

        if (latest.getHealthStatus() != null) {
            switch (latest.getHealthStatus()) {
                case 2: score -= 10; break;
                case 3: score -= 20; break;
                case 4: score -= 30; break;
            }
        }

        return Math.max(score, 0);
    }

    private String generateOverallAssessment(List<com.elderly.apartment.entity.HealthRecord> records) {
        var latest = records.get(0);
        StringBuilder assessment = new StringBuilder();

        assessment.append("本次体检共记录").append(records.size()).append("次健康数据。");

        if (latest.getHealthStatus() != null) {
            switch (latest.getHealthStatus()) {
                case 1:
                    assessment.append("老人整体健康状况良好，各项指标基本正常。");
                    break;
                case 2:
                    assessment.append("老人健康状况一般，部分指标需要关注。");
                    break;
                case 3:
                    assessment.append("老人健康状况较差，需要加强监护和调理。");
                    break;
                case 4:
                    assessment.append("老人健康状况危急，需要立即就医治疗。");
                    break;
            }
        }

        return assessment.toString();
    }

    private String generateRecommendations(List<com.elderly.apartment.entity.HealthRecord> records) {
        StringBuilder recommendations = new StringBuilder();
        var latest = records.get(0);

        recommendations.append("1. 定期监测血压、血糖等关键指标；\n");
        recommendations.append("2. 保持规律作息，适量运动；\n");
        recommendations.append("3. 注意饮食均衡，低盐低脂；\n");

        if (latest.getHealthStatus() != null && latest.getHealthStatus() > 1) {
            recommendations.append("4. 建议定期复诊，遵医嘱用药；\n");
            recommendations.append("5. 如有不适，及时就医。");
        }

        return recommendations.toString();
    }

    private List<HealthReportDetail> generateReportDetails(Integer reportId,
            List<com.elderly.apartment.entity.HealthRecord> records) {
        List<HealthReportDetail> details = new ArrayList<>();

        if (records.isEmpty()) return details;

        var latest = records.get(0);

        // 血压
        if (latest.getSystolicPressure() != null && latest.getDiastolicPressure() != null) {
            HealthReportDetail detail = new HealthReportDetail();
            detail.setReportId(reportId);
            detail.setCheckItem("血压");
            detail.setCheckValue(latest.getSystolicPressure() + "/" + latest.getDiastolicPressure());
            detail.setUnit("mmHg");
            detail.setReferenceRange("90-140/60-90");
            detail.setResult((latest.getSystolicPressure() > 140 || latest.getDiastolicPressure() > 90) ? 2 : 1);
            detail.setTrend(3);
            details.add(detail);
        }

        // 血糖
        if (latest.getBloodSugar() != null) {
            HealthReportDetail detail = new HealthReportDetail();
            detail.setReportId(reportId);
            detail.setCheckItem("血糖");
            detail.setCheckValue(String.valueOf(latest.getBloodSugar()));
            detail.setUnit("mmol/L");
            detail.setReferenceRange("3.9-6.1");
            detail.setResult(latest.getBloodSugar() > 6.1 ? 2 : 1);
            detail.setTrend(3);
            details.add(detail);
        }

        // 心率
        if (latest.getHeartRate() != null) {
            HealthReportDetail detail = new HealthReportDetail();
            detail.setReportId(reportId);
            detail.setCheckItem("心率");
            detail.setCheckValue(String.valueOf(latest.getHeartRate()));
            detail.setUnit("次/分");
            detail.setReferenceRange("60-100");
            detail.setResult((latest.getHeartRate() > 100 || latest.getHeartRate() < 60) ? 2 : 1);
            detail.setTrend(3);
            details.add(detail);
        }

        // 体温
        if (latest.getTemperature() != null) {
            HealthReportDetail detail = new HealthReportDetail();
            detail.setReportId(reportId);
            detail.setCheckItem("体温");
            detail.setCheckValue(String.valueOf(latest.getTemperature()));
            detail.setUnit("℃");
            detail.setReferenceRange("36.0-37.2");
            detail.setResult((latest.getTemperature() > 37.2 || latest.getTemperature() < 36.0) ? 2 : 1);
            detail.setTrend(3);
            details.add(detail);
        }

        // 血氧
        if (latest.getBloodOxygen() != null) {
            HealthReportDetail detail = new HealthReportDetail();
            detail.setReportId(reportId);
            detail.setCheckItem("血氧饱和度");
            detail.setCheckValue(String.valueOf(latest.getBloodOxygen()));
            detail.setUnit("%");
            detail.setReferenceRange("95-100");
            detail.setResult(latest.getBloodOxygen() < 95 ? 2 : 1);
            detail.setTrend(3);
            details.add(detail);
        }

        // 体重
        if (latest.getWeight() != null) {
            HealthReportDetail detail = new HealthReportDetail();
            detail.setReportId(reportId);
            detail.setCheckItem("体重");
            detail.setCheckValue(String.valueOf(latest.getWeight()));
            detail.setUnit("kg");
            detail.setReferenceRange("-");
            detail.setResult(1);
            detail.setTrend(3);
            details.add(detail);
        }

        return details;
    }
}
