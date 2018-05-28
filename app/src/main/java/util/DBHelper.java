//https://www.youtube.com/watch?v=yjRMolYswdQ

package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import model.User;
import util.Status;

/**
 * Created by Andressa on 26/05/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String BASE_NAME = "DBApp";
    public static final int BASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, BASE_NAME, null, BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreateTableUser = "CREATE TABLE user("
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

    public void insertUser(User u) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("idUser", u.getIdUser());
        cv.put("name", u.getName());
        cv.put("dateOfBirth", u.getDateOfBirth());
        cv.put("language", u.getLanguage());
        cv.put("occupation", u.getOccupation());
        cv.put("email", u.getEmail());
        cv.put("password", u.getPassword());
        cv.put("localization", u.getLocalization());
        cv.put("statusAccount", u.getStatusAccount().toString());

        db.insert("user", null, cv);

        db.close();
    }

    public User getUser() {
        User u = new User();

        SQLiteDatabase db = getReadableDatabase();

        String sqlGetUser = "SELECT * FROM user";

        Cursor c = db.rawQuery(sqlGetUser, null);

        if(c.moveToFirst()) {
            u.setIdUser(c.getInt(0));
            u.setName(c.getString(1));

            String array[] = new String[3];
            array = c.getString(2).toString().split("-");
            String aux = array[2]+"/"+array[1]+"/"+array[0];
            u.setDateOfBirth(aux);

            u.setLanguage(c.getString(3));
            u.setOccupation(c.getString(4));
            u.setEmail(c.getString(5));
            u.setPassword(c.getString(6));
            u.setLocalization(c.getString(7));
            u.setStatusAccount(Enum.valueOf(Status.class, c.getString(8)));
        }

        return u;
    }
}
