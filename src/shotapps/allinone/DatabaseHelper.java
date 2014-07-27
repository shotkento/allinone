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
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = "DatabaseHelper";

    private static final String DB_NAME = "allinone";
    private static final String DB_NAME_ASSET = "allinone.db";
    private static final int VERSION = 1;

    private final String CREATE_TRAINING = "CREATE TABLE training (_id integer primary key not null, count integer, correct integer)";
    private final String CREATE_WORD = "CREATE TABLE word (_id integer primary key not null, word_en text, word_jp text, count integer, correct integer)";
    private final String INSERT_TRAINING = "INSERT INTO training(_id, count, correct) VALUES(?, ?, ?)";

    private SQLiteDatabase mDatabase;
    private final Context mContext;
    private final File mDatabasePath;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
        mDatabasePath = mContext.getDatabasePath(DB_NAME);
    }

    public void createDatabase () throws IOException {
        Log.d("", "uchida createDatabase");
        if (!isExist()) {
            Log.d("", "uchida !isExist()");
            getReadableDatabase();
            try {
                copyDatabaseFromAsset();

                String dbPath = mDatabasePath.getAbsolutePath();
                SQLiteDatabase checkDb = null;
                try {
                    checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
                } catch (SQLiteException e) {
                }
                if (checkDb != null) {
                    checkDb.setVersion(VERSION);
                    checkDb.close();
                }
            } catch (IOException e) {
                throw new Error("Error while copying database");
            }
        }
    }

    private boolean isExist() {
        String dbPath = mDatabasePath.getAbsolutePath();

        SQLiteDatabase checkDb = null;
        try {
            checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }

        if (checkDb == null) {
            return false;
        }

        int oldVersion = checkDb.getVersion();
        int newVersion = VERSION;

        if (oldVersion == newVersion) {
            // データベースは存在していて最新
            checkDb.close();
            return true;
        }

        // データベースが存在していて最新ではないので削除
        File f = new File(dbPath);
        f.delete();
        return false;
    }

    private void copyDatabaseFromAsset() throws IOException {
        InputStream input = mContext.getAssets().open(DB_NAME_ASSET);

        OutputStream output = new FileOutputStream(mDatabasePath);

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
        db.execSQL(CREATE_TRAINING);
        db.execSQL(CREATE_WORD);

        db.beginTransaction();
        try {
            SQLiteStatement sql = db.compileStatement(INSERT_TRAINING);
            for (int i = 1; i <= 419; i++) {
                sql.bindLong(1, i);
                sql.bindLong(2, 0);
                sql.bindLong(3, 0);
                sql.executeInsert();
            }
            Log.d(TAG, "transaction successful!");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
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
