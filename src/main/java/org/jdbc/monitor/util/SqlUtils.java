package org.jdbc.monitor.util;

import lombok.extern.slf4j.Slf4j;
import org.jdbc.monitor.common.Constant;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shi rui
 * @create: 2018-12-25 17:44
 */
@Slf4j
public class SqlUtils {

    private static final String SQL_KEYWORD_SELECT = "SELECT";
    private static final String SQL_KEYWORD_WHERE = "WHERE";
    private static final String SQL_KEYWORD_AND = "AND";
    private static final char SQL_KEYWORD_LEFT_PARENTHESIS = '(';
    private static final char SQL_KEYWORD_RIGHT_PARENTHESIS = ')';
    private static final String PARENTHESIS_SUBSTITUTE = "^_^";
    private static final String[] opers = new String[]{"=", "!=", ">", ">=", "<", "<="};
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '\"';
    private static final String REPLACE_VALUE_CHAR = " ? ";

    /**
     * 格式化 sql,
     * 暂时只支持一个层级的（）条件
     * @param sql
     * @return
     */
    public static String getQuerySql(String sql){
        Assert.notNull(sql,"sql语句为空");
        String tmpSql = sql.toUpperCase();
        //非查询sql
        if(tmpSql.indexOf(SQL_KEYWORD_SELECT) < 0){
            return sql;
        }
        //没有where条件
        if(tmpSql.indexOf(SQL_KEYWORD_WHERE) < 0){
            return tmpSql;
        }
        int firstWhereIndex = tmpSql.indexOf(SQL_KEYWORD_WHERE);
        String[] sqlArray = new String[]{tmpSql.substring(0,firstWhereIndex),tmpSql.substring(firstWhereIndex + SQL_KEYWORD_WHERE.length())};
        String whereSql = getWhereSqlNoParameter(sqlArray[1]);
        return sqlArray[0] + " " + SQL_KEYWORD_WHERE + " " + whereSql;
    }

    private static String getWhereSqlNoParameter(String whereSql){
        String tmpSql;
        String[] replaceWhereSql = null;
        if(whereSql.indexOf(SQL_KEYWORD_LEFT_PARENTHESIS) >= 0){
            replaceWhereSql = replaceParenthesisContent(whereSql);
            tmpSql = replaceWhereSql[0];
        }else {
            tmpSql = whereSql;
        }
        StringBuilder stringBuilder = new StringBuilder();

        String[] conditions = tmpSql.split(SQL_KEYWORD_AND);
        for(int i=0;i<conditions.length;i++){
            if(i > 0){
                stringBuilder.append(" "+SQL_KEYWORD_AND + " ");
            }
            stringBuilder.append(getReplaceCondition(conditions[i]));
        }

        if(replaceWhereSql != null){
            for(int i = 1;i < replaceWhereSql.length;i++){
                StringBuilder replaceStr = new StringBuilder();
                replaceStr.append(SQL_KEYWORD_LEFT_PARENTHESIS);
                String repStr = replaceWhereSql[i].substring(1,replaceWhereSql[i].length()-1);
                replaceStr.append(getWhereSqlNoParameter(repStr));
                replaceStr.append(SQL_KEYWORD_RIGHT_PARENTHESIS);
                int replaceIndex = stringBuilder.indexOf(PARENTHESIS_SUBSTITUTE);
                stringBuilder.replace(replaceIndex, replaceIndex+PARENTHESIS_SUBSTITUTE.length(),replaceStr.toString());
            }
        }

        return stringBuilder.toString();
    }

    private static String getReplaceCondition(String condition){
        if(condition.indexOf(SINGLE_QUOTE) >= 0){
            return replaceQuote(condition,SINGLE_QUOTE);
        }
        if(condition.indexOf(DOUBLE_QUOTE) >= 0){
            return replaceQuote(condition,DOUBLE_QUOTE);
        }
        if(condition.equals(PARENTHESIS_SUBSTITUTE)){
            return condition;
        }
        for(String oper:opers){
            if(condition.contains(oper)){
                String[] con = condition.split(oper);
                if(isNumeric(con[1].trim())){
                    return condition.replace(con[1],REPLACE_VALUE_CHAR);
                }
            }
        }
        return condition;
    }

    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '.'){
                return false;
            }
        }
        return true;
    }

    private static String replaceQuote(String str,char quote){
        int start = 0;
        char[] strChar = str.toCharArray();
        for(int i = 0;i < strChar.length; i++){
            char c = strChar[i];
            if(c == quote){
                if(start == 0){
                    start = i;
                }else{
                    return str.replace(str.substring(start,i+1),REPLACE_VALUE_CHAR);
                }
                continue;
            }
        }
        return str;
    }

    /**
     *
     * @param str 原始字符串
     * @return 第一个元素为被替换后的字符串
     */
    private static String[] replaceParenthesisContent(String str){
        int leftCount = 0, rightCount = 0, start = 0;
        char[] strChar = str.toCharArray();
        List<String> repleadedStr = new ArrayList<>();
        for(int i = 0;i < strChar.length; i++){
            char c = strChar[i];
            if(c == SQL_KEYWORD_LEFT_PARENTHESIS){
                if(leftCount == 0){
                    start = i;
                }
                leftCount++;
                continue;
            }
            if(c == SQL_KEYWORD_RIGHT_PARENTHESIS){
                rightCount ++;
                if(rightCount == leftCount){
                    String replaceSr = str.substring(start,i+1);
                    repleadedStr.add(replaceSr);
                    leftCount = 0;
                    rightCount = 0;
                    start = 0;
                }
            }
        }
        for(String repStr:repleadedStr){
            str = str.replace(repStr,PARENTHESIS_SUBSTITUTE);
        }
        repleadedStr.add(0,str);
        return repleadedStr.toArray(new String[repleadedStr.size()]);
    }

    public static boolean isSelect(String sql){
        return sql.toUpperCase().startsWith(Constant.SQL_SELECT_PREFIX);
    }

    public static int getQueryCount(ResultSet resultSet){
        int row = 0;
        try{
            resultSet.last();
            row = resultSet.getRow();
            resultSet.beforeFirst();
        }catch (Exception e){
            log.error("统计查询条数异常:",e);
        }
        return row;
    }

    public static void main(String[] args){
        String sql = "select * from purchase_order_info_0 p join purchase_order_task_0 t on t.OrderId = p.id \n" +
                "where t.`Status` = 100 and t.OrderId in (select id from purchase_order_snapshot_0 s where s.orderId = t.OrderId) and p.CreateTime > '2018-12-23' and id in(10,11,12) ";
        System.out.println(getQuerySql(sql));
    }




}
