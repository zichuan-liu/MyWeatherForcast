package com.lzc.mail;

import android.support.annotation.NonNull;

import java.io.File;

public class SendMailUtil {

    public static void send(final File file, String toAdd) {
        final MailInfo mailInfo = creatMail(toAdd);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo, file);
            }
        }).start();
    }

    public static void send(String toAdd) {
        final MailInfo mailInfo = creatMail(toAdd);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        }).start();
    }

    /*
        这里！！！
        改变发送邮箱的服务器和文本内容等
     */
    @NonNull
    private static MailInfo creatMail(String toAdd) {

        String HOST = "smtp.163.com";//ShareUtils.getString(MyApplication.getInstance(), "HOST", "");
        String PORT = "25";//ShareUtils.getString(MyApplication.getInstance(), "PORT", "");
        String FROM_ADD = "zrlab_sysinfo@163.com";//ShareUtils.getString(MyApplication.getInstance(), "FROM_ADD", "");
        String FROM_PSW = "dir123";//ShareUtils.getString(MyApplication.getInstance(), "FROM_PSW", "");
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);//发送方邮箱服务器
        mailInfo.setMailServerPort(PORT);//发送方邮箱端口号
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD); // 发送者邮箱地址
        mailInfo.setPassword(FROM_PSW);// 发送者邮箱授权码
        mailInfo.setFromAddress(FROM_ADD); // 发送者邮箱
        mailInfo.setToAddress(toAdd); // 接收者邮箱
        mailInfo.setSubject("Android应用测试"); // 邮件主题
        mailInfo.setContent("用户数据"); // 邮件文本
        return mailInfo;
    }

}
