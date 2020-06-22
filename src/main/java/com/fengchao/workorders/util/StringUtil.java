package com.fengchao.workorders.util;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class StringUtil {

    public static final String DEFAULT_DATA_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String exceptionDetail(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return "\n" + writer.toString();
    }

    public static void throw400Exp(HttpServletResponse response, String msg)
            throws RuntimeException {
        response.setStatus(MyErrorMap.e400.getCode());
        response.setContentType("application/json;charset=UTF-8");
        throw new RuntimeException(msg.trim());
    }

    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) { return "";}
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    public static String camelToUnderline(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toUpperCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成大写
                result.append(s);
            }
        }
        return result.toString().toLowerCase();
    }

    public static String Date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATA_TIME_PATTERN);
        return sdf.format(date);
    }

    public static String Date2String(LocalDateTime dateTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DEFAULT_DATA_TIME_PATTERN);
        return df.format(dateTime);
    }

    public static Date string2DateNew(String stringDate) {
        if (null == stringDate || stringDate.isEmpty()) {
            return null;
        }

        int stringSize = stringDate.length();
        if (14 == stringSize){
            StringBuilder sb = new StringBuilder();
            sb.append(stringDate.substring(0,4));
            sb.append("-");
            sb.append(stringDate.substring(4,6));
            sb.append("-");
            sb.append(stringDate.substring(6,8));
            sb.append(" ");
            sb.append(stringDate.substring(8,10));
            sb.append(":");
            sb.append(stringDate.substring(10,12));
            sb.append(":");
            sb.append(stringDate.substring(12));
            stringDate = sb.toString();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date returnDate = null;
        try {
            returnDate = sdf.parse(stringDate);
        } catch (ParseException ex) {
            log.error(ex.getMessage(),ex);
        }

        return returnDate;
    }

    public static LocalDateTime String2Date(String stringDate) {
        if (null == stringDate) {
            return null;
        }
        if (stringDate.isEmpty()) {
            return null;
        }
        //log.debug("==String2Date: " + stringDate);
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DEFAULT_DATA_TIME_PATTERN);
        LocalDateTime returnDate = LocalDateTime.parse(stringDate,df);

        return returnDate;

    }

    public static String getTimeStampRandomStr(){
        Long timeStampMs = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long timeStampS = timeStampMs/1000;
        String timeStamp = timeStampS.toString();
        Random random = new Random();

        String triRandom = random.nextInt(1000) + "";
        StringBuilder sb = new StringBuilder();
        int randLength = triRandom.length();
        if (randLength < 3) {
            for (int i = 1; i <= 3 - randLength; i++) {
                sb.append("0");
            }
        }
        sb.append(triRandom);
        return timeStamp + sb.toString();

    }

}
