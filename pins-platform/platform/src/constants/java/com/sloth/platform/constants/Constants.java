package com.sloth.platform.constants;

import android.Manifest;
import android.os.Build;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2023/8/25 17:08
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2023/8/25         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Constants {

    interface PageConstants {
        int PAGE_SIZE = 20;//默认页大小
        int START_INDEX = 1;//默认起始页
    }

    interface HintConstants {
        String NET_ERROR = "网络连接异常";
    }

    interface MemoryConstants {

        int BYTE = 1;
        int KB   = 1024;
        int MB   = 1048576;
        int GB   = 1073741824;

        @IntDef({BYTE, KB, MB, GB})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Unit { }
    }

    interface PermissionConstants {

        String CALENDAR             = "CALENDAR";
        String CAMERA               = "CAMERA";
        String CONTACTS             = "CONTACTS";
        String LOCATION             = "LOCATION";
        String MICROPHONE           = "MICROPHONE";
        String PHONE                = "PHONE";
        String SENSORS              = "SENSORS";
        String SMS                  = "SMS";
        String STORAGE              = "STORAGE";
        String ACTIVITY_RECOGNITION = "ACTIVITY_RECOGNITION";

        String[] GROUP_CALENDAR             = {
                Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR
        };
        String[] GROUP_CAMERA               = {
                Manifest.permission.CAMERA
        };
        String[] GROUP_CONTACTS             = {
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS
        };
        String[] GROUP_LOCATION             = {
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };
        String[] GROUP_MICROPHONE           = {
                Manifest.permission.RECORD_AUDIO
        };
        String[] GROUP_PHONE                = {
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.ADD_VOICEMAIL,
                Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.ANSWER_PHONE_CALLS
        };
        String[] GROUP_PHONE_BELOW_O        = {
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.ADD_VOICEMAIL,
                Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS
        };
        String[] GROUP_SENSORS              = {
                Manifest.permission.BODY_SENSORS
        };
        String[] GROUP_SMS                  = {
                Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS,
        };
        String[] GROUP_STORAGE              = {
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        String[] GROUP_ACTIVITY_RECOGNITION = {
                Manifest.permission.ACTIVITY_RECOGNITION,
        };

        @StringDef({CALENDAR, CAMERA, CONTACTS, LOCATION, MICROPHONE, PHONE, SENSORS, SMS, STORAGE,})
        @Retention(RetentionPolicy.SOURCE)
        @interface PermissionGroup { }

        static String[] getPermissions(@Constants.PermissionConstants.PermissionGroup final String permission) {
            if (permission == null) return new String[0];
            switch (permission) {
                case CALENDAR:
                    return GROUP_CALENDAR;
                case CAMERA:
                    return GROUP_CAMERA;
                case CONTACTS:
                    return GROUP_CONTACTS;
                case LOCATION:
                    return GROUP_LOCATION;
                case MICROPHONE:
                    return GROUP_MICROPHONE;
                case PHONE:
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        return GROUP_PHONE_BELOW_O;
                    } else {
                        return GROUP_PHONE;
                    }
                case SENSORS:
                    return GROUP_SENSORS;
                case SMS:
                    return GROUP_SMS;
                case STORAGE:
                    return GROUP_STORAGE;
                case ACTIVITY_RECOGNITION:
                    return GROUP_ACTIVITY_RECOGNITION;
            }
            return new String[]{permission};
        }
    }

    interface RegexConstants {

        /**
         * Regex of simple mobile.
         */
        String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
        /**
         * Regex of exact mobile.
         * <p>china mobile: 134(0-8), 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 165, 172, 178, 182, 183, 184, 187, 188, 195, 197, 198</p>
         * <p>china unicom: 130, 131, 132, 145, 155, 156, 166, 167, 175, 176, 185, 186, 196</p>
         * <p>china telecom: 133, 149, 153, 162, 173, 177, 180, 181, 189, 190, 191, 199</p>
         * <p>china broadcasting: 192</p>
         * <p>global star: 1349</p>
         * <p>virtual operator: 170, 171</p>
         */
        String REGEX_MOBILE_EXACT  = "^((13[0-9])|(14[579])|(15[0-35-9])|(16[2567])|(17[0-35-8])|(18[0-9])|(19[0-35-9]))\\d{8}$";
        /**
         * Regex of telephone number.
         */
        String REGEX_TEL           = "^0\\d{2,3}[- ]?\\d{7,8}$";
        /**
         * Regex of id card number which length is 15.
         */
        String REGEX_ID_CARD15     = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        /**
         * Regex of id card number which length is 18.
         */
        String REGEX_ID_CARD18     = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
        /**
         * Regex of email.
         */
        String REGEX_EMAIL         = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        /**
         * Regex of url.
         */
        String REGEX_URL           = "[a-zA-z]+://[^\\s]*";
        /**
         * Regex of Chinese character.
         */
        String REGEX_ZH            = "^[\\u4e00-\\u9fa5]+$";
        /**
         * Regex of username.
         * <p>scope for "a-z", "A-Z", "0-9", "_", "Chinese character"</p>
         * <p>can't end with "_"</p>
         * <p>length is between 6 to 20</p>
         */
        String REGEX_USERNAME      = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
        /**
         * Regex of date which pattern is "yyyy-MM-dd".
         */
        String REGEX_DATE          = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
        /**
         * Regex of ip address.
         */
        String REGEX_IP            = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

        ///////////////////////////////////////////////////////////////////////////
        // The following come from http://tool.oschina.net/regex
        ///////////////////////////////////////////////////////////////////////////

        /**
         * Regex of double-byte characters.
         */
        String REGEX_DOUBLE_BYTE_CHAR     = "[^\\x00-\\xff]";
        /**
         * Regex of blank line.
         */
        String REGEX_BLANK_LINE           = "\\n\\s*\\r";
        /**
         * Regex of QQ number.
         */
        String REGEX_QQ_NUM               = "[1-9][0-9]{4,}";
        /**
         * Regex of postal code in China.
         */
        String REGEX_CHINA_POSTAL_CODE    = "[1-9]\\d{5}(?!\\d)";
        /**
         * Regex of integer.
         */
        String REGEX_INTEGER              = "^(-?[1-9]\\d*)|0$";
        /**
         * Regex of positive integer.
         */
        String REGEX_POSITIVE_INTEGER     = "^[1-9]\\d*$";
        /**
         * Regex of negative integer.
         */
        String REGEX_NEGATIVE_INTEGER     = "^-[1-9]\\d*$";
        /**
         * Regex of non-negative integer.
         */
        String REGEX_NOT_NEGATIVE_INTEGER = "^[1-9]\\d*|0$";
        /**
         * Regex of non-positive integer.
         */
        String REGEX_NOT_POSITIVE_INTEGER = "^-[1-9]\\d*|0$";
        /**
         * Regex of positive float.
         */
        String REGEX_FLOAT                = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
        /**
         * Regex of positive float.
         */
        String REGEX_POSITIVE_FLOAT       = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
        /**
         * Regex of negative float.
         */
        String REGEX_NEGATIVE_FLOAT       = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$";
        /**
         * Regex of positive float.
         */
        String REGEX_NOT_NEGATIVE_FLOAT   = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$";
        /**
         * Regex of negative float.
         */
        String REGEX_NOT_POSITIVE_FLOAT   = "^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$";

        ///////////////////////////////////////////////////////////////////////////
        // If u want more please visit http://toutiao.com/i6231678548520731137
        ///////////////////////////////////////////////////////////////////////////
    }

    interface TimeConstants {

        int MSEC = 1;
        int SEC  = 1000;
        int MIN  = 60000;
        int HOUR = 3600000;
        int DAY  = 86400000;

        @IntDef({MSEC, SEC, MIN, HOUR, DAY})
        @Retention(RetentionPolicy.SOURCE)
        @interface Unit { }
    }

    interface ContentTypes {
        String JSON = "application/json";
        String STREAM = "application/octet-stream";
        String FILE = "multipart/form-data";
    }

    enum FileType {
        IMAGE(10, "image", new String[]{".png", ".bmp", ".jpg", ".jpeg", ".gif"}),
        MUSIC(20, "image", new String[]{".mp3", ".wav", ".pcm"}),
        VIDEO(30, "video", new String[]{".mp4", ".avi", ".flv", ".mov", ".rmvb", ".3pg", ".rm", ".rtsp", ".rtmp", ".qt", ".asf", ".mpeg", ".mpg"}),
        ZIP(40, "zip", new String[]{".zip", ".7z", ".rar"}),
        DOC(50, "doc", new String[]{".doc", ".docx"}),
        TXT(60, "txt", new String[]{".txt", ".json", ".md"}),
        PDF(70, "pdf", new String[]{".pdf"}),
        HTML(80, "html", new String[]{".html"}),
        JAR(90, "jar", new String[]{".jar"}),
        DEX(100, "dex", new String[]{".dex"}),
        UNKNOWN(110, "*", new String[]{".*"}),
        ;
        public int code;
        public String name;
        public String[] suffix;

        FileType(int code, String name, String[] suffix) {
            this.code = code;
            this.name = name;
            this.suffix = suffix;
        }

    }

}
