package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import javax.websocket.server.ServerEndpoint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单记录
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 查询指定状态的订单数量
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    @Select("select sum(amount) from orders where status = #{status} and (order_time between #{begin} and #{end})")
    Double getTurnover(LocalDateTime begin, LocalDateTime end, Integer status);

    Integer getOrder(LocalDateTime begin, LocalDateTime end, Integer status);

    @Select("select order_detail.name, sum(order_detail.number) number from orders, order_detail" +
            " where order_detail.order_id = orders.id and orders.status = 5 and " +
            "(orders.order_time between #{begin} and #{end}) group by order_detail.name" +
            " order by number desc limit 10")
    List<GoodsSalesDTO> getSalesTop10(LocalDate begin, LocalDate end);
}
