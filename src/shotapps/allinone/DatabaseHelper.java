package shotapps.allinone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = "DatabaseHelper";

    private static final String DB_NAME = "allinone";
    private static final String DB_NAME_ASSET = "allinone.db";
    private static final int VERSION = 1;

    private SQLiteDatabase mDatabase;
    private final Context mContext;
    private final File mDatabasePath;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
        mDatabasePath = mContext.getDatabasePath(DB_NAME);
        Log.d(TAG, "DB path is " + mDatabasePath);
    }

    public void createDatabase() throws IOException {
        Log.d(TAG, "Create DB");
        Log.d(TAG, "CD DB path is " + mDatabasePath);
        if (!isExist()) {
            // DBがないので作成する
            Log.d(TAG, "DB is not exist!!");
            getReadableDatabase();
            try {
                copyDatabaseFromAsset();

                String dbPath = mDatabasePath.getAbsolutePath();
                SQLiteDatabase checkDb = null;
                try {
                    checkDb = SQLiteDatabase.openDatabase(dbPath, null,
                            SQLiteDatabase.OPEN_READWRITE);
                } catch (SQLiteException e) {
                    Log.e(TAG, "Failed to open DB");
                }
                if (checkDb != null) {
                    Log.d(TAG, "DB is not null");
                    checkDb.setVersion(VERSION);
                    checkDb.close();
                }
            } catch (IOException e) {
                throw new Error("Error while copying DB");
            }
        } else {
            // 既にDBは作成されてる
            Log.d(TAG, "DB is exist");
        }
    }

    /**
     * 再コピーを防止するために、すでにデータベースがあるかどうか判定する
     *
     * @return 存在している場合 {@code true}
     */
    private boolean isExist() {
        String dbPath = mDatabasePath.getAbsolutePath();

        SQLiteDatabase checkDb = null;
        try {
            checkDb = SQLiteDatabase.openDatabase(dbPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // DBはまだ存在していない
        }

        if (checkDb == null) {
            // DBはまだ存在していない
            return false;
        }

        int oldVersion = checkDb.getVersion();
        int newVersion = VERSION;

        if (oldVersion == newVersion) {
            // DBは存在していて最新
            checkDb.close();
            return true;
        }

        // DBが存在していて最新ではないので削除
        File f = new File(dbPath);
        f.delete();
        return false;
    }

    /**
     * asset に格納したデーだベースをデフォルトのデータベースパスに作成したからのデータベースにコピーする
     */
    private void copyDatabaseFromAsset() throws IOException {
        // asset内のデータベースファイルにアクセス
        InputStream input = mContext.getAssets().open(DB_NAME_ASSET);

        // デフォルトのデータベースパスに作成した空のDB
        OutputStream output = new FileOutputStream(mDatabasePath);

        // コピー
        byte[] buffer = new byte[1024];
        int size;
        while ((size = input.read(buffer)) > 0) {
            output.write(buffer, 0, size);
        }
        output.flush();
        output.close();
        input.close();
    }

    public SQLiteDatabase openDatabase() throws SQLException {
        return getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "uchida onCreate!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
        super.close();
    }

}
