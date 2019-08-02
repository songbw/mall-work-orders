package com.fengchao.workorders.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;

public class StringUtil {

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
        if (null == jsonStr || "".equals(jsonStr)) return "";
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

    public static boolean isRightName(String name) {
        if (null == name || name.isEmpty()) {
            return false;
        }

        name = name.trim();
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{1}([a-zA-Z0-9]|[._]){3,19}$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isRightPhone(String phone) {
        if (null == phone || phone.isEmpty()) {
            return false;
        }

        phone = phone.trim();
        Pattern pattern = Pattern.compile("^((\\+86)|(86))?(1)\\d{10}$");
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }

    public static String Date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date String2Date(String stringDate)
                throws ParseException {
        if (null == stringDate) {
            return null;
        }
        if (stringDate.isEmpty()) {
            return null;
        }
        //System.out.println("==String2Date: " + stringDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date returnDate = new Date(1L);
        try {
            Date tmpDate = sdf.parse(stringDate);
            returnDate = tmpDate;
            //System.out.println("== tmpDate: " + tmpDate + " resultDate: " + returnDate);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            returnDate = new Date();
        } finally {
            return returnDate;
        }
    }



}
