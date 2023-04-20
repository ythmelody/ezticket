package com.ezticket.ecpay.service;

import com.ezticket.web.activity.pojo.Seats;
import com.ezticket.web.activity.pojo.Session;
import com.ezticket.web.activity.pojo.Tdetails;
import com.ezticket.web.activity.pojo.Torder;
import com.ezticket.web.activity.repository.SeatsRepository;
import com.ezticket.web.activity.repository.SessionRepository;
import com.ezticket.web.activity.repository.TdetailsRepository;
import com.ezticket.web.activity.repository.TorderRepository;
import com.ezticket.web.product.pojo.Pdetails;
import com.ezticket.web.product.pojo.Porder;
import com.ezticket.web.product.pojo.Product;
import com.ezticket.web.product.repository.PdetailsRepository;
import com.ezticket.web.product.repository.PorderRepository;
import com.ezticket.web.product.repository.ProductDAO;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import ecpay.payment.integration.domain.QueryTradeInfoObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private PorderRepository porderRepository;
    @Autowired
    private PdetailsRepository pdetailsRepository;
    @Autowired
    private ProductDAO productRepository;

    //	Add by Shawn on 04/17
    @Autowired
    private TorderRepository torderRepository;

    @Autowired
    private TdetailsRepository tdetailsRepository;

    @Autowired
    private SeatsRepository seatsRepository;

    @Autowired
    private SessionRepository sessionRepository;


    public String ecpayCheckout(Integer proderno) {

        // 綠界的方法裡面都有註解，可以點進去看

        // 建立AllInOne物件
        AllInOne all = new AllInOne("");
        // 取得訂單
        Porder porder = porderRepository.getReferenceById(proderno);
        AioCheckOutALL obj = new AioCheckOutALL();
        // // 綠界規定須20碼,放入訂單編號15碼+訂單編號5碼
        obj.setMerchantTradeNo("ezTicket0000000" + proderno);
        // 取得當前時間，放入時間
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String Now = now.format(formatter);
        obj.setMerchantTradeDate(Now);
        // 放入結帳金額
        obj.setTotalAmount(porder.getPchecktotal().toString());
        // 交易敘述
        obj.setTradeDesc("ezTicket 售票平台");
        // 商品明細只能是一個字串 <br> \n 能換行的都試過了，不要浪費時間
        // 將品項一個一個加入字串中
        String itemList = "";
        List<Pdetails> pdetailsList = pdetailsRepository.findByPorderno(proderno);
        for (int i = 0; i < pdetailsList.size(); i++) {
            Pdetails pdetails = pdetailsList.get(i);
            Product product = productRepository.getByPrimaryKey(pdetails.getPdetailsNo().getProductno());
            itemList += (i + 1) + ". " + product.getPname() + " x " + pdetails.getPorderqty() + ", ";
        }
        // 塞入商品明細
        obj.setItemName(itemList);


        // 這段沒用了，沒有HTTP收不到綠界回傳值-----------------------------------------------
//		// 因應大家IP不同，用方法取得自己的ip
//		InetAddress ip = null;
//		try {
//			// 使用可能會拋出異常的方法
//			ip = InetAddress.getLocalHost();
//		} catch (UnknownHostException e) {
//			// 處理異常
//			System.err.println(e);
//		}
//		String hostname = ip.getHostAddress();
//		// 交易成功時回傳值接收的路徑，但是收不到還在測試中
//		String returnURL = "http://" + hostname + ":8085/ecpay/return";
        // -------------------------------------------------------------------------------

        //	放入你自己架的HTTP
        //	沒有的話下載並安裝 Ngrok，並註冊一個 Ngrok 帳號。
        //	啟動本地伺服器，例如 Tomcat 或是 Spring Boot。
        //	在命令列輸入 ngrok http 8080，其中 8080 是你本機伺服器的 Port，請依實際情況更改。
        //	Ngrok 會顯示一個公開的 URL，例如 http://xxxxxx.ngrok.io，複製此 URL。
        String returnURL = "https://f18d-2001-b400-e250-3c95-1826-2468-e665-af7c.jp.ngrok.io";

        obj.setReturnURL(returnURL + "/ecpay/return");
        // 是否需要額外的付款資訊ngrok http 8080
        obj.setNeedExtraPaidInfo("N");
        // 會回傳一個form表單
        String form = all.aioCheckOut(obj, null);

        return form;
    }


    // 訂單查詢
    public String checkorder(String merchantTradeNo) {
        QueryTradeInfoObj queryTradeInfoObj = new QueryTradeInfoObj();
        queryTradeInfoObj.setMerchantID("2000132");
        queryTradeInfoObj.setMerchantTradeNo(merchantTradeNo);
        queryTradeInfoObj.setTimeStamp(String.valueOf(Instant.now().getEpochSecond()));
        AllInOne allInOne = new AllInOne("");
        return allInOne.queryTradeInfo(queryTradeInfoObj);
    }

    public void orderPayStatus() {

    }


    //	=========================================== 節目訂單 ==================================================
    public String ecpayTCheckout(Integer torderNo) {
        // 建立AllInOne物件
        AllInOne all = new AllInOne("");
        // 取得訂單
        Torder torder = torderRepository.getReferenceById(torderNo);
        AioCheckOutALL obj = new AioCheckOutALL();
        // // 綠界規定須20碼,放入訂單編號15碼+訂單編號5碼
        obj.setMerchantTradeNo("ezTicket0000000" + torderNo);
        // 取得當前時間，放入時間
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String Now = now.format(formatter);
        obj.setMerchantTradeDate(Now);
        // 放入結帳金額
        obj.setTotalAmount(torder.getTcheckTotal().toString());
        // 交易敘述
        obj.setTradeDesc("ezTicket 售票平台");
        // 將票券資訊濃縮為一個字串
        String itemList = "";

        List<Tdetails> tdetailsList = tdetailsRepository.findByTorderno(torderNo);

        Tdetails tdetails = tdetailsList.get(0);

        Session session = sessionRepository.getById(tdetails.getSessionNo());

        if(tdetails.getSeatNo() != null){
            Seats seats = seatsRepository.getById(tdetails.getSeatNo());
            itemList = session.getActivity().getAName() + " - " + session.getSessionsTime() + " - " + seats.getBlockName() + " 區 * " + tdetailsList.size() + " 張, ";
        } else {
            itemList = session.getActivity().getAName() + " - " + session.getSessionsTime() + " - 無區分 * " + tdetails.getTqty() + " 張, ";
        }

        // 塞入票券明細
        obj.setItemName(itemList);

        //	放入你自己架的 HTTPS
        //	沒有的話下載並安裝 Ngrok，並註冊一個 Ngrok 帳號。
        //	啟動本地伺服器，例如 Tomcat 或是 Spring Boot。
        //	在命令列輸入 ngrok http 8080，其中 8080 是你本機伺服器的 Port，請依實際情況更改。
        //	Ngrok 會顯示一個公開的 URL，例如 https://xxxxxx.ngrok.io，複製此 URL。
        String returnURL = "https://7810-2407-4b00-1c00-8929-507e-f5da-8a37-25b8.jp.ngrok.io";
        obj.setReturnURL(returnURL + "/ecpay/Treturn");

        // 設定票券訂單明細路徑及返回商店路徑
        // 利用自身IP取得路徑返回商店
        // 因應大家IP不同，用方法取得自己的ip
        InetAddress ip = null;
        try {
            // 使用可能會拋出異常的方法
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // 處理異常
            System.err.println(e);
        }
        String hostname = ip.getHostAddress();
        String local = "http://" + hostname + ":8085";
        String confirmedUrl = "/front-activity-orderconfirm.html?id=" + torderNo;
        obj.setClientBackURL(local + confirmedUrl);

        // 是否需要額外的付款資訊 ngrok http 8080
        obj.setNeedExtraPaidInfo("N");

        // 回傳 form 表單
        String form = all.aioCheckOut(obj, null);

        return form;
    }

    // 每五分鐘檢查超過十分鐘未付款的訂單，將座位釋放且將訂單改為已結案
    @Scheduled(fixedRate = 300000)
    public void checkTorders() {
        List<Torder> unPaidOrders = torderRepository.findAllByUnpaidAndBeforeTenMins();

        for (Torder torder : unPaidOrders) {
            String merchantTradeNo = "ezTicket0000000" + torder.getTorderNo();

            QueryTradeInfoObj queryTradeInfoObj = new QueryTradeInfoObj();
            queryTradeInfoObj.setMerchantID("2000132");
            queryTradeInfoObj.setMerchantTradeNo(merchantTradeNo);
            queryTradeInfoObj.setTimeStamp(String.valueOf(Instant.now().getEpochSecond()));
            AllInOne allInOne = new AllInOne("");

            String tradeInfo = allInOne.queryTradeInfo(queryTradeInfoObj);
            if (tradeInfo.contains("TradeStatus=1&")) {

                String strArr[] = tradeInfo.split("&");

                Timestamp tsp = null;
                for(String s: strArr){
                    if(s.contains("PaymentDate") && s.length() > 12){
                        tsp = Timestamp.valueOf(s.substring(12).trim().replaceAll("/", "-"));
                    }
                }

                torder.setTpayDate(tsp);
                torder.setTpaymentStatus(1);
                torder.setTprocessStatus(1);
                torderRepository.save(torder);

                // 票券 QR Code 產生應於此處 - 2 (Melody)

            } else {
                torder.setTpaymentStatus(3);
                torder.setTprocessStatus(4);
                torderRepository.save(torder);

                List<Tdetails> toBeModiDetails = tdetailsRepository.findByTorderno(torder.getTorderNo());
                for (Tdetails tdetail : toBeModiDetails) {
                    if (tdetail.getSeatNo() != null) {
                        // 若訂單失敗且節目屬於有座位的，則釋放座位，將座位改成可售出
                        seatsRepository.updateStatus(1, tdetail.getSeatNo());
                    } else {
                        // 若訂單失敗且節目屬於有無座位的，則已售出票券數量減少
                        sessionRepository.updateStandingQtyById(-tdetail.getTqty(), tdetail.getSessionNo());
                    }
                }
            }
        }
    }
}
