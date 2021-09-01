package henu.soft.xiaosi.product;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import henu.soft.xiaosi.product.controller.AttrGroupController;
import henu.soft.xiaosi.product.controller.BrandController;
import henu.soft.xiaosi.product.entity.BrandEntity;
import henu.soft.xiaosi.product.service.AttrGroupService;
import henu.soft.xiaosi.product.service.BrandService;

import henu.soft.xiaosi.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@SpringBootTest
class GuliShopProductCommonApplicationTests {

    @Autowired
    BrandService brandService;



    @Test
    void contextLoads() {

        BrandEntity entity = new BrandEntity();
        entity.setDescript("xiaosi");
        entity.setLogo("logo");
        brandService.save(entity);
        System.out.println("保存成功！");
    }

    @Test
    void testUpdateById() {
        BrandEntity entity = new BrandEntity();
        entity.setBrandId(1L);
        entity.setDescript("new_xiaosi");
        entity.setLogo("new_logo");
        brandService.updateById(entity);
        System.out.println("修改成功！");
    }

    @Test
    void testQuery() {
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("logo", "new_logo"));
        for (BrandEntity brandEntity : list) {
            System.out.println(brandEntity);
        }
        System.out.println("查询完毕！");

    }

//    @Test
//    void testSDKUpload() {
//        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        String endpoint = "oss-cn-qingdao.aliyuncs.com";
//        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "guli-shop@1508316576041390.onaliyun.com";
//        String accessKeySecret = "Zd89li7uzQNFvIEgHfjclauW8JuUhy";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//            // 创建PutObjectRequest对象。
//            // 依次填写Bucket名称（例如examplebucket）、Object完整路径（例如exampledir/exampleobject.txt）和本地文件的完整路径。Object完整路径中不能包含Bucket名称。
//            // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
//        PutObjectRequest putObjectRequest = new PutObjectRequest("guli-shop-xiaosi", "brand/11.txt", new File("D:\\iPhone 6sp照片\\DCIM\\100APPLE\\IMG_0058.JPG"));
//
//            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
//            // ObjectMetadata metadata = new ObjectMetadata();
//            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
//            // metadata.setObjectAcl(CannedAccessControlList.Private);
//            // putObjectRequest.setMetadata(metadata);
//
//        // 上传文件。
//        ossClient.putObject(putObjectRequest);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//        System.out.println("上传完成~~");
//    }



//    @Autowired
//    OSS ossClient;
//
//    @Test
//    void testUpload() throws FileNotFoundException {
//        ossClient.putObject("guli-shop-xiaosi", "11", new FileInputStream("C:\\Users\\司超龙\\Pictures\\meizi.jpg"));
//
//    }


    @Autowired
    BrandController brandController;

    @Test
    void testValid(){
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("");
        brandController.save(brandEntity);
    }

    @Autowired
    CategoryService categoryService;
    @Test
    void testGetCatelogPath(){
        Long[] path = categoryService.getCatelogPathByCatelogId(225L);
        System.out.println(path);
    }

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void testRedis(){

        // 存入
        redisTemplate.opsForValue().set("name","xiaosi");

        // 取出
        System.out.println(redisTemplate.opsForValue().get("name"));


    }

    @Autowired
    RedissonClient redissonClient;

    @Test
    void testRedisson(){
        System.out.println(redissonClient);
    }



}
