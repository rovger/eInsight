package com.eInsight.common.utils;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;

import org.bson.types.ObjectId;

public class ObjectIdDeSerializer implements ObjectDeserializer {
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONLexer lexer = parser.getLexer();

        T result = null;
        if (lexer.token() == 4) {
            String objectIdString = lexer.stringVal();
            result = (T) new ObjectId(objectIdString);

            lexer.nextToken();
        } else if (lexer.token() == 8) {
            lexer.nextToken();
        }
        return result;
    }

    public int getFastMatchToken() {
        return 0;
    }
}
