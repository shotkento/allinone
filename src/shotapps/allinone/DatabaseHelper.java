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
    }

    public void createDatabase () throws IOException {
        Log.d("", "uchida createDatabase");
        if (isExist()) {
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
        return true;
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
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
        super.close();
    }

}
