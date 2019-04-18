package com.secmask.util.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.secmask.util.constant.DatabaseType;

public class DbFieldUtil {

	public static final List<String> keywords = Arrays.asList( "AS", "BY", "IS", "IN", "OR", "ON", "ALL", "AND", "NOT", "SET", "ASC",
			"TOP", "END", "DESC", "INTO", "LIKE", "ILIKE", "DROP", "JOIN", "LEFT", "CROSS", "FROM", "CASE", "WHEN",
			"THEN", "ELSE", "SOME", "FULL", "WITH", "TABLE", "VIEW", "WHERE", "FOR", "PIVOT", "USING", "UNION", "GROUP",
			"BEGIN", "INDEX", "INNER", "LIMIT", "OUTER", "ORDER", "RIGHT", "DELETE", "CREATE", "SELECT", "OFFSET",
			"EXISTS", "HAVING", "INSERT", "UPDATE", "ESCAPE", "PRIMARY", "FULLTEXT", "NATURAL", "BETWEEN", "DISTINCT",
			"INTERSECT", "EXCEPT", "MINUS", "LATERAL", "INTERVAL", "FOREIGN", "CONSTRAINT", "REFERENCES", "CHARACTER",
			"VARYING", "START", "CONNECT", "NOCYCLE", "ALTER", "ADD", "UNBOUNDED", "PRECEDING", "CURRENT", "RETURNING",
			"BINARY", "REGEXP", "UNLOGGED", "EXEC", "EXECUTE", "FETCH", "NEXT", "ONLY", "COMMIT", "UNIQUE", "WITHIN",
			"IF", "RECURSIVE", "OF", "KEEP", "GROUP_CONCAT", "SKIP", "MERGE", "MATCHED", "RESTRICT", "DUPLICATE",
			"LOW_PRIORITY", "DELAYED", "HIGH_PRIORITY", "IGNORE", "<S_DOUBLE>", "<S_LONG>", "<DIGIT>", "<S_HEX>",
			"<HEX_VALUE>", "<LINE_COMMENT>", "<MULTI_LINE_COMMENT>", "<S_IDENTIFIER>", "<LETTER>", "<PART_LETTER>",
			"<S_CHAR_LITERAL>", "<S_QUOTED_IDENTIFIER>" );

	public static String convertKeyword(String columnName, String dbType) {
		String convertedColumnName = columnName;
		boolean isKeyword = false;
		if(StringUtils.isNotBlank(columnName)) {
			for(String keyword : keywords) {
				if(StringUtils.equalsIgnoreCase(columnName, keyword)) {
					isKeyword = true;
					break;
				}
			}
		}
		if(isKeyword) {
			switch (dbType) {
				case DatabaseType.MYSQL:
					convertedColumnName = "`" + columnName + "`";
					break;
				case DatabaseType.DB2:
				case DatabaseType.POSTGRESQL:
				case DatabaseType.ORACLE:
					convertedColumnName = "\"" + columnName + "\"";
					break;
				case DatabaseType.SQLSERVER:
					convertedColumnName = "[" + columnName + "]";
					break;
				default:
					break;
			}
		}
		return convertedColumnName;
	}
	
	public static String removeKeywordMark(String columnName, String dbType) {
		String removedMarkColumnName = columnName;
		switch (dbType) {
			case DatabaseType.MYSQL:
				if(columnName.startsWith("`") && columnName.endsWith("`")) {
					removedMarkColumnName = columnName.replaceAll("`", "");
				}
				break;
			case DatabaseType.DB2:
			case DatabaseType.POSTGRESQL:
			case DatabaseType.ORACLE:
				if(columnName.startsWith("\"") && columnName.endsWith("\"")) {
					removedMarkColumnName = columnName.replaceAll("\"", "");
				}
				break;
			case DatabaseType.SQLSERVER:
				if(columnName.startsWith("[") && columnName.endsWith("]")) {
					removedMarkColumnName = columnName.replaceAll("\\[", "").replaceAll("]", "");
				}
				break;
			default:
				break;
		}
		return removedMarkColumnName;
	}
	
	public static List<String> removeKeywordMark(List<String> columnNames, String dbType){
		List<String> removedMarkColumnNames = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(columnNames)) {
			for(String columnName : columnNames) {
				removedMarkColumnNames.add(removeKeywordMark(columnName, dbType));
			}
		}
		return removedMarkColumnNames;
	}
	
	public static void main(String[] args) {
		System.out.println(new Date());
	}
	
}
