package henu.soft.xiaosi.order.web;

import henu.soft.common.exception.NoStockException;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.order.service.OrderService;
import henu.soft.xiaosi.order.vo.OrderConfirmVo;
import henu.soft.xiaosi.order.vo.OrderSubmitVo;
import henu.soft.xiaosi.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{page}/order.html")
    public String toPage(@PathVariable("page") String page) {
        return page;
    }

    @RequestMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrder", confirmVo);
        return "confirm";
    }


    /**
     * 确认订单
     * @param submitVo
     * @param model
     * @param attributes
     * @return
     */

    @RequestMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, Model model, RedirectAttributes attributes) {
        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(submitVo);
            Integer code = responseVo.getCode();
            if (code == 0) {
                model.addAttribute("order", responseVo.getOrder());
                return "pay";
            } else {
                String msg = "下单失败;";
                switch (code) {
                    case 1:
                        msg += "防重令牌校验失败";
                        break;
                    case 2:
                        msg += "商品价格发生变化";
                        break;
                }
                attributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.gulishop.cn/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String msg = "下单失败，商品无库存";
                attributes.addFlashAttribute("msg", msg);
            }
            return "redirect:http://order.gulishop.cn/toTrade";
        }
    }





}
