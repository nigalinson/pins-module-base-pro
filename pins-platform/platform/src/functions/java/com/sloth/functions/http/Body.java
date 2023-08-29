package com.sloth.functions.http;

import com.sloth.platform.Platform;
import com.sloth.platform.constants.Constants;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.ByteString;

public class Body {
    private static final String TAG = Body.class.getSimpleName();

    public static MultipartBody.Part createFilePart(String name, String path) {
        Platform.log().d(TAG, path);
        File file = new File(path);
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(Constants.ContentTypes.FILE), file);
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }

    public static RequestBody createRequestBody(String json) {
        Platform.log().d(TAG, json);
        return RequestBody.create(MediaType.parse(Constants.ContentTypes.JSON), json);
    }

    public static RequestBody createRequestBody(byte[] json) {
        return RequestBody.create(MediaType.parse(Constants.ContentTypes.JSON), json);
    }

    public static RequestBody createRequestBodyStream(byte[] json) {
        return RequestBody.create(MediaType.parse(Constants.ContentTypes.STREAM), json);
    }

    public static RequestBody createRequestBody(String json, int formatType) {
        if (formatType == 0) {
            return createRequestBody(json);
        } else {
            return createRequestBody(ByteString.encodeUtf8(json).toByteArray());
        }
    }

}
