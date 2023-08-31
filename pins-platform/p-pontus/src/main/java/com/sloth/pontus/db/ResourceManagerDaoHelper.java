package com.sloth.pontus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.sloth.platform.Platform;
import com.sloth.pontus.dao.DaoMaster;
import org.greenrobot.greendao.database.Database;

/**
 * 资源管理数据连接
 */
class ResourceManagerDaoHelper extends DaoMaster.DevOpenHelper {
    private static final String TAG = ResourceManagerDaoHelper.class.getSimpleName();

    ResourceManagerDaoHelper(Context context, String dbPath) {
        super(context, dbPath);
    }

    ResourceManagerDaoHelper(Context context, SQLiteDatabase.CursorFactory factory, String dbPath) {
        super(context, dbPath, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Platform.log().d(TAG, "数据库结构升级");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    private void insertColumn(Database db, String table, String columnName) {
        try {
            Platform.log().d(TAG, "表：" + table + "，新增字段：" + columnName);
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + columnName + " TEXT;");
        } catch (Exception e) {
            Platform.log().d(TAG, "已经存在列:" + columnName);
        }
    }

}
