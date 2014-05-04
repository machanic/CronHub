package org.cronhub.managesystem.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.cronhub.managesystem.commons.dao.bean.User;
import org.cronhub.managesystem.commons.params.Params;

public class SinaAlertUtils {
	private String mailurl;
	private String smsurl;

	
	
	public void setMailurl(String mailurl) {
		this.mailurl = mailurl;
	}
	public void setSmsurl(String smsurl) {
		this.smsurl = smsurl;
	}
	
	public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
	  
	public void alertSms(List<User> users, String content){
		StringBuilder smsUser = new StringBuilder();
		for(User u : users){
			smsUser.append(u.getMail_name()).append(",");
		}
		smsUser.deleteCharAt(smsUser.length()-1);
		
		try {
			String param = "type=1&send_list="+smsUser.toString()+"&content="+URLEncoder.encode(content,"utf-8");
		
			String ret = sendPost(this.smsurl, param);
			System.out.println(ret);
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void alertMail(List<User> users,  String title, String content){
		StringBuilder mailUser = new StringBuilder();
		for(User u : users){
			mailUser.append(u.getMail_name()).append(",");
		}
		mailUser.deleteCharAt(mailUser.length()-1);
		
		String mailUserSend = mailUser.toString();
		try {
			String param = "type=0&send_list="+mailUserSend+"&subject="+URLEncoder.encode(title, "utf-8")+"&content="+URLEncoder.encode(content,"utf-8");
		
			String ret = sendPost(this.mailurl, param);
			System.out.println(ret);
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	public static void main(String[] args) {
		SinaAlertUtils u = new SinaAlertUtils();
		u.setMailurl("http://logging.yunwei.intra.sina.com.cn/interface/send_mail_v5.php");
		u.setMailuser("[\"machen3\"]");
		u.setSmsurl("http://logging.yunwei.intra.sina.com.cn/interface/send_mail_v5.php");
		u.setSmsuser("[\"machen3\"]");
		//u.alertMail("这是来自调度系统","测试内容");
		String message = "cronhub报警["+Params.date_format_page.format(new Date())+
												"]!机器:10.39.3.113,任务失败:/usr/home/sina_recmd/work/model/hour.sh 2012 33, \nlog描述:清理3天前的数据: clean /dw_ext/sinarecmd/tmp/model/ 3 clean /dw_ext/sinarecmd/rpt/model/model/ 3 clean /dw_ext/sinarecmd/rpt/model/user/ 3 clean /dw_ext/sinarecmd/adm/model/store 3";
		u.alertMail("测试报警", message);
		System.out.println("done");
	}
	**/
}
