package com.gome.iuv.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.gome.iuv.utils.DesUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by xjd on 2017/7/10.
 */
public class MyFastJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements GenericHttpMessageConverter<Object> {
    private Charset charset = Charset.forName("UTF-8");
    /** @deprecated */
    @Deprecated
    protected SerializerFeature[] features = new SerializerFeature[0];
    /** @deprecated */
    @Deprecated
    protected SerializeFilter[] filters = new SerializeFilter[0];
    /** @deprecated */
    @Deprecated
    protected String dateFormat;
    private FastJsonConfig fastJsonConfig = new FastJsonConfig();

    public FastJsonConfig getFastJsonConfig() {
        return this.fastJsonConfig;
    }

    public void setFastJsonConfig(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
    }

    public MyFastJsonHttpMessageConverter() {
        super(MediaType.ALL);
    }

    /** @deprecated */
    @Deprecated
    public Charset getCharset() {
        return this.fastJsonConfig.getCharset();
    }

    /** @deprecated */
    @Deprecated
    public void setCharset(Charset charset) {
        this.fastJsonConfig.setCharset(charset);
    }

    /** @deprecated */
    @Deprecated
    public String getDateFormat() {
        return this.fastJsonConfig.getDateFormat();
    }

    /** @deprecated */
    @Deprecated
    public void setDateFormat(String dateFormat) {
        this.fastJsonConfig.setDateFormat(dateFormat);
    }

    /** @deprecated */
    @Deprecated
    public SerializerFeature[] getFeatures() {
        return this.fastJsonConfig.getSerializerFeatures();
    }

    /** @deprecated */
    @Deprecated
    public void setFeatures(SerializerFeature... features) {
        this.fastJsonConfig.setSerializerFeatures(features);
    }

    /** @deprecated */
    @Deprecated
    public SerializeFilter[] getFilters() {
        return this.fastJsonConfig.getSerializeFilters();
    }

    /** @deprecated */
    @Deprecated
    public void setFilters(SerializeFilter... filters) {
        this.fastJsonConfig.setSerializeFilters(filters);
    }

    /** @deprecated */
    @Deprecated
    public void addSerializeFilter(SerializeFilter filter) {
        if(filter != null) {
            int length = this.fastJsonConfig.getSerializeFilters().length;
            SerializeFilter[] filters = new SerializeFilter[length + 1];
            System.arraycopy(this.fastJsonConfig.getSerializeFilters(), 0, filters, 0, length);
            filters[filters.length - 1] = filter;
            this.fastJsonConfig.setSerializeFilters(filters);
        }
    }

    protected boolean supports(Class<?> clazz) {
        return true;
    }

    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream in = inputMessage.getBody();
        return JSON.parseObject(in, this.fastJsonConfig.getCharset(), clazz, this.fastJsonConfig.getFeatures());
    }

    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        ByteArrayOutputStream outnew = new ByteArrayOutputStream();
        boolean writeAsToString = false;
        String len;
        if(obj != null) {
            len = obj.getClass().getName();
            if("com.fasterxml.jackson.databind.node.ObjectNode".equals(len) || "java.lang.String".equals(len)) {
                writeAsToString = true;
            }
        }

        OutputStream out;
        if(writeAsToString) {
            len = obj.toString();
            out = outputMessage.getBody();
            out.write(len.getBytes());
            if(this.fastJsonConfig.isWriteContentLength()) {
                headers.setContentLength((long)len.length());
            }
        } else {
            int len1 = JSON.writeJSONString(outnew, this.fastJsonConfig.getCharset(), obj, this.fastJsonConfig.getSerializeConfig(), this.fastJsonConfig.getSerializeFilters(), this.fastJsonConfig.getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, this.fastJsonConfig.getSerializerFeatures());
            if(this.fastJsonConfig.isWriteContentLength()) {
                headers.setContentLength((long)len1);
            }

            out = outputMessage.getBody();
            outnew.writeTo(out);
        }

        outnew.close();
    }

    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return super.canRead(contextClass, mediaType);
    }

    public boolean canWrite(Type type, Class<?> contextClass, MediaType mediaType) {
        return super.canWrite(contextClass, mediaType);
    }

    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream in = inputMessage.getBody();
        String inStr = "";
        try {
            //解密
            inStr = DesUtil.decrypt3DES(IOUtils.toString(in));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.parseObject(IOUtils.toInputStream(inStr), this.fastJsonConfig.getCharset(), type, this.fastJsonConfig.getFeatures());
    }

    public void write(Object t, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        String tStr = t.toString();
        try {
            tStr = DesUtil.encrypt3DES(tStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        t = tStr;
        if(headers.getContentType() == null) {
            if(contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                contentType = this.getDefaultContentType(t);
            }

            if(contentType != null) {
                headers.setContentType(contentType);
            }
        }

        if(headers.getContentLength() == -1L) {
            Long contentLength = this.getContentLength(t, headers.getContentType());
            if(contentLength != null) {
                headers.setContentLength(contentLength.longValue());
            }
        }

        this.writeInternal(t, outputMessage);
        outputMessage.getBody().flush();
    }
}
