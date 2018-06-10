//https://www.youtube.com/watch?v=yjRMolYswdQ

package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import model.User;

/**
 * Created by Andressa on 26/05/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String BASE_NAME = "DBApp";
    public static final int BASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, BASE_NAME, null, BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreateTableUser = "CREATE TABLE IF NOT EXISTS user("
                                + 	"idUser INTEGER NOT NULL, "
                                +   "name VARCHAR(140) NOT NULL, "
                                +   "dateOfBirth DATE NOT NULL, "
                                +   "language VARCHAR(200) NOT NULL, "
                                +   "occupation VARCHAR(200) NOT NULL, "
                                +   "email VARCHAR(300) NOT NULL, "
                                +   "password VARCHAR (300) NOT NULL, "
                                +   "localization VARCHAR(300), "
                                +   "statusAccount VARCHAR(50), "
                                +   "PRIMARY KEY(idUser))";

        sqLiteDatabase.execSQL(sqlCreateTableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sqlDropTableUser = "DROP TABLE user";
        sqLiteDatabase.execSQL(sqlDropTableUser);
        onCreate(sqLiteDatabase);
    }
}
