package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取营业额
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Double turnover = orderMapper.getTurnover(LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX), Orders.COMPLETED);
            if (turnover == null) {
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 获取用户数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Integer newUser = userMapper.getUser(
                    LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX));

            Integer totalUser = userMapper.getUser(null, LocalDateTime.of(date, LocalTime.MAX));
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 获取订单数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> totalOrderList = new ArrayList<>();
        List<Integer> completeOrderList = new ArrayList<>();
        Integer totalOrderNumber = 0;
        Integer completeOrderNumber = 0;
        Double orderCompletionRate;

        for (LocalDate date : dateList) {
            Integer totalOrder = orderMapper.getOrder(
                    LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX),
                    null
            );
            Integer completeOrder = orderMapper.getOrder(
                    LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX),
                    Orders.COMPLETED
            );
            totalOrderList.add(totalOrder);
            completeOrderList.add(completeOrder);
            totalOrderNumber += totalOrder;
            completeOrderNumber += completeOrder;
        }
        orderCompletionRate = 1.0 * completeOrderNumber / totalOrderNumber;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .totalOrderCount(totalOrderNumber)
                .validOrderCount(completeOrderNumber)
                .validOrderCountList(StringUtils.join(completeOrderList, ","))
                .build();
    }

    /**
     * 销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(begin, end);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOList) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.isAfter(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        return dateList;
    }
}
