package com.sloth.pontus.db;

import android.content.Context;
import com.sloth.pontus.dao.DaoMaster;
import com.sloth.pontus.dao.DaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;
import java.io.File;

/**
 * resource db
 */
public class ResourceManagerConnector {

    private volatile DaoSession mDaoSession;
    private volatile ResourceManagerDaoHelper connection;

    public ResourceManagerConnector(Context context, String dbPath) {
        File dbFile = new File(dbPath);
        isFolderExists(dbFile.getParentFile().getAbsolutePath());
        connection = new ResourceManagerDaoHelper(context.getApplicationContext(), dbPath);
        Database db = connection.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }

    /**
     * 检查目录是否存在，如果不存在则创建目录
     *
     * @param folder 目录路径
     * @return true: 目录文件存在 false: 目录不存在
     */
    private boolean isFolderExists(String folder) {
        File file = new File(folder);
        return file.exists() || file.mkdirs();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    public void closeHelper() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    public void closeDaoSession() {
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

}
