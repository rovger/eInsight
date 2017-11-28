package com.eInsight.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.bson.types.ObjectId;

public class ConvertUtil {
    public static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    public static SerializerFeature[] config = {SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullStringAsEmpty};
    public static SerializerFeature[] config_useClassName = {SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteClassName};
    public static SerializeConfig serializeConfig = initObjectIdSerializerConfig();
    public static String _ID = "_id";
    public static String OBJECT_ID = "objectId";
    public static String GET_OBJECT_ID_METHOD_NAME = "getObjectId";

    public static SerializeConfig initObjectIdSerializerConfig() {
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(ObjectId.class, new ObjectIdSerializer());
        ParserConfig.getGlobalInstance().putDeserializer(ObjectId.class, new ObjectIdDeSerializer());
        return serializeConfig;
    }

    public static String objectToJsonString(Object object, boolean useClassName) {
        String jsonText;
        if (useClassName) {
            jsonText = JSONObject.toJSONString(object, serializeConfig, config_useClassName);
        } else {
            jsonText = objectToJsonString(object);
        }
        return jsonText;
    }

    public static String objectToJsonString(Object object) {
        return JSONObject.toJSONString(object, serializeConfig, config);
    }

    public static <T> T dbObjectToBean(DBObject dbObject, Class<T> clazz) {
        T result = null;
        if (dbObject != null) {
            turnKeyFrom_idToObjectId(dbObject);

            String jsonText = objectToJsonString(dbObject);
            result = JSON.parseObject(jsonText, clazz);
        }
        return result;
    }

    public static void turnKeyFrom_idToObjectId(DBObject dbObject) {
        if (dbObject.containsField(_ID)) {
            Object value = dbObject.removeField(_ID);
            dbObject.put(OBJECT_ID, value);
        }
    }

    public static void turnKeyFromObjectIdTo_id(JSONObject jsonObject, Object bean) {
        if (jsonObject.containsKey(OBJECT_ID)) {
            jsonObject.remove(OBJECT_ID);

            Object objectId = null;
            try {
                Method getObjectIdMethod = bean.getClass().getDeclaredMethod(GET_OBJECT_ID_METHOD_NAME, new Class[0]);
                objectId = getObjectIdMethod.invoke(bean, new Object[0]);
            } catch (Exception localException) {
            }
            if (objectId != null) {
                jsonObject.put(_ID, objectId);
            }
        }
    }

    public static DBObject beanToDBObject(Object bean) {
        DBObject object = null;
        if (isValidBean(bean)) {
            object = new BasicDBObject();
            String jsonText = objectToJsonString(bean, true);
            JSONObject jsonObject = JSON.parseObject(jsonText);

            turnKeyFromObjectIdTo_id(jsonObject, bean);

            object.putAll(jsonObject);
        }
        return object;
    }

    public static boolean isValidBean(Object bean) {
        Validator validator = validatorFactory.getValidator();
        Collection<ConstraintViolation<Object>> errorList = validator.validate(bean, new Class[0]);
        if (!errorList.isEmpty()) {
            String errorMessage = "";
            for (ConstraintViolation<Object> error : errorList) {
                errorMessage = errorMessage + "this value : " + error.getInvalidValue() + ", bean to DBObject validation error!the message : " + error.getMessage() + "\n";
            }
            throw new RuntimeException(errorMessage);
        }
        return true;
    }

    public static String buildResponse(DBObject object, List<DBObject> dbobjectlist) {
        LinkedHashMap<String, Object> result = new LinkedHashMap();
        LinkedHashMap<String, Object> summary = new LinkedHashMap();
        summary.put("queryCondition", object);
        summary.put("count", Integer.valueOf(dbobjectlist.size()));
        result.put("summary", summary);
        result.put("detail", dbobjectlist);
        String response = new GsonBuilder().serializeNulls().registerTypeAdapter(ObjectId.class, new JSONObjectIdAdapter()).create().toJson(result);
        return response;
    }
}
