package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.BalanceRecord;
import com.elderly.apartment.entity.CareBooking;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.mapper.BalanceRecordMapper;
import com.elderly.apartment.mapper.CareBookingMapper;
import com.elderly.apartment.service.CareBookingService;
import com.elderly.apartment.service.UserService;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class CareBookingServiceImpl extends ServiceImpl<CareBookingMapper, CareBooking> implements CareBookingService {

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceRecordMapper balanceRecordMapper;

    private final AtomicInteger sequence = new AtomicInteger(0);
    private final Random random = new Random();

    /**
     * 生成订单号：BK + 年月日 + 时间戳后4位 + 2位随机数 + 2位序列号
     * 格式：BK202503031234560199
     * 确保每次重启后也不会重复
     */
    private synchronized String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 使用当前时间的毫秒后4位
        long timestamp = System.currentTimeMillis() % 10000;
        // 2位随机数
        int rand = random.nextInt(100);
        // 2位序列号（防止同一毫秒内重复）
        int seq = sequence.incrementAndGet() % 100;
        return String.format("BK%s%04d%02d%02d", dateStr, timestamp, rand, seq);
    }

    @Override
    public boolean save(CareBooking entity) {
        // 自动生成订单号
        if (entity.getOrderNo() == null || entity.getOrderNo().isEmpty()) {
            entity.setOrderNo(generateOrderNo());
        }
        // 设置默认值
        if (entity.getStatus() == null) {
            entity.setStatus("pending");
        }
        if (entity.getPaymentStatus() == null) {
            entity.setPaymentStatus("pending");
        }
        // 自动填充住客姓名和房间号
        if (entity.getElderlyId() != null) {
            // 从user表查询老人信息（user_type=2）
            User user = userService.getById(entity.getElderlyId().intValue());
            if (user != null && user.isElderly()) {
                if (entity.getElderlyName() == null || entity.getElderlyName().isEmpty()) {
                    entity.setElderlyName(user.getRealName());
                }
                if (entity.getRoomNumber() == null || entity.getRoomNumber().isEmpty()) {
                    entity.setRoomNumber(user.getRoomNumber());
                }
                if (entity.getPhone() == null || entity.getPhone().isEmpty()) {
                    entity.setPhone(user.getPhone());
                }
            }
        }
        return super.save(entity);
    }

    @Override
    public IPage<CareBooking> getBookingPage(Integer page, Integer size, Map<String, Object> params) {
        Page<CareBooking> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CareBooking> wrapper = new LambdaQueryWrapper<>();

        // 根据参数添加筛选条件
        if (params != null) {
            // 状态筛选
            if (params.containsKey("status") && params.get("status") != null) {
                String status = params.get("status").toString();
                if (!status.isEmpty()) {
                    wrapper.eq(CareBooking::getStatus, status);
                }
            }

            // 护理员ID筛选
            if (params.containsKey("nurseId") && params.get("nurseId") != null) {
                try {
                    Long nurseId = Long.valueOf(params.get("nurseId").toString());
                    wrapper.eq(CareBooking::getNurseId, nurseId);
                } catch (NumberFormatException e) {
                    log.warn("nurseId 参数格式错误: {}", params.get("nurseId"));
                }
            }

            // 查询未分配的订单（nurseId为null）
            if (params.containsKey("unassigned") && "true".equals(params.get("unassigned").toString())) {
                wrapper.isNull(CareBooking::getNurseId);
                // 待接单只查询未过期的订单（预约日期 >= 今天）
                wrapper.ge(CareBooking::getAppointmentDate, LocalDate.now());
            }

            // 住客姓名模糊查询
            if (params.containsKey("elderlyName") && params.get("elderlyName") != null) {
                String elderlyName = params.get("elderlyName").toString();
                if (!elderlyName.isEmpty()) {
                    wrapper.like(CareBooking::getElderlyName, elderlyName);
                }
            }

            // 预约日期范围查询
            if (params.containsKey("startDate") && params.get("startDate") != null) {
                String startDate = params.get("startDate").toString();
                if (!startDate.isEmpty()) {
                    wrapper.ge(CareBooking::getAppointmentDate, startDate);
                }
            }
            if (params.containsKey("endDate") && params.get("endDate") != null) {
                String endDate = params.get("endDate").toString();
                if (!endDate.isEmpty()) {
                    wrapper.le(CareBooking::getAppointmentDate, endDate);
                }
            }
        }

        // 按创建时间倒序，如果创建时间相同则按ID倒序
        wrapper.orderByDesc(CareBooking::getCreateTime)
               .orderByDesc(CareBooking::getId);
        IPage<CareBooking> result = this.page(pageParam, wrapper);

        // 自动填充住客姓名和房间号（如果为空）
        if (result.getRecords() != null) {
            for (CareBooking booking : result.getRecords()) {
                log.info("查询到预约记录: id={}, elderlyId={}, elderlyName={}, roomNumber={}",
                    booking.getId(), booking.getElderlyId(), booking.getElderlyName(), booking.getRoomNumber());
                if ((booking.getElderlyName() == null || booking.getElderlyName().isEmpty() ||
                     booking.getRoomNumber() == null || booking.getRoomNumber().isEmpty())
                    && booking.getElderlyId() != null) {
                    // 从user表查询老人信息
                    User user = userService.getById(booking.getElderlyId().intValue());
                    log.info("查询到老人信息: id={}, name={}, roomNumber={}",
                        user != null ? user.getId() : null,
                        user != null ? user.getRealName() : null,
                        user != null ? user.getRoomNumber() : null);
                    if (user != null) {
                        if (booking.getElderlyName() == null || booking.getElderlyName().isEmpty()) {
                            booking.setElderlyName(user.getRealName());
                        }
                        if (booking.getRoomNumber() == null || booking.getRoomNumber().isEmpty()) {
                            booking.setRoomNumber(user.getRoomNumber());
                        }
                        if (booking.getPhone() == null || booking.getPhone().isEmpty()) {
                            booking.setPhone(user.getPhone());
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    @Transactional
    public boolean confirmBooking(Long id) {
        CareBooking booking = this.getById(id);
        if (booking == null) {
            return false;
        }
        // 只有待确认的订单可以确认
        if (!"pending".equals(booking.getStatus())) {
            return false;
        }
        booking.setStatus("confirmed");
        booking.setUpdateTime(LocalDateTime.now());
        return this.updateById(booking);
    }

    @Override
    @Transactional
    public boolean completeBooking(Long id) {
        CareBooking booking = this.getById(id);
        if (booking == null) {
            return false;
        }
        // 只有已分配的订单可以完成
        if (!"assigned".equals(booking.getStatus())) {
            return false;
        }
        booking.setStatus("completed");
        booking.setUpdateTime(LocalDateTime.now());
        return this.updateById(booking);
    }

    @Override
    @Transactional
    public boolean cancelBooking(Long id) {
        CareBooking booking = this.getById(id);
        if (booking == null) {
            return false;
        }

        String paymentStatus = booking.getPaymentStatus();
        String status = booking.getStatus();

        // 待支付的订单可以直接取消
        if ("pending".equals(paymentStatus)) {
            booking.setStatus("cancelled");
            booking.setUpdateTime(LocalDateTime.now());
            return this.updateById(booking);
        }

        // 已支付 + 待确认的订单可以取消并自动退款
        if ("paid".equals(paymentStatus) && "pending".equals(status)) {
            booking.setStatus("cancelled");
            booking.setPaymentStatus("refunded");
            booking.setUpdateTime(LocalDateTime.now());
            // TODO: 这里可以添加实际的退款逻辑（调用支付接口退款）
            log.info("订单 {} 已取消，自动退款金额: {}", booking.getOrderNo(), booking.getPrice());
            return this.updateById(booking);
        }

        // 其他情况不能取消
        return false;
    }

    @Override
    @Transactional
    public boolean assignNurse(Long id, Long nurseId) {
        System.out.println("分配/接单服务: id=" + id + ", nurseId=" + nurseId);
        CareBooking booking = this.getById(id);
        if (booking == null) {
            System.out.println("分配失败: 订单不存在");
            return false;
        }
        System.out.println("找到订单: id=" + booking.getId() + ", status=" + booking.getStatus() + ", nurseId=" + booking.getNurseId());

        // 检查订单是否已被其他护理员接单
        if (booking.getNurseId() != null && !booking.getNurseId().equals(nurseId)) {
            System.out.println("接单失败: 订单已被其他护理员接单");
            return false;
        }

        // 只有已确认的订单可以分配护理员（护理员自主接单）
        if (!"confirmed".equals(booking.getStatus())) {
            System.out.println("分配失败: 订单状态不正确, status=" + booking.getStatus());
            return false;
        }

        // 检查时间冲突：同一护理员在同一时间段只能有一个预约
        LocalDate appointmentDate = booking.getAppointmentDate();
        String appointmentTime = booking.getAppointmentTime();
        if (hasTimeConflict(nurseId, id, appointmentDate, appointmentTime)) {
            System.out.println("接单失败: 该时间段已有其他预约");
            return false;
        }

        // 根据nurseId获取护理员姓名
        String nurseName = getNurseNameById(nurseId);

        // 使用UpdateWrapper确保更新所有字段
        LambdaUpdateWrapper<CareBooking> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CareBooking::getId, id)
                .eq(CareBooking::getStatus, "confirmed") // 乐观锁：确保订单仍处于confirmed状态
                .set(CareBooking::getNurseId, nurseId)
                .set(CareBooking::getNurseName, nurseName)
                .set(CareBooking::getStatus, "assigned")
                .set(CareBooking::getUpdateTime, LocalDateTime.now());

        boolean result = this.update(updateWrapper);
        System.out.println("更新结果: " + result);
        return result;
    }
    
    /**
     * 根据护理员ID获取护理员姓名
     * 从user表中查询real_name
     */
    private String getNurseNameById(Long nurseId) {
        if (nurseId == null) {
            return null;
        }
        try {
            User user = userService.getById(nurseId.intValue());
            if (user != null && user.getRealName() != null && !user.getRealName().isEmpty()) {
                return user.getRealName();
            }
        } catch (Exception e) {
            log.warn("查询护理员姓名失败，nurseId={}", nurseId, e);
        }
        // 如果查询失败，返回默认名称
        return "护理员" + nurseId;
    }

    /**
     * 检查时间冲突
     * 同一护理员在同一日期和时间段不能有两个预约
     */
    private boolean hasTimeConflict(Long nurseId, Long excludeBookingId, LocalDate appointmentDate, String appointmentTime) {
        if (nurseId == null || appointmentDate == null || appointmentTime == null) {
            return false;
        }

        LambdaQueryWrapper<CareBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareBooking::getNurseId, nurseId)
                .eq(CareBooking::getAppointmentDate, appointmentDate)
                .eq(CareBooking::getAppointmentTime, appointmentTime)
                .in(CareBooking::getStatus, Arrays.asList("assigned", "completed"))
                .ne(CareBooking::getId, excludeBookingId);

        long count = this.count(wrapper);
        System.out.println("时间冲突检查: nurseId=" + nurseId + ", date=" + appointmentDate + ", time=" + appointmentTime + ", count=" + count);
        return count > 0;
    }

    @Override
    @Transactional
    public boolean payBooking(Long id, String paymentMethod, Double amount) {
        CareBooking booking = this.getById(id);
        if (booking == null) {
            log.error("支付失败：订单不存在，id={}", id);
            return false;
        }

        // 检查订单状态
        if (!"pending".equals(booking.getStatus())) {
            log.error("支付失败：订单状态不正确，id={}, status={}", id, booking.getStatus());
            return false;
        }

        // 检查支付状态
        if (!"pending".equals(booking.getPaymentStatus())) {
            log.error("支付失败：订单已支付或已退款，id={}, paymentStatus={}", id, booking.getPaymentStatus());
            return false;
        }

        // 检查金额
        if (booking.getPrice() == null || Math.abs(booking.getPrice().doubleValue() - amount) > 0.01) {
            log.error("支付失败：金额不匹配，id={}, 订单金额={}, 支付金额={}", id, booking.getPrice(), amount);
            return false;
        }

        // 余额支付：扣减用户余额
        if ("balance".equals(paymentMethod)) {
            // 获取老人信息（从user表）
            User user = userService.getById(booking.getElderlyId().intValue());
            if (user == null || !user.isElderly()) {
                log.error("支付失败：老人信息不存在，elderlyId={}", booking.getElderlyId());
                return false;
            }

            // 检查余额是否充足
            BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            BigDecimal payAmount = BigDecimal.valueOf(amount);
            if (currentBalance.compareTo(payAmount) < 0) {
                log.error("支付失败：余额不足，当前余额={}, 需支付={}", currentBalance, payAmount);
                return false;
            }

            // 扣减余额
            user.setBalance(currentBalance.subtract(payAmount));
            user.setUpdateTime(LocalDateTime.now());
            boolean updateBalance = userService.updateById(user);
            if (!updateBalance) {
                log.error("支付失败：扣减余额失败，userId={}", user.getId());
                return false;
            }

            // 创建交易记录
            BalanceRecord record = new BalanceRecord();
            record.setElderlyId(user.getId());
            record.setAmount(payAmount.negate()); // 消费为负数
            record.setType("consume");
            record.setTitle("护理服务预约");
            record.setDescription("预约服务：" + booking.getServiceName() + "，订单号：" + booking.getOrderNo());
            record.setPaymentMethod("balance");
            record.setCreateTime(LocalDateTime.now());
            balanceRecordMapper.insert(record);

            log.info("余额扣减成功：userId={}, 扣减金额={}, 剩余余额={}", 
                user.getId(), payAmount, user.getBalance());
        }

        // 更新订单状态
        booking.setPaymentStatus("paid");
        booking.setStatus("confirmed"); // 支付后自动确认
        booking.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(booking);
        if (success) {
            log.info("护理预约支付成功：id={}, 金额={}, 支付方式={}", id, amount, paymentMethod);
        } else {
            log.error("护理预约支付失败：id={}", id);
        }

        return success;
    }
}
