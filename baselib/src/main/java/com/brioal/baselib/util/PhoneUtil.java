package com.brioal.baselib.util;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 手机信息相关类
 * 1.判断是否是手机
 * 2.获取手机的IMIE
 * 3.获取手机状态信息
 * 4.拨打电话
 * 5.发送短信
 * 6.获取手机联系人
 * 7.打开手机联系人界面点击联系人后，便获取该号码
 * 8.获取手机短信并保存到xml中
 * 9.打开网络设置界面
 * 10.判断wifi是否连接状态
 * 11.获取移动网络运营商名称，如中国联通、中国移动、中国电信
 * 12.返回移动终端类型
 * 13.判断手机连接的网络类型(2G,3G,4G)
 * 14.判断当前手机的网络类型(WIFI还是2,3,4G)，需要用到上面的方法
 * Created by Brioal on 2016/8/1.
 */

public class PhoneUtil {

    //判断是否是手机
    public static boolean isPhone(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    //获取手机的IMIE
    public static String getDeviceIMIE(Context context) {
        String deviceID = "";
        if (isPhone(context)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceID = tm.getDeviceId();
        } else {
            deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceID;
    }

    public static String getPhoneStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String str = "";
        str += "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
        str += "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n";
        str += "Line1Number = " + tm.getLine1Number() + "\n";
        str += "NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n";
        str += "NetworkOperator = " + tm.getNetworkOperator() + "\n";
        str += "NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n";
        str += "NetworkType = " + tm.getNetworkType() + "\n";
        str += "honeType = " + tm.getPhoneType() + "\n";
        str += "SimCountryIso = " + tm.getSimCountryIso() + "\n";
        str += "SimOperator = " + tm.getSimOperator() + "\n";
        str += "SimOperatorName = " + tm.getSimOperatorName() + "\n";
        str += "SimSerialNumber = " + tm.getSimSerialNumber() + "\n";
        str += "SimState = " + tm.getSimState() + "\n";
        str += "SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n";
        str += "VoiceMailNumber = " + tm.getVoiceMailNumber() + "\n";
        return str;
    }

    // 需添加权限<uses-permission android:name="android.permission.CALL_PHONE"/>

     //拨打电话
    public static void callDial(Context context, String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
            return;
        }

    }

    //发送短信
    public static void sendSms(Context context, String phoneNumber, String content) {
        Uri uri = Uri.parse("smsto:" + (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", TextUtils.isEmpty(content) ? "" : content);
        context.startActivity(intent);
    }

    public static List<HashMap<String, String>> getAllContactInfo(Context context) {
        SystemClock.sleep(3000);
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        // 1.获取内容解析者
        ContentResolver resolver = context.getContentResolver();
        // 2.获取内容提供者的地址:com.android.contacts
        // raw_contacts表的地址 :raw_contacts
        // view_data表的地址 : data
        // 3.生成查询地址
        Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri date_uri = Uri.parse("content://com.android.contacts/data");
        // 4.查询操作,先查询raw_contacts,查询contact_id
        // projection : 查询的字段
        Cursor cursor = resolver.query(raw_uri, new String[]{"contact_id"},
                null, null, null);
        // 5.解析cursor
        while (cursor.moveToNext()) {
            // 6.获取查询的数据
            String contact_id = cursor.getString(0);
            // cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex
            // : 查询字段在cursor中索引值,一般都是用在查询字段比较多的时候
            // 判断contact_id是否为空
            if (!TextUtils.isEmpty(contact_id)) {//null   ""
                // 7.根据contact_id查询view_data表中的数据
                // selection : 查询条件
                // selectionArgs :查询条件的参数
                // sortOrder : 排序
                // 空指针: 1.null.方法 2.参数为null
                Cursor c = resolver.query(date_uri, new String[]{"data1",
                                "mimetype"}, "raw_contact_id=?",
                        new String[]{contact_id}, null);
                HashMap<String, String> map = new HashMap<String, String>();
                // 8.解析c
                while (c.moveToNext()) {
                    // 9.获取数据
                    String data1 = c.getString(0);
                    String mimetype = c.getString(1);
                    // 10.根据类型去判断获取的data1数据并保存
                    if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                        // 电话
                        map.put("phone", data1);
                    } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                        // 姓名
                        map.put("name", data1);
                    }
                }
                // 11.添加到集合中数据
                list.add(map);
                // 12.关闭cursor
                c.close();
            }
        }
        // 12.关闭cursor
        cursor.close();
        return list;
    }

