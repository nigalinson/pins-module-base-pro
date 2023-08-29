package com.sloth.functions.http;

import com.sloth.platform.Platform;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

public class PlatformLogInterceptor implements Interceptor {
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private final Logger logger;
    private volatile Level level;

    public PlatformLogInterceptor() {
        this(Logger.DEFAULT);
    }

    public PlatformLogInterceptor(Logger logger) {
        this.level = Level.NONE;
        this.logger = logger;
    }

    public PlatformLogInterceptor setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        } else {
            this.level = level;
            return this;
        }
    }

    public Level getLevel() {
        return this.level;
    }

    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;
        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        } else {
            boolean logBody = level == Level.BODY;
            boolean logHeaders = logBody || level == Level.HEADERS;
            RequestBody requestBody = request.body();
            boolean hasRequestBody = requestBody != null;
            Connection connection = chain.connection();
            Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
            String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            if (!logHeaders && hasRequestBody) {
                requestStartMessage = requestStartMessage + " (" + requestBody.contentLength() + "-byte body)";
            }

            this.logger.log(requestStartMessage);
            if (logHeaders) {
                if (hasRequestBody) {
                    if (requestBody.contentType() != null) {
                        this.logger.log("Content-Type: " + requestBody.contentType());
                    }

                    if (requestBody.contentLength() != -1L) {
                        this.logger.log("Content-Length: " + requestBody.contentLength());
                    }
                }

                Headers headers = request.headers();
                int i = 0;

                for(int count = headers.size(); i < count; ++i) {
                    String name = headers.name(i);
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        this.logger.log(name + ": " + headers.value(i));
                    }
                }

                if (logBody && hasRequestBody) {
                    if (this.bodyEncoded(request.headers())) {
                        this.logger.log("--> END " + request.method() + " (encoded body omitted)");
                    } else {
                        Buffer buffer = new Buffer();
                        requestBody.writeTo(buffer);
                        Charset charset = UTF8;
                        MediaType contentType = requestBody.contentType();
                        if (contentType != null) {
                            charset = contentType.charset(UTF8);
                        }

                        this.logger.log("");
                        if (isPlaintext(buffer)) {
                            this.logger.log(buffer.readString(charset));
                            this.logger.log("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)");
                        } else {
                            this.logger.log("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)");
                        }
                    }
                } else {
                    this.logger.log("--> END " + request.method());
                }
            }

            long startNs = System.nanoTime();

            Response response;
            try {
                response = chain.proceed(request);
            } catch (Exception var25) {
                this.logger.log("<-- HTTP FAILED: " + var25);
                throw var25;
            }

            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            String bodySize = contentLength != -1L ? contentLength + "-byte" : "unknown-length";
            this.logger.log("<-- " + response.code() + ' ' + response.message() + ' ' + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');
            if (logHeaders) {
                Headers headers = response.headers();
                int i = 0;

                for(int count = headers.size(); i < count; ++i) {
                    this.logger.log(headers.name(i) + ": " + headers.value(i));
                }

                if (logBody && HttpHeaders.hasBody(response)) {
                    if (this.bodyEncoded(response.headers())) {
                        this.logger.log("<-- END HTTP (encoded body omitted)");
                    } else {
                        BufferedSource source = responseBody.source();
                        source.request(9223372036854775807L);
                        Buffer buffer = source.buffer();
                        Charset charset = UTF8;
                        MediaType contentType = responseBody.contentType();
                        if (contentType != null) {
                            charset = contentType.charset(UTF8);
                        }

                        if (!isPlaintext(buffer)) {
                            this.logger.log("");
                            this.logger.log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                            return response;
                        }

                        if (contentLength != 0L) {
                            this.logger.log("");
                            this.logger.log(buffer.clone().readString(charset));
                        }

                        this.logger.log("<-- END HTTP (" + buffer.size() + "-byte body)");
                    }
                } else {
                    this.logger.log("<-- END HTTP");
                }
            }

            return response;
        }
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64L ? buffer.size() : 64L;
            buffer.copyTo(prefix, 0L, byteCount);

            for(int i = 0; i < 16 && !prefix.exhausted(); ++i) {
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }

            return true;
        } catch (EOFException var6) {
            return false;
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    public interface Logger {
        Logger DEFAULT = message -> Platform.log().i("api", message);

        void log(String var1);
    }

    public static enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY;

        private Level() {
        }
    }
}
