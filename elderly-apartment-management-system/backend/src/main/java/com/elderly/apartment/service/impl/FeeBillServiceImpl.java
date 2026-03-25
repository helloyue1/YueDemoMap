package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.*;
import com.elderly.apartment.mapper.FeeBillMapper;
import com.elderly.apartment.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class FeeBillServiceImpl extends ServiceImpl<FeeBillMapper, FeeBill> implements FeeBillService {

    @Autowired
    private FeeBillMapper feeBillMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private FeeStandardService feeStandardService;

    @Autowired
    private FeeDetailService feeDetailService;

    @Autowired
    private FeePaymentService feePaymentService;

    @Autowired
    private FeeInvoiceService feeInvoiceService;

    @Autowired
    private FeeAccountService feeAccountService;

    @Override
    public Page<FeeBill> getBillList(Integer page, Integer size, String elderlyName, String billMonth, Integer status) {
        Page<FeeBill> pageParam = new Page<>(page, size);
        return feeBillMapper.selectBillList(pageParam, elderlyName, billMonth, status);
    }

    @Override
    public FeeBill getBillDetail(Long id) {
        return feeBillMapper.selectBillDetail(id);
    }

    @Override
    @Transactional
    public void saveBill(FeeBill bill) {
        // 生成账单号
        bill.setBillNo(generateBillNo());
        
        // 设置默认值
        if (bill.getRoomFee() == null) bill.setRoomFee(BigDecimal.ZERO);
        if (bill.getCareFee() == null) bill.setCareFee(BigDecimal.ZERO);
        if (bill.getMealFee() == null) bill.setMealFee(BigDecimal.ZERO);
        if (bill.getMedicalFee() == null) bill.setMedicalFee(BigDecimal.ZERO);
        if (bill.getOtherFee() == null) bill.setOtherFee(BigDecimal.ZERO);
        if (bill.getDiscountAmount() == null) bill.setDiscountAmount(BigDecimal.ZERO);
        if (bill.getPaidAmount() == null) bill.setPaidAmount(BigDecimal.ZERO);
        
        // 计算总金额
        BigDecimal total = bill.getRoomFee().add(bill.getCareFee())
                .add(bill.getMealFee()).add(bill.getMedicalFee()).add(bill.getOtherFee());
        bill.setTotalAmount(total);
        
        // 计算应付金额
        BigDecimal payable = total.subtract(bill.getDiscountAmount());
        bill.setPayableAmount(payable.compareTo(BigDecimal.ZERO) > 0 ? payable : BigDecimal.ZERO);
        
        // 设置状态为待缴费
        bill.setStatus(0);
        
        // 设置创建时间
        bill.setCreateTime(LocalDateTime.now());
        bill.setUpdateTime(LocalDateTime.now());
        
        // 保存账单
        save(bill);
        
        // 生成费用明细
        User user = userService.getById(bill.getElderlyId());
        if (user != null) {
            generateFeeDetails(bill, user);
            
            // 确保账户存在
            feeAccountService.createAccount(user.getId());
        }
    }

    @Override
    @Transactional
    public void generateMonthlyBills(String billMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate monthDate = LocalDate.parse(billMonth + "-01", formatter);
        LocalDate startDate = monthDate.withDayOfMonth(1);
        LocalDate endDate = monthDate.with(TemporalAdjusters.lastDayOfMonth());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.eq(User::getStatus, 1);
        List<User> userList = userService.list(wrapper);

        for (User user : userList) {
            LambdaQueryWrapper<FeeBill> billWrapper = new LambdaQueryWrapper<>();
            billWrapper.eq(FeeBill::getElderlyId, user.getId());
            billWrapper.eq(FeeBill::getBillMonth, billMonth);
            if (count(billWrapper) > 0) {
                continue;
            }

            FeeBill bill = new FeeBill();
            bill.setBillNo(generateBillNo());
            bill.setElderlyId(user.getId());
            bill.setBillMonth(billMonth);
            bill.setBillStartDate(startDate);
            bill.setBillEndDate(endDate);

            BigDecimal roomFee = calculateRoomFee(user);
            BigDecimal careFee = calculateCareFee(user);
            BigDecimal mealFee = calculateMealFee(user);

            bill.setRoomFee(roomFee);
            bill.setCareFee(careFee);
            bill.setMealFee(mealFee);
            bill.setMedicalFee(BigDecimal.ZERO);
            bill.setOtherFee(BigDecimal.ZERO);

            BigDecimal total = roomFee.add(careFee).add(mealFee);
            bill.setTotalAmount(total);
            bill.setDiscountAmount(BigDecimal.ZERO);
            bill.setPayableAmount(total);
            bill.setPaidAmount(BigDecimal.ZERO);
            bill.setStatus(0);
            bill.setDueDate(endDate.plusDays(10));
            bill.setCreateTime(LocalDateTime.now());
            bill.setUpdateTime(LocalDateTime.now());

            save(bill);

            generateFeeDetails(bill, user);

            feeAccountService.createAccount(user.getId());
        }
    }

    private String generateBillNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return "BILL" + dateStr + randomStr;
    }

    private BigDecimal calculateRoomFee(User user) {
        if (user.getRoomId() == null) {
            return BigDecimal.ZERO;
        }
        Room room = roomService.getById(user.getRoomId());
        if (room == null || room.getType() == null) {
            return new BigDecimal("2500.00");
        }

        FeeStandard standard = feeStandardService.getStandardByTypeAndRoom(1L, room.getType());
        if (standard != null) {
            return standard.getAmount();
        }

        switch (room.getType()) {
            case 1:
                return new BigDecimal("3000.00");
            case 2:
                return new BigDecimal("2000.00");
            case 3:
                return new BigDecimal("5000.00");
            default:
                return new BigDecimal("2500.00");
        }
    }

    private BigDecimal calculateCareFee(User user) {
        return new BigDecimal("1500.00");
    }

    private BigDecimal calculateMealFee(User user) {
        return new BigDecimal("800.00");
    }

    private void generateFeeDetails(FeeBill bill, User user) {
        if (bill.getRoomFee().compareTo(BigDecimal.ZERO) > 0) {
            FeeDetail detail = new FeeDetail();
            detail.setBillId(bill.getId());
            detail.setFeeTypeId(1L);
            detail.setFeeName("住宿费");
            detail.setAmount(bill.getRoomFee());
            detail.setUnitPrice(bill.getRoomFee());
            detail.setQuantity(BigDecimal.ONE);
            detail.setFeeDate(bill.getBillStartDate());
            detail.setDescription("月度住宿费用");
            detail.setCreateTime(LocalDateTime.now());
            feeDetailService.save(detail);
        }

        if (bill.getCareFee().compareTo(BigDecimal.ZERO) > 0) {
            FeeDetail detail = new FeeDetail();
            detail.setBillId(bill.getId());
            detail.setFeeTypeId(2L);
            detail.setFeeName("护理费");
            detail.setAmount(bill.getCareFee());
            detail.setUnitPrice(bill.getCareFee());
            detail.setQuantity(BigDecimal.ONE);
            detail.setFeeDate(bill.getBillStartDate());
            detail.setDescription("月度护理费用");
            detail.setCreateTime(LocalDateTime.now());
            feeDetailService.save(detail);
        }

        if (bill.getMealFee().compareTo(BigDecimal.ZERO) > 0) {
            FeeDetail detail = new FeeDetail();
            detail.setBillId(bill.getId());
            detail.setFeeTypeId(3L);
            detail.setFeeName("餐饮费");
            detail.setAmount(bill.getMealFee());
            detail.setUnitPrice(bill.getMealFee());
            detail.setQuantity(BigDecimal.ONE);
            detail.setFeeDate(bill.getBillStartDate());
            detail.setDescription("月度餐饮费用");
            detail.setCreateTime(LocalDateTime.now());
            feeDetailService.save(detail);
        }
    }

    @Override
    @Transactional
    public boolean removeById(java.io.Serializable id) {
        // 先查询该账单的所有缴费记录
        LambdaQueryWrapper<FeePayment> paymentQueryWrapper = new LambdaQueryWrapper<>();
        paymentQueryWrapper.eq(FeePayment::getBillId, id);
        List<FeePayment> payments = feePaymentService.list(paymentQueryWrapper);
        
        // 删除关联的发票记录
        for (FeePayment payment : payments) {
            LambdaQueryWrapper<FeeInvoice> invoiceWrapper = new LambdaQueryWrapper<>();
            invoiceWrapper.eq(FeeInvoice::getPaymentId, payment.getId());
            feeInvoiceService.remove(invoiceWrapper);
        }
        
        // 删除关联的缴费记录
        LambdaQueryWrapper<FeePayment> paymentWrapper = new LambdaQueryWrapper<>();
        paymentWrapper.eq(FeePayment::getBillId, id);
        feePaymentService.remove(paymentWrapper);
        
        // 再删除关联的费用明细
        LambdaQueryWrapper<FeeDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(FeeDetail::getBillId, id);
        feeDetailService.remove(detailWrapper);
        
        // 最后删除账单
        return super.removeById(id);
    }

    @Override
    public Map<String, Object> getStatistics(String startMonth, String endMonth) {
        Map<String, Object> result = new HashMap<>();

        // 如果没有传入时间参数，查询最近12个月的数据
        if (startMonth == null || startMonth.isEmpty()) {
            LocalDate now = LocalDate.now();
            endMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            startMonth = now.minusMonths(11).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        if (endMonth == null || endMonth.isEmpty()) {
            endMonth = startMonth;
        }

        BigDecimal totalReceivable = feeBillMapper.sumPayableAmount(startMonth, endMonth);
        BigDecimal totalReceived = feeBillMapper.sumPaidAmount(startMonth, endMonth);
        BigDecimal totalUnpaid = feeBillMapper.sumUnpaidAmount(startMonth, endMonth);

        if (totalReceivable == null) totalReceivable = BigDecimal.ZERO;
        if (totalReceived == null) totalReceived = BigDecimal.ZERO;
        if (totalUnpaid == null) totalUnpaid = BigDecimal.ZERO;

        result.put("totalReceivable", totalReceivable);
        result.put("totalReceived", totalReceived);
        result.put("totalUnpaid", totalUnpaid);

        BigDecimal collectionRate = BigDecimal.ZERO;
        if (totalReceivable.compareTo(BigDecimal.ZERO) > 0) {
            collectionRate = totalReceived.divide(totalReceivable, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        result.put("collectionRate", collectionRate);

        List<Map<String, Object>> monthlyStats = feeBillMapper.selectMonthlyStats(startMonth, endMonth);
        result.put("monthlyStats", monthlyStats);

        return result;
    }

    @Override
    public List<Map<String, Object>> getDebtorList() {
        return feeBillMapper.selectDebtorList();
    }
}