    /**
     * 打开手机联系人界面点击联系人后，便获取该号码
     * 参照以下代码
     */
    private void getContantNum() {
    /*
    Intent intent = new Intent();
    intent.setAction("android.intent.action.PICK");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.setType("vnd.android.cursor.dir/phone_v2");
    startActivityForResult(intent, 0);

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            String num = null;
            // 创建内容解析者
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                num = cursor.getString(cursor.getColumnIndex("data1"));
            }
            cursor.close();
            num = num.replaceAll("-", "");//替换的操作,555-6 -> 5556
        }
    }
    */
    }


    //需添加权限<uses-permission android:name="android.permission.READ_SMS"/>
    //需添加权限<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    public static void getAllSMS(Context context) {
        //1.获取短信
        //1.1获取内容解析者
        ContentResolver resolver = context.getContentResolver();
        //1.2获取内容提供者地址   sms,sms表的地址:null  不写
        //1.3获取查询路径
        Uri uri = Uri.parse("content://sms");
        //1.4.查询操作
        //projection : 查询的字段
        //selection : 查询的条件
        //selectionArgs : 查询条件的参数
        //sortOrder : 排序
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        //设置最大进度
        int count = cursor.getCount();//获取短信的个数
        //2.备份短信
        //2.1获取xml序列器
        XmlSerializer xmlSerializer = Xml.newSerializer();
        try {
            //2.2设置xml文件保存的路径
            //os : 保存的位置
            //encoding : 编码格式
            xmlSerializer.setOutput(new FileOutputStream(new File("/mnt/sdcard/backupsms.xml")), "utf-8");
            //2.3设置头信息
            //standalone : 是否独立保存
            xmlSerializer.startDocument("utf-8", true);
            //2.4设置根标签
            xmlSerializer.startTag(null, "smss");
            //1.5.解析cursor
            while (cursor.moveToNext()) {
                SystemClock.sleep(1000);
                //2.5设置短信的标签
                xmlSerializer.startTag(null, "sms");
                //2.6设置文本内容的标签
                xmlSerializer.startTag(null, "address");
                String address = cursor.getString(0);
                //2.7设置文本内容
                xmlSerializer.text(address);
                xmlSerializer.endTag(null, "address");
                xmlSerializer.startTag(null, "date");
                String date = cursor.getString(1);
                xmlSerializer.text(date);
                xmlSerializer.endTag(null, "date");
                xmlSerializer.startTag(null, "type");
                String type = cursor.getString(2);
                xmlSerializer.text(type);
                xmlSerializer.endTag(null, "type");
                xmlSerializer.startTag(null, "body");
                String body = cursor.getString(3);
                xmlSerializer.text(body);
                xmlSerializer.endTag(null, "body");
                xmlSerializer.endTag(null, "sms");
                System.out.println("address:" + address + "   date:" + date + "  type:" + type + "  body:" + body);
            }
            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();
            //2.8将数据刷新到文件中
            xmlSerializer.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //打开网络设置界面
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    //判断wifi是否连接状态
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    //获取移动网络运营商名称，如中国联通、中国移动、中国电信
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    // PHONE_TYPE_NONE :0 手机制式未知
// PHONE_TYPE_GSM :1 手机制式为GSM，移动和联通
// PHONE_TYPE_CDMA :2 手机制式为CDMA，电信
// PHONE_TYPE_SIP:3
    //返回移动终端类型
    public static int getPhoneType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType();
    }


    // 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
    public class Constants {
        public static final int NETWORK_CLASS_UNKNOWN = 0;
        public static final int NETWORK_WIFI = 1;
        public static final int NETWORK_CLASS_2_G = 2;
        public static final int NETWORK_CLASS_3_G = 3;
        public static final int NETWORK_CLASS_4_G = 4;
    }

    //判断手机连接的网络类型(2G,3G,4G)
    public static int getNetWorkClass(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return Constants.NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return Constants.NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return Constants.NETWORK_CLASS_4_G;
            default:
                return Constants.NETWORK_CLASS_UNKNOWN;
        }
    }

    public static int getNetWorkStatus(Context context) {
        int netWorkType = Constants.NETWORK_CLASS_UNKNOWN;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = Constants.NETWORK_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                netWorkType = getNetWorkClass(context);
            }
        }
        return netWorkType;
    }


}
