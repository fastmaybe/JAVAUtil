package com.secmask.util.tool;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wd
 * @Program DBMaskerServer
 * @create 2018-05-21 16:06
 */
public class BeanUtil {

    public static <T> List<T> mapListToObjModle(T entity, List<Map<String,Object>> params) {
        List<T> list = new ArrayList<T>();
        for(Map<String,Object> m : params) {
            list.add(mapToObjModle(entity,m));
        }
        return list;
    }

    /**
     * map转对象方法
     *
     * @param entity
     * @param params
     * @return
     */
    public static <T> T mapToObjModle(T entity, Map<String, Object> params) {
        T t = null;
        try {
            // 得到对象的字段
            t = (T)entity.getClass().newInstance();
            BeanUtils.copyProperties(entity,t);
            List<Field> fields = getAccessibleFields(t.getClass());
            // 迭代字段
            for (Field f : fields) {
                String name = f.getName();
                Object objVal = params.get(name);
                // 找到对应值，进行转化设置
                if (objVal != null) {
                    if (f.getType().equals(String.class)) {
                        objVal = String.valueOf(objVal).trim();
                    } else if (!f.getType().isAssignableFrom(objVal.getClass())) {
                        if (StringUtils.isNotBlank(String.valueOf(objVal))) {
                            //转换依赖方法：org.apache.commons.beanutils.ConvertUtils.convert(Object, Class<?>)
                            objVal = ConvertUtils.convert(objVal, f.getType());
                        } else {
                            objVal = null;
                        }
                    }
                    f.set(t, objVal);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 循环向上转型, 获取对象所有的DeclaredField
     *
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static List<Field> getAccessibleFields(final Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            for (Field f : superClass.getDeclaredFields()) {
                boolean hasInSubClass = false;
                for (Field f2 : fields) {
                    if (f2.getName().equals(f.getName())) {
                        hasInSubClass = true;
                        break;
                    }
                }
                if (!hasInSubClass) {
                    makeAccessible(f);
                    fields.add(f);
                }
            }
        }
        return fields;
    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    private static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
