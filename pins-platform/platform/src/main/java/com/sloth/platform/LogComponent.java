package com.sloth.platform;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public interface LogComponent {

    interface LogBuilder {
        String cacheFolder();
        String fileFolder();
    }

    /**
     * 打印debug级别日志
     *
     * @param tag 日志标签
     * @param msg 日志内容
     */
    void d(String tag, String msg);

    /**
     * 打印info级别日志
     *
     * @param tag
     * @param msg
     */
    void v(String tag, String msg);

    /**
     * 打印info级别日志
     *
     * @param tag 日志标签
     * @param msg 日志内容
     */
    void i(String tag, String msg);

    /**
     * 打印warn级别日志
     *
     * @param tag 日志标签
     * @param msg 日志内容
     */
    void w(String tag, String msg);

    /**
     * 打印error级别日志
     *
     * @param tag 日志标签
     * @param msg 日志内容
     */
    void e(String tag, String msg);

    /**
     * 答应error级别的错误堆栈信息
     *
     * @param throwable
     */
    void printStackTrace(String tag, Throwable throwable);

    /**
     * dump缓存中的信息，写入日志
     *
     * @param sync
     */
    void flush(boolean sync);

    /**
     * 终止日志流
     */
    void exit();

    abstract class AbsLog implements LogComponent {
        private static boolean DEBUG_D = true;
        private static boolean DEBUG_V = true;
        private static boolean DEBUG_I = true;
        private static boolean DEBUG_W = true;
        private static boolean DEBUG_E = true;

        protected abstract void debug(String tag, String msg);

        protected abstract void verbose(String tag, String msg);

        protected abstract void info(String tag, String msg);

        protected abstract void warn(String tag, String msg);

        protected abstract void error(String tag, String msg);

        protected void stackTrace(String tag, Throwable throwable){
            if(throwable != null){
                ThrowableReader throwableReader = new ThrowableReader();
                throwable.printStackTrace(new PrintStream(throwableReader));
                error(tag, throwableReader.getFormatMessage());
            }
        }

        public static void setLogLevel(int level) {
            DEBUG_D = level >= 1;
            DEBUG_V = level >= 2;
            DEBUG_I = level >= 3;
            DEBUG_W = level >= 4;
            DEBUG_E = level >= 5;
        }

        @Override
        public void d(String tag, String msg) {
            if (DEBUG_D) {
                debug(tag, msg);
            }
        }

        @Override
        public void v(String tag, String msg) {
            if (DEBUG_V) {
                verbose(tag, msg);
            }
        }

        @Override
        public void i(String tag, String msg) {
            if (DEBUG_I) {
                info(tag, msg);
            }
        }

        @Override
        public void w(String tag, String msg) {
            if (DEBUG_W) {
                warn(tag, msg);
            }
        }

        @Override
        public void e(String tag, String msg) {
            if (DEBUG_E) {
                error(tag, msg);
            }
        }

        @Override
        public void printStackTrace(String tag, Throwable throwable) {
            if (DEBUG_E) {
                stackTrace(tag, throwable);
            }
        }
    }

    class ThrowableReader extends OutputStream {

        private static final int SIZE = 1024;

        private final StringBuilder stringBuilder;

        private final byte[] reader;

        private int index = -1;

        public ThrowableReader() {
            stringBuilder = new StringBuilder();
            reader = new byte[SIZE];
        }

        @Override
        public void write(int b) throws IOException {
            if (++index >= SIZE) {
                stringBuilder.append(new String(reader));
                index = 0;
            }
            reader[index] = (byte) b;
        }

        public String getFormatMessage() {
            if (index != -1) {
                stringBuilder.append(new String(reader, 0, index + 1));
                index = -1;
            }
            return stringBuilder.toString();
        }

    }

}
