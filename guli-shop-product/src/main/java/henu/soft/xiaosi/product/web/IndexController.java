package henu.soft.xiaosi.product.web;

import henu.soft.xiaosi.product.entity.CategoryEntity;
import henu.soft.xiaosi.product.service.CategoryService;
import henu.soft.xiaosi.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model){

        // TODO 查出来所有的一级分类

       List<CategoryEntity> categoryEntities =  categoryService.getLevel1Categories();

        model.addAttribute("categorys",categoryEntities);

        return "index";

    }
    //index/catalog.json

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }


    @Autowired
    RedissonClient redissonClient;

    /**
     * 测试方法
     * @return
     */
    @GetMapping("/xiaosi")
    @ResponseBody
    public String hello() {

        RLock lock = redissonClient.getLock("mylock");

        // 默认加锁是30s
        // 不够的化看门狗机制自动续期
        // 若发生业务异常，锁不会被续期，自动解锁
        lock.lock();


        try {
            // 模拟业务，此时时间大于锁自动取消的时间
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getId()+ "执行了业务...");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "xiaosi";

    }

}
