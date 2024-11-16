package com.wst;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import okhttp3.*;
import org.json.JSONObject;
public class Main {

    // 数据库连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // 向产品表插入数据
    public static void addProduct(String picture, String name, String introduce, double price, double vipPrice,String sort) {
        String sql = "INSERT INTO product (picture, name,introduce, price, vip_price, sort,gmt_create, gmt_modified) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?,?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // 设置参数
            preparedStatement.setString(1, picture);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, introduce);
            preparedStatement.setDouble(4, price);
            preparedStatement.setDouble(5, vipPrice);
            preparedStatement.setString(6, sort);

            // 设置当前时间
            java.sql.Timestamp currentTime = new java.sql.Timestamp(new Date().getTime());
            preparedStatement.setTimestamp(7, currentTime);
            preparedStatement.setTimestamp(8, currentTime);

            // 执行插入操作
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Product added successfully!");
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String uploadFile(File file, String url) throws Exception {
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + file.getAbsolutePath());
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/png"));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("请求失败，状态码: " + response.code());
            }

            // 解析 JSON 响应
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);

            // 提取 data 字段
            return jsonObject.optString("data", "链接未找到");
        }
    }

    public static void main(String[] args)throws Exception {
        // 调用addProduct方法添加一个新产品
        //addProduct("image_url", "Product Name", "Product Description", 99.99, 89.99);
        String[][] products = new String[1000][10];
        System.out.print("请输入要遍历的文件夹路径: ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        File folder = new File(path);

        // 调用方法遍历文件夹
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("指定路径不是有效的文件夹！");
            return;
        }
        int cur=0;
        File[] files = folder.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                cur++;
                products[cur][1]=file.getName();
                File[] tmpfile = file.listFiles();
                for(File tmp : tmpfile) {
                    String tmpname = tmp.getName();
                    int index = tmpname.lastIndexOf(".");
                    String name = tmpname.substring(0, index);
                    String extension = tmpname.substring(index+1);
                    if(extension.equals("txt")) {
                        products[cur][2]=name;
                        products[cur][3]=new String(Files.readAllBytes(tmp.toPath()),"GBK");
                    }else{
                        products[cur][4]=name.replaceAll("[^0-9.]", "");
                        products[cur][5]=uploadFile(tmp, "http://localhost:8877/product/upload");
                    }
                }
            }
        }
        for(int i=1; i<=cur; i++) {
            addProduct(products[i][5],products[i][1],products[i][3],Double.parseDouble(products[i][4]),Double.parseDouble(products[i][4])/2,products[i][2]);
            for(int j=1; j<=5; j++) {

                System.out.print(products[i][j]+"\n");
            }
        }
    }
}
